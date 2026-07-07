export interface SimuladorRequest {
  precioVehiculo: number;
  moneda: string;
  plazoMeses: number;
  cuotaInicialPct: number;
  cuotaBalonPct: number;
  valorTasaPct: number;
  tipoTasa: string;
  capitalizacion: number;
  tipoGracia: string;
  mesesGracia: number;
  seguroDesgravamenPctMensual: number;
  seguroVehicularPctAnual: number;
  portesMensuales: number;
  cokAnualPct: number;
}

export interface CronogramaSimulacionResponse {
  nroCuota: number;
  saldoInicial: number;
  interes: number;
  amortizacion: number;
  seguroDesgravamen: number;
  seguroVehicular: number;
  portes: number;
  cuotaCredito: number;
  cuotaTotal: number;
  saldoFinal: number;
}

export interface IndicadoresSimulacionResponse {
  van: number;
  tirMensual: number;
  tirAnual: number;
  tcea: number;
  totalIntereses: number;
  totalAmortizacion: number;
  totalSeguros: number;
  totalPortes: number;
  totalPagado: number;
}

export interface SimuladorResponse {
  moneda: string;
  plazoMeses: number;
  precioVehiculo: number;
  cuotaInicial: number;
  saldoFinanciado: number;
  tea: number;
  tepMensual: number;
  cuotaBalon: number;
  cuotaUniforme: number;
  indicadores: IndicadoresSimulacionResponse;
  cronograma: CronogramaSimulacionResponse[];
}
