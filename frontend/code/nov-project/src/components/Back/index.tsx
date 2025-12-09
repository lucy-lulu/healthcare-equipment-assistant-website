import { useEffect, useRef } from 'react';

//reference: https://github.com/tipsy/bubbly-bg.git

export type BubblyOptions = {
  canvas?: HTMLCanvasElement;
  compose?: GlobalCompositeOperation;
  bubbles?: {
    count?: number;
    radius?: () => number;
    fill?: () => string;
    angle?: () => number;
    velocity?: () => number;
    shadow?: () => { blur: number; color: string } | null;
    stroke?: () => { width: number; color: string } | null;
    objectCreator?: () => Bubble;
  };
  background?: (
    ctx: CanvasRenderingContext2D
  ) => string | CanvasGradient | CanvasPattern;
  animate?: boolean;
};

type Bubble = {
  r: number;
  f: string;
  x: number;
  y: number;
  a: number;
  v: number;
  sh: { blur: number; color: string } | null;
  st: { width: number; color: string } | null;
  draw: (ctx: CanvasRenderingContext2D, bubble: Bubble) => void;
};

function createBubbly(userConfig: BubblyOptions = {}) {
  // 1) 画布
  const cv =
    userConfig.canvas ??
    (() => {
      const canvas = document.createElement('canvas');
      canvas.style.position = 'fixed';
      canvas.style.zIndex = '-1';
      canvas.style.left = '0';
      canvas.style.top = '0';
      canvas.style.width = '100vw';
      canvas.style.height = '100vh';
      document.body.appendChild(canvas);
      return canvas;
    })();

  const ctx = cv.getContext('2d');
  if (!ctx) throw new Error('Canvas 2D context not available');

  const compose = userConfig.compose ?? 'lighter';
  const animate = userConfig.animate !== false;
  const background = userConfig.background ?? (() => '#2AE');

  const bubbles = Object.assign(
    {
      count: Math.floor((cv.clientWidth + cv.clientHeight) * 0.02),
      radius: () => 4 + (Math.random() * window.innerWidth) / 25,
      fill: () => `hsla(0,0%,100%,${Math.random() * 0.1})`,
      angle: () => Math.random() * Math.PI * 2,
      velocity: () => 0.1 + Math.random() * 0.5,
      shadow: () => null as Bubble['sh'],
      stroke: () => null as Bubble['st'],
    },
    userConfig.bubbles ?? {}
  );

  const applySize = () => {
    const dpr = Math.max(1, window.devicePixelRatio || 1);
    const w = cv.clientWidth || window.innerWidth;
    const h = cv.clientHeight || window.innerHeight;
    cv.width = Math.max(1, Math.floor(w * dpr));
    cv.height = Math.max(1, Math.floor(h * dpr));
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
  };
  if (!userConfig.canvas) {
    cv.style.width = '100vw';
    cv.style.height = '100vh';
  }
  applySize();

  const objectCreator =
    userConfig.bubbles?.objectCreator ??
    (() =>
      ({
        r: bubbles.radius(),
        f: bubbles.fill(),
        x: Math.random() * (cv.clientWidth || window.innerWidth),
        y: Math.random() * (cv.clientHeight || window.innerHeight),
        a: bubbles.angle(),
        v: bubbles.velocity(),
        sh: bubbles.shadow(),
        st: bubbles.stroke(),
        draw: (c: CanvasRenderingContext2D, b: Bubble) => {
          if (b.sh) {
            c.shadowColor = b.sh.color;
            c.shadowBlur = b.sh.blur;
          } else {
            c.shadowColor = 'transparent';
            c.shadowBlur = 0;
          }
          c.fillStyle = b.f;
          c.beginPath();
          c.arc(b.x, b.y, b.r, 0, Math.PI * 2);
          c.fill();
          if (b.st) {
            c.lineWidth = b.st.width;
            c.strokeStyle = b.st.color;
            c.stroke();
          }
        },
      } as Bubble));

  let bubbleArray: Bubble[] = Array.from(
    { length: bubbles.count },
    objectCreator
  );

  let rafId = 0;
  const draw = () => {
    ctx.globalCompositeOperation = 'source-over';
    ctx.fillStyle = background(ctx) as any;
    ctx.fillRect(0, 0, cv.width, cv.height);

    ctx.globalCompositeOperation = compose;

    const w = cv.clientWidth || window.innerWidth;
    const h = cv.clientHeight || window.innerHeight;

    for (const b of bubbleArray) {
      b.draw(ctx, b);
      b.x += Math.cos(b.a) * b.v;
      b.y += Math.sin(b.a) * b.v;

      if (b.x - b.r > w) b.x = -b.r;
      if (b.x + b.r < 0) b.x = w + b.r;
      if (b.y - b.r > h) b.y = -b.r;
      if (b.y + b.r < 0) b.y = h + b.r;
    }

    if (animate) rafId = requestAnimationFrame(draw);
  };

  const onResize = () => {
    const oldW = cv.clientWidth || window.innerWidth;
    const oldH = cv.clientHeight || window.innerHeight;
    applySize();
    const newW = cv.clientWidth || window.innerWidth;
    const newH = cv.clientHeight || window.innerHeight;
    if (newW !== oldW || newH !== oldH) {
      const targetCount = Math.floor((newW + newH) * 0.02);
      if (!userConfig.bubbles?.count) {
        bubbleArray = Array.from({ length: targetCount }, objectCreator);
      }
    }
  };
  window.addEventListener('resize', onResize);

  draw();

  const destroy = () => {
    cancelAnimationFrame(rafId);
    window.removeEventListener('resize', onResize);
    if (!userConfig.canvas && cv.parentNode) {
      cv.parentNode.removeChild(cv);
    }
  };

  return destroy;
}

export default function BubblyBg({
  className = '',
  options = {},
}: {
  className?: string;
  options?: BubblyOptions;
}) {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);

  useEffect(() => {
    if (!canvasRef.current) return;
    const destroy = createBubbly({ canvas: canvasRef.current, ...options });
    return () => destroy();
  }, [options]);

  return (
    <canvas
      ref={canvasRef}
      className={`absolute inset-0 ${className}`}
      style={{
        width: '100%',
        height: '100%',
        display: 'block',
        pointerEvents: 'none',
      }}
    />
  );
}
