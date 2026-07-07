import { apiRequest } from './apiClient';
import type { ConfiguracionRequest, ConfiguracionResponse } from '../types/configuracion';

export async function listarConfiguraciones(token: string): Promise<ConfiguracionResponse[]> {
  const response = await apiRequest<ConfiguracionResponse[]>('/configuraciones', { token });
  return response.data ?? [];
}

export async function buscarConfiguracionPorId(
  token: string,
  idConfig: number,
): Promise<ConfiguracionResponse> {
  const response = await apiRequest<ConfiguracionResponse>(`/configuraciones/${idConfig}`, { token });

  if (!response.data) {
    throw new Error('La API no devolvio la configuracion solicitada');
  }

  return response.data;
}

export async function crearConfiguracion(
  token: string,
  request: ConfiguracionRequest,
): Promise<ConfiguracionResponse> {
  const response = await apiRequest<ConfiguracionResponse>('/configuraciones', {
    method: 'POST',
    body: JSON.stringify(request),
    token,
  });

  if (!response.data) {
    throw new Error('La API no devolvio la configuracion creada');
  }

  return response.data;
}

export async function actualizarConfiguracion(
  token: string,
  idConfig: number,
  request: ConfiguracionRequest,
): Promise<ConfiguracionResponse> {
  const response = await apiRequest<ConfiguracionResponse>(`/configuraciones/${idConfig}`, {
    method: 'PUT',
    body: JSON.stringify(request),
    token,
  });

  if (!response.data) {
    throw new Error('La API no devolvio la configuracion actualizada');
  }

  return response.data;
}

export async function eliminarConfiguracion(token: string, idConfig: number): Promise<void> {
  await apiRequest<void>(`/configuraciones/${idConfig}`, {
    method: 'DELETE',
    token,
  });
}
