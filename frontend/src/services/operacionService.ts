import { apiBlobRequest, apiRequest } from './apiClient';
import type {
  CronogramaResponse,
  IndicadorResponse,
  OperacionRequest,
  OperacionResponse,
} from '../types/operacion';

export async function listarOperaciones(token: string): Promise<OperacionResponse[]> {
  const response = await apiRequest<OperacionResponse[]>('/operaciones', { token });
  return response.data ?? [];
}

export async function crearOperacion(
  token: string,
  request: OperacionRequest,
): Promise<OperacionResponse> {
  const response = await apiRequest<OperacionResponse>('/operaciones', {
    method: 'POST',
    body: JSON.stringify(request),
    token,
  });

  if (!response.data) {
    throw new Error('La API no devolvio la operacion creada');
  }

  return response.data;
}

export async function obtenerOperacion(token: string, idOperacion: number): Promise<OperacionResponse> {
  const response = await apiRequest<OperacionResponse>(`/operaciones/${idOperacion}`, { token });

  if (!response.data) {
    throw new Error('La API no devolvio la operacion solicitada');
  }

  return response.data;
}

export async function obtenerCronograma(
  token: string,
  idOperacion: number,
): Promise<CronogramaResponse[]> {
  const response = await apiRequest<CronogramaResponse[]>(`/operaciones/${idOperacion}/cronograma`, { token });
  return response.data ?? [];
}

export async function obtenerIndicadores(
  token: string,
  idOperacion: number,
): Promise<IndicadorResponse> {
  const response = await apiRequest<IndicadorResponse>(`/operaciones/${idOperacion}/indicadores`, { token });

  if (!response.data) {
    throw new Error('La API no devolvio los indicadores solicitados');
  }

  return response.data;
}

export async function descargarOperacionPdf(token: string, idOperacion: number): Promise<Blob> {
  return apiBlobRequest(`/operaciones/${idOperacion}/export/pdf`, { token });
}

export async function descargarOperacionExcel(token: string, idOperacion: number): Promise<Blob> {
  return apiBlobRequest(`/operaciones/${idOperacion}/export/excel`, { token });
}
