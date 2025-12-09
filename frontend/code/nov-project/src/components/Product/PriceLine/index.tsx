import { Product } from '@/types/Product';

function buildPriceList(product: Product) {
  return [
    { level: 1, label: 'Level 1', value: product.price4 }, // level 1 -> price4
    { level: 2, label: 'Level 2', value: product.price3 },
    { level: 3, label: 'Level 3', value: product.price2 },
    { level: 4, label: 'Level 4', value: product.price1 }, // level 4 -> price1
  ].filter((p) => p.value != null);
}

const PriceLines: React.FC<{ product: Product; userLevel: number }> = ({
  product,
  userLevel,
}) => {
  const list = buildPriceList(product);
  const strikeAll = userLevel === 0;
  return (
    <div className="space-y-1.5">
      {list.map(({ level, label, value }) => {
        const isCurrent = !strikeAll && userLevel === level;
        const shouldStrike = !strikeAll && !isCurrent;

        return (
          <div key={level} className="leading-7">
            <span
              className={[
                'font-semibold mr-1',
                'text-[16px]',
                shouldStrike ? 'line-through text-gray-400' : '',
              ].join(' ')}
            >
              {label}:
            </span>

            <span
              className={[
                'text-[18px]',
                isCurrent ? 'font-semibold text-blue-600' : '',
                strikeAll ? 'font-semibold text-black' : '',
                shouldStrike ? 'line-through text-gray-400' : '',
              ].join(' ')}
            >
              ${value}
            </span>

            {isCurrent && (
              <span className="ml-2 text-sm font-medium text-green-600">
                (Current level)
              </span>
            )}
          </div>
        );
      })}
    </div>
  );
};

export default PriceLines;
