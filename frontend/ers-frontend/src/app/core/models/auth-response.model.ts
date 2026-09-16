// union of two enum strings in backend returns
export type Role = 'EMPLOYEE' | 'MANAGER';

// matches the POST/login response from backend (AuthResponse.java)
export interface AuthResponse {
  token: string;
  username: string;
  role: Role;
  UserId: number;
}