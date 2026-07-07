import { apiRequest } from './apiClient';
import type { ClienteRequest, ClienteResponse } from '../types/cliente';

export async function listarClientes(token: string): Promise<ClienteResponse[]> {
  const response = await apiRequest<ClienteResponse[]>('/clientes', { token });
  return response.data ?? [];
}

export async function crearCliente(
  token: string,
  request: ClienteRequest,
): Promise<ClienteResponse> {
  const response = await apiRequest<ClienteResponse>('/clientes', {
    method: 'POST',
    body: JSON.stringify(request),
    token,
  });

  if (!response.data) {
    throw new Error('La API no devolvio el cliente creado');
  }

  return response.data;
}

export async function buscarClientePorId(token: string, idCliente: number): Promise<ClienteResponse> {
  const response = await apiRequest<ClienteResponse>(`/clientes/${idCliente}`, { token });

  if (!response.data) {
    throw new Error('La API no devolvio el cliente solicitado');
  }

  return response.data;
}

export async function buscarClientePorDni(token: string, dni: string): Promise<ClienteResponse> {
  const response = await apiRequest<ClienteResponse>(`/clientes/dni/${dni}`, { token });

  if (!response.data) {
    throw new Error('La API no devolvio el cliente solicitado');
  }

  return response.data;
}

export async function actualizarCliente(
  token: string,
  idCliente: number,
  request: ClienteRequest,
): Promise<ClienteResponse> {
  const response = await apiRequest<ClienteResponse>(`/clientes/${idCliente}`, {
    method: 'PUT',
    body: JSON.stringify(request),
    token,
  });

  if (!response.data) {
    throw new Error('La API no devolvio el cliente actualizado');
  }

  return response.data;
}

export async function eliminarCliente(token: string, idCliente: number): Promise<void> {
  await apiRequest<void>(`/clientes/${idCliente}`, {
    method: 'DELETE',
    token,
  });
}
