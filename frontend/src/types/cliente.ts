export interface ClienteRequest {
  dni: string;
  nombres: string;
  apellidos: string;
  ingresoMensual: number;
  calificacion: string;
  telefono?: string;
  email?: string;
}

export interface ClienteResponse {
  idCliente: number;
  dni: string;
  nombres: string;
  apellidos: string;
  ingresoMensual: number;
  calificacion: string;
  telefono?: string | null;
  email?: string | null;
}
