import type { SimuladorRequest, SimuladorResponse } from './simulador';

export type ComparadorEscenarioId = 'A' | 'B' | 'C';

export interface ComparadorEscenario {
  id: ComparadorEscenarioId;
  label: string;
  selectedVehiculoId: number;
  selectedConfigId: number;
  request: SimuladorRequest;
  response: SimuladorResponse | null;
  error: string | null;
  isCalculating: boolean;
}

export type ComparadorMetricFormat = 'money' | 'percent';
export type ComparadorMetricHighlight = 'min' | 'max';

export interface ComparadorMetric {
  key: string;
  label: string;
  format: ComparadorMetricFormat;
  highlight?: ComparadorMetricHighlight;
  getValue: (response: SimuladorResponse) => number;
}
