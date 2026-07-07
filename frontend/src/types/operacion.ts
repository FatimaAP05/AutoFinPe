export interface OperacionRequest {
  idCliente: number;
  idVehiculo: number;
  idConfiguracion: number;
  moneda: string;
  plazoMeses: number;
  cuotaInicialPct: number;
  cuotaBalonPct: number;
  valorTasaPct: number;
  tipoTasa: string;
  tipoGracia: string;
  mesesGracia: number;
  seguroDesgravamenPct: number;
  seguroVehicularPct: number;
  portesMensuales: number;
  cokAnualPct: number;
}

export interface CronogramaResponse {
  nroCuota: number;
  saldoInicial: number;
  interes: number;
  amortizacion: number;
  seguroDesgrav: number;
  seguroVehic: number;
  portes: number;
  cuotaCredito: number;
  cuotaTotal: number;
  saldoFinal: number;
}

export interface IndicadorResponse {
  idIndicador: number | null;
  tcea: number;
  van: number;
  tir: number;
  totalIntereses: number;
  totalAmortizacion: number;
  totalSeguros: number;
  totalPortes: number;
  totalPagado: number;
}

export interface OperacionResponse {
  idOperacion: number;
  idCliente: number;
  clienteNombre: string;
  idVehiculo: number;
  vehiculoModelo: string;
  idConfiguracion: number;
  fecha: string;
  plazo: number;
  cuotaInicialPct: number;
  cuotaBalonPct: number;
  valorTasa: number;
  estado: string;
  indicador: IndicadorResponse;
  cronograma: CronogramaResponse[];
}
