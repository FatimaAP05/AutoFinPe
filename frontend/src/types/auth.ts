export interface LoginRequest {
  login: string;
  clave: string;
}

export interface AuthenticatedUser {
  idUsuario: number;
  login: string;
  nombres: string;
  rol: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  expiresInSeconds: number;
  usuario: AuthenticatedUser;
}

export interface AuthSession {
  token: string;
  usuario: AuthenticatedUser;
}
