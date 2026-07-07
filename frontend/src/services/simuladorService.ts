import { apiRequest } from './apiClient';
import type { SimuladorRequest, SimuladorResponse } from '../types/simulador';

export async function calcularSimulacion(
  token: string,
  request: SimuladorRequest,
): Promise<SimuladorResponse> {
  const response = await apiRequest<SimuladorResponse>('/simulador/calcular', {
    method: 'POST',
    body: JSON.stringify(request),
    token,
  });

  if (!response.data) {
    throw new Error('La API no devolvio la simulacion calculada');
  }

  return response.data;
}
