import { apiRequest } from './apiClient';
import type { LoginRequest, LoginResponse } from '../types/auth';

export async function login(request: LoginRequest): Promise<LoginResponse> {
  const response = await apiRequest<LoginResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(request),
  });

  if (!response.data) {
    throw new Error('La API no devolvio datos de autenticacion');
  }

  return response.data;
}
