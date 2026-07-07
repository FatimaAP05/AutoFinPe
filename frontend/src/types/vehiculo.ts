export interface VehiculoRequest {
  marca: string;
  modelo: string;
  anio: number;
  precioPen: number;
  precioUsd: number;
  categoria: string;
}

export interface VehiculoResponse {
  idVehiculo: number;
  marca: string;
  modelo: string;
  anio: number;
  precioPen: number;
  precioUsd: number;
  categoria: string;
  imagenUrl: string | null;
}
