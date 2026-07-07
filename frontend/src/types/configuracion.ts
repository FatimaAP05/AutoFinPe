export interface ConfiguracionRequest {
  moneda: string;
  tipoTasa: string;
  capitalizacion: number;
  tipoGracia: string;
  mesesGracia: number;
}

export interface ConfiguracionResponse {
  idConfig: number;
  moneda: string;
  tipoTasa: string;
  capitalizacion: number;
  tipoGracia: string;
  mesesGracia: number;
}
