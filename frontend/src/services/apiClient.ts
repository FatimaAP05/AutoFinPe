import type { StandardApiResponse } from '../types/api';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '/api';

interface ApiRequestOptions extends RequestInit {
  token?: string;
}

export async function apiRequest<T>(
  path: string,
  options: ApiRequestOptions = {},
): Promise<StandardApiResponse<T>> {
  const headers = new Headers(options.headers);

  if (!headers.has('Content-Type') && options.body && !(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json');
  }

  if (options.token) {
    headers.set('Authorization', `Bearer ${options.token}`);
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
  });

  const payload = (await response.json()) as StandardApiResponse<T>;

  if (!response.ok || !payload.success) {
    const errorMessage = payload.errors?.join('\n') || payload.message || 'Error al consumir la API';
    throw new Error(errorMessage);
  }

  return payload;
}

export async function apiBlobRequest(
  path: string,
  options: ApiRequestOptions = {},
): Promise<Blob> {
  const headers = new Headers(options.headers);

  if (options.token) {
    headers.set('Authorization', `Bearer ${options.token}`);
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const contentType = response.headers.get('Content-Type') ?? '';

    if (contentType.includes('application/json')) {
      const payload = (await response.json()) as StandardApiResponse<unknown>;
      const errorMessage = payload.errors?.join(', ') || payload.message || 'Error al descargar el archivo';
      throw new Error(errorMessage);
    }

    const errorMessage = (await response.text()) || 'Error al descargar el archivo';
    throw new Error(errorMessage);
  }

  return response.blob();
}
