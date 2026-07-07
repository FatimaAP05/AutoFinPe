import { apiRequest } from './apiClient';
import type { VehiculoRequest, VehiculoResponse } from '../types/vehiculo';

export async function listarVehiculos(token: string): Promise<VehiculoResponse[]> {
  const response = await apiRequest<VehiculoResponse[]>('/vehiculos', { token });
  return response.data ?? [];
}

export async function buscarVehiculoPorId(
  token: string,
  idVehiculo: number,
): Promise<VehiculoResponse> {
  const response = await apiRequest<VehiculoResponse>(`/vehiculos/${idVehiculo}`, { token });

  if (!response.data) {
    throw new Error('La API no devolvio el vehiculo solicitado');
  }

  return response.data;
}

export async function buscarVehiculosPorMarca(
  token: string,
  marca: string,
): Promise<VehiculoResponse[]> {
  const response = await apiRequest<VehiculoResponse[]>(`/vehiculos/marca/${marca}`, { token });
  return response.data ?? [];
}

export async function buscarVehiculosPorCategoria(
  token: string,
  categoria: string,
): Promise<VehiculoResponse[]> {
  const response = await apiRequest<VehiculoResponse[]>(`/vehiculos/categoria/${categoria}`, { token });
  return response.data ?? [];
}

export async function crearVehiculo(
  token: string,
  request: VehiculoRequest,
): Promise<VehiculoResponse> {
  const response = await apiRequest<VehiculoResponse>('/vehiculos', {
    method: 'POST',
    body: JSON.stringify(request),
    token,
  });

  if (!response.data) {
    throw new Error('La API no devolvio el vehiculo creado');
  }

  return response.data;
}

export async function actualizarVehiculo(
  token: string,
  idVehiculo: number,
  request: VehiculoRequest,
): Promise<VehiculoResponse> {
  const response = await apiRequest<VehiculoResponse>(`/vehiculos/${idVehiculo}`, {
    method: 'PUT',
    body: JSON.stringify(request),
    token,
  });

  if (!response.data) {
    throw new Error('La API no devolvio el vehiculo actualizado');
  }

  return response.data;
}

export async function eliminarVehiculo(token: string, idVehiculo: number): Promise<void> {
  await apiRequest<void>(`/vehiculos/${idVehiculo}`, {
    method: 'DELETE',
    token,
  });
}

export async function subirImagenVehiculo(
  token: string,
  idVehiculo: number,
  imagen: File,
): Promise<VehiculoResponse> {
  const formData = new FormData();
  formData.append('imagen', imagen);

  const response = await apiRequest<VehiculoResponse>(`/vehiculos/${idVehiculo}/imagen`, {
    method: 'POST',
    body: formData,
    token,
  });

  if (!response.data) {
    throw new Error('La API no devolvio el vehiculo actualizado');
  }

  return response.data;
}
