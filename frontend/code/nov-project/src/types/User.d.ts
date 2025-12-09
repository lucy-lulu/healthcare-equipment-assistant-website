export interface UserDetail {
  id: string;
  username: string;
  email: string;
  role: 'partner' | 'sales' | 'ot' | 'admin';
  token: string;
  type: string;
  level: number; // 1, 2, 3, 4
}

export interface LoginRequestParam {
  username: string;
  password: string;
}
