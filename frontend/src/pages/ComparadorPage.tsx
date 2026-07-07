import { FormEvent, useEffect, useMemo, useState } from 'react';
import { ActionBar } from '../components/ui/ActionBar';
import { EmptyState } from '../components/ui/EmptyState';
import { HelpLabel } from '../components/ui/HelpLabel';
import { MetricStrip } from '../components/ui/MetricStrip';
import { PageHeader } from '../components/ui/PageHeader';
import { Panel } from '../components/ui/Panel';
import { StatCard } from '../components/ui/StatCard';
import { StatusMessage } from '../components/ui/StatusMessage';
import { useAuth } from '../context/AuthContext';
import { listarConfiguraciones } from '../services/configuracionService';
import { calcularSimulacion } from '../services/simuladorService';
import { listarVehiculos } from '../services/vehiculoService';
import type { ConfiguracionResponse } from '../types/configuracion';
import type {
  ComparadorEscenario,
  ComparadorEscenarioId,
  ComparadorMetric,
} from '../types/comparador';
import type { SimuladorRequest, SimuladorResponse } from '../types/simulador';
import type { VehiculoResponse } from '../types/vehiculo';

const BASE_REQUEST: SimuladorRequest = {
  precioVehiculo: 0,
  moneda: 'PEN',
  plazoMeses: 48,
  cuotaInicialPct: 20,
  cuotaBalonPct: 25,
  valorTasaPct: 14.5,
  tipoTasa: 'E',
  capitalizacion: 12,
  tipoGracia: 'S',
  mesesGracia: 0,
  seguroDesgravamenPctMensual: 0.05,
  seguroVehicularPctAnual: 0.12,
  portesMensuales: 10,
  cokAnualPct: 10,
};

const METRICS: ComparadorMetric[] = [
  {
    key: 'cuotaUniforme',
    label: 'Cuota uniforme',
    format: 'money',
    getValue: (response) => response.cuotaUniforme,
  },
  {
    key: 'saldoFinanciado',
    label: 'Saldo financiado',
    format: 'money',
    getValue: (response) => response.saldoFinanciado,
  },
  {
    key: 'cuotaBalon',
    label: 'Cuota balon',
    format: 'money',
    getValue: (response) => response.cuotaBalon,
  },
  {
    key: 'van',
    label: 'VAN',
    format: 'money',
    highlight: 'max',
    getValue: (response) => response.indicadores.van,
  },
  {
    key: 'tir',
    label: 'TIR',
    format: 'percent',
    getValue: (response) => response.indicadores.tirAnual,
  },
  {
    key: 'tcea',
    label: 'TCEA',
    format: 'percent',
    highlight: 'min',
    getValue: (response) => response.indicadores.tcea,
  },
  {
    key: 'totalIntereses',
    label: 'Total intereses',
    format: 'money',
    getValue: (response) => response.indicadores.totalIntereses,
  },
  {
    key: 'totalAmortizacion',
    label: 'Total amortizacion',
    format: 'money',
    getValue: (response) => response.indicadores.totalAmortizacion,
  },
  {
    key: 'totalSeguros',
    label: 'Total seguros',
    format: 'money',
    getValue: (response) => response.indicadores.totalSeguros,
  },
  {
    key: 'totalPortes',
    label: 'Total portes',
    format: 'money',
    getValue: (response) => response.indicadores.totalPortes,
  },
  {
    key: 'totalPagado',
    label: 'Total pagado',
    format: 'money',
    highlight: 'min',
    getValue: (response) => response.indicadores.totalPagado,
  },
];

const INITIAL_SCENARIOS: ComparadorEscenario[] = [
  createScenario('A'),
  createScenario('B'),
];

export function ComparadorPage() {
  const { token } = useAuth();
  const [vehiculos, setVehiculos] = useState<VehiculoResponse[]>([]);
  const [configuraciones, setConfiguraciones] = useState<ConfiguracionResponse[]>([]);
  const [scenarios, setScenarios] = useState<ComparadorEscenario[]>(INITIAL_SCENARIOS);
  const [isLoading, setIsLoading] = useState(false);
  const [isComparing, setIsComparing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const completedScenarios = useMemo(
    () => scenarios.filter((scenario) => scenario.response),
    [scenarios],
  );

  useEffect(() => {
    void cargarDatosBase();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  async function cargarDatosBase() {
    if (!token) {
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      const [vehiculosResponse, configuracionesResponse] = await Promise.all([
        listarVehiculos(token),
        listarConfiguraciones(token),
      ]);

      setVehiculos(vehiculosResponse);
      setConfiguraciones(configuracionesResponse);
      setScenarios((currentScenarios) =>
        currentScenarios.map((scenario) =>
          aplicarDatosBase(scenario, vehiculosResponse[0], configuracionesResponse[0]),
        ),
      );
    } catch (exception) {
      setError(toErrorMessage(exception));
    } finally {
      setIsLoading(false);
    }
  }

  async function handleScenarioSubmit(
    event: FormEvent<HTMLFormElement>,
    scenarioId: ComparadorEscenarioId,
  ) {
    event.preventDefault();
    await calcularEscenario(scenarioId);
  }

  async function calcularEscenario(scenarioId: ComparadorEscenarioId) {
    if (!token) {
      return;
    }

    const scenario = scenarios.find((item) => item.id === scenarioId);
    if (!scenario) {
      return;
    }

    updateScenario(scenarioId, (currentScenario) => ({
      ...currentScenario,
      response: null,
      error: null,
      isCalculating: true,
    }));
    setError(null);
    setSuccessMessage(null);

    try {
      const response = await calcularSimulacion(token, normalizarFormulario(scenario.request));
      updateScenario(scenarioId, (currentScenario) => ({
        ...currentScenario,
        response,
        error: null,
        isCalculating: false,
      }));
      setSuccessMessage(`${scenario.label} calculado correctamente`);
    } catch (exception) {
      updateScenario(scenarioId, (currentScenario) => ({
        ...currentScenario,
        response: null,
        error: toErrorMessage(exception),
        isCalculating: false,
      }));
    }
  }

  async function calcularTodos() {
    if (!token) {
      return;
    }

    setIsComparing(true);
    setError(null);
    setSuccessMessage(null);
    setScenarios((currentScenarios) =>
      currentScenarios.map((scenario) => ({
        ...scenario,
        response: null,
        error: null,
        isCalculating: true,
      })),
    );

    try {
      const calculatedScenarios = await Promise.all(
        scenarios.map(async (scenario) => {
          try {
            const response = await calcularSimulacion(token, normalizarFormulario(scenario.request));
            return {
              ...scenario,
              response,
              error: null,
              isCalculating: false,
            };
          } catch (exception) {
            return {
              ...scenario,
              response: null,
              error: toErrorMessage(exception),
              isCalculating: false,
            };
          }
        }),
      );

      setScenarios(calculatedScenarios);
      const failedCount = calculatedScenarios.filter((scenario) => scenario.error).length;
      setSuccessMessage(
        failedCount === 0
          ? 'Comparacion calculada correctamente'
          : 'La comparacion termino con escenarios pendientes de corregir',
      );
    } finally {
      setIsComparing(false);
    }
  }

  function agregarEscenario() {
    setScenarios((currentScenarios) => {
      if (currentScenarios.length >= 3) {
        return currentScenarios;
      }

      const nextId: ComparadorEscenarioId = currentScenarios.some((scenario) => scenario.id === 'C') ? 'B' : 'C';
      const nextScenario = aplicarDatosBase(createScenario(nextId), vehiculos[0], configuraciones[0]);
      return [...currentScenarios, nextScenario].sort((left, right) => left.id.localeCompare(right.id));
    });
  }

  function quitarEscenario() {
    setScenarios((currentScenarios) => {
      if (currentScenarios.length <= 2) {
        return currentScenarios;
      }
      return currentScenarios.filter((scenario) => scenario.id !== 'C');
    });
  }

  function handleVehiculoChange(scenarioId: ComparadorEscenarioId, idVehiculo: number) {
    const vehiculo = vehiculos.find((item) => item.idVehiculo === idVehiculo);

    updateScenario(scenarioId, (scenario) => ({
      ...scenario,
      selectedVehiculoId: idVehiculo,
      response: null,
      request: {
        ...scenario.request,
        precioVehiculo: vehiculo ? obtenerPrecioVehiculo(vehiculo, scenario.request.moneda) : scenario.request.precioVehiculo,
      },
    }));
  }

  function handleConfiguracionChange(scenarioId: ComparadorEscenarioId, idConfig: number) {
    const configuracion = configuraciones.find((item) => item.idConfig === idConfig);

    updateScenario(scenarioId, (scenario) => {
      if (!configuracion) {
        return {
          ...scenario,
          selectedConfigId: idConfig,
          response: null,
        };
      }

      return {
        ...scenario,
        selectedConfigId: idConfig,
        response: null,
        request: {
          ...scenario.request,
          moneda: configuracion.moneda,
          tipoTasa: configuracion.tipoTasa,
          capitalizacion: configuracion.capitalizacion,
          tipoGracia: configuracion.tipoGracia,
          mesesGracia: configuracion.mesesGracia,
          precioVehiculo: obtenerPrecioVehiculoPorMoneda(
            scenario.selectedVehiculoId,
            configuracion.moneda,
            scenario.request.precioVehiculo,
          ),
        },
      };
    });
  }

  function handleMonedaChange(scenarioId: ComparadorEscenarioId, moneda: string) {
    updateScenario(scenarioId, (scenario) => ({
      ...scenario,
      response: null,
      request: {
        ...scenario.request,
        moneda,
        precioVehiculo: obtenerPrecioVehiculoPorMoneda(
          scenario.selectedVehiculoId,
          moneda,
          scenario.request.precioVehiculo,
        ),
      },
    }));
  }

  function updateRequest<K extends keyof SimuladorRequest>(
    scenarioId: ComparadorEscenarioId,
    key: K,
    value: SimuladorRequest[K],
  ) {
    updateScenario(scenarioId, (scenario) => ({
      ...scenario,
      response: null,
      request: {
        ...scenario.request,
        [key]: value,
      },
    }));
  }

  function updateScenario(
    scenarioId: ComparadorEscenarioId,
    updater: (scenario: ComparadorEscenario) => ComparadorEscenario,
  ) {
    setScenarios((currentScenarios) =>
      currentScenarios.map((scenario) => (scenario.id === scenarioId ? updater(scenario) : scenario)),
    );
  }

  function obtenerPrecioVehiculoPorMoneda(idVehiculo: number, moneda: string, fallback: number) {
    const vehiculo = vehiculos.find((item) => item.idVehiculo === idVehiculo);
    return vehiculo ? obtenerPrecioVehiculo(vehiculo, moneda) : fallback;
  }

  const bestTcea = findBestScenario(completedScenarios, (response) => response.indicadores.tcea, 'min');
  const bestTotalPagado = findBestScenario(completedScenarios, (response) => response.indicadores.totalPagado, 'min');
  const bestVan = findBestScenario(completedScenarios, (response) => response.indicadores.van, 'max');

  return (
    <section className="page-section" aria-labelledby="comparador-title">
      <PageHeader
        kicker="Comparador"
        title="Comparador de escenarios"
        titleId="comparador-title"
        action={
          <>
            <button
              className="secondary-button"
              type="button"
              onClick={agregarEscenario}
              disabled={scenarios.length >= 3}
            >
              Agregar escenario
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={quitarEscenario}
              disabled={scenarios.length <= 2}
            >
              Quitar escenario
            </button>
            <button
              className="primary-button"
              type="button"
              onClick={() => void calcularTodos()}
              disabled={isLoading || isComparing}
            >
              {isComparing ? 'Comparando...' : 'Comparar escenarios'}
            </button>
          </>
        }
      />

      {error && <StatusMessage type="error">{error}</StatusMessage>}
      {successMessage && <StatusMessage type="success">{successMessage}</StatusMessage>}
      {isLoading && <StatusMessage type="info">Cargando vehiculos y configuraciones...</StatusMessage>}

      <div className="scenario-grid">
        {scenarios.map((scenario) => (
          <form
            key={scenario.id}
            className="form-panel scenario-form"
            onSubmit={(event) => void handleScenarioSubmit(event, scenario.id)}
          >
            <div className="panel-heading scenario-heading">
              <div>
                <p className="section-kicker">Escenario {scenario.id}</p>
                <h3>{scenario.label}</h3>
              </div>
              {scenario.response && <span className="comparison-chip">Calculado</span>}
            </div>

            <div className="form-grid compact-form-grid">
              <label className="form-grid-full">
                <HelpLabel helpKey="vehiculo">Vehiculo</HelpLabel>
                <select
                  value={scenario.selectedVehiculoId}
                  onChange={(event) => handleVehiculoChange(scenario.id, Number(event.target.value))}
                  required
                >
                  <option value={0}>Seleccione</option>
                  {vehiculos.map((vehiculo) => (
                    <option key={vehiculo.idVehiculo} value={vehiculo.idVehiculo}>
                      {vehiculo.marca} {vehiculo.modelo} ({vehiculo.anio})
                    </option>
                  ))}
                </select>
              </label>

              <label className="form-grid-full">
                <HelpLabel helpKey="configuracion">Configuracion</HelpLabel>
                <select
                  value={scenario.selectedConfigId}
                  onChange={(event) => handleConfiguracionChange(scenario.id, Number(event.target.value))}
                  required
                >
                  <option value={0}>Seleccione</option>
                  {configuraciones.map((configuracion) => (
                    <option key={configuracion.idConfig} value={configuracion.idConfig}>
                      {configuracion.moneda} - {configuracion.tipoTasa} - Cap. {configuracion.capitalizacion} - Gracia{' '}
                      {configuracion.tipoGracia}/{configuracion.mesesGracia}
                    </option>
                  ))}
                </select>
              </label>

              <label>
                <HelpLabel helpKey="precioVehiculo">Precio vehiculo</HelpLabel>
                <input
                  min="0.01"
                  step="0.01"
                  type="number"
                  value={scenario.request.precioVehiculo}
                  onChange={(event) => updateRequest(scenario.id, 'precioVehiculo', Number(event.target.value))}
                  required
                />
              </label>

              <label>
                <HelpLabel helpKey="moneda">Moneda</HelpLabel>
                <select
                  value={scenario.request.moneda}
                  onChange={(event) => handleMonedaChange(scenario.id, event.target.value)}
                  required
                >
                  <option value="PEN">PEN</option>
                  <option value="USD">USD</option>
                </select>
              </label>

              <label>
                <HelpLabel helpKey="plazo">Plazo</HelpLabel>
                <input
                  max="84"
                  min="12"
                  type="number"
                  value={scenario.request.plazoMeses}
                  onChange={(event) => updateRequest(scenario.id, 'plazoMeses', Number(event.target.value))}
                  required
                />
              </label>

              <label>
                <HelpLabel helpKey="cuotaInicial">Cuota inicial (%)</HelpLabel>
                <input
                  max="90"
                  min="0"
                  step="0.01"
                  type="number"
                  value={scenario.request.cuotaInicialPct}
                  onChange={(event) => updateRequest(scenario.id, 'cuotaInicialPct', Number(event.target.value))}
                  required
                />
              </label>

              <label>
                <HelpLabel helpKey="cuotaBalon">Cuota balon (%)</HelpLabel>
                <input
                  max="50"
                  min="0"
                  step="0.01"
                  type="number"
                  value={scenario.request.cuotaBalonPct}
                  onChange={(event) => updateRequest(scenario.id, 'cuotaBalonPct', Number(event.target.value))}
                  required
                />
              </label>

              <label>
                <HelpLabel helpKey="valorTasa">Valor tasa (%)</HelpLabel>
                <input
                  max="100"
                  min="0"
                  step="0.01"
                  type="number"
                  value={scenario.request.valorTasaPct}
                  onChange={(event) => updateRequest(scenario.id, 'valorTasaPct', Number(event.target.value))}
                  required
                />
              </label>

              <label>
                <HelpLabel helpKey="tipoTasa">Tipo tasa</HelpLabel>
                <select
                  value={scenario.request.tipoTasa}
                  onChange={(event) => updateRequest(scenario.id, 'tipoTasa', event.target.value)}
                  required
                >
                  <option value="E">Efectiva</option>
                  <option value="N">Nominal</option>
                </select>
              </label>

              <label>
                <HelpLabel helpKey="capitalizacion">Capitalizacion</HelpLabel>
                <select
                  value={scenario.request.capitalizacion}
                  onChange={(event) => updateRequest(scenario.id, 'capitalizacion', Number(event.target.value))}
                  required
                >
                  <option value={1}>1</option>
                  <option value={2}>2</option>
                  <option value={4}>4</option>
                  <option value={12}>12</option>
                  <option value={365}>365</option>
                </select>
              </label>

              <label>
                <HelpLabel helpKey="periodoGracia">Tipo gracia</HelpLabel>
                <select
                  value={scenario.request.tipoGracia}
                  onChange={(event) => updateRequest(scenario.id, 'tipoGracia', event.target.value)}
                  required
                >
                  <option value="S">Sin gracia</option>
                  <option value="T">Gracia total</option>
                  <option value="P">Gracia parcial</option>
                </select>
              </label>

              <label>
                <HelpLabel helpKey="periodoGracia">Meses gracia</HelpLabel>
                <input
                  max="6"
                  min="0"
                  type="number"
                  value={scenario.request.mesesGracia}
                  onChange={(event) => updateRequest(scenario.id, 'mesesGracia', Number(event.target.value))}
                  required
                />
              </label>

              <label>
                <HelpLabel helpKey="seguroDesgravamen">Seguro desgravamen (%)</HelpLabel>
                <input
                  max="10"
                  min="0"
                  step="0.01"
                  type="number"
                  value={scenario.request.seguroDesgravamenPctMensual}
                  onChange={(event) =>
                    updateRequest(scenario.id, 'seguroDesgravamenPctMensual', Number(event.target.value))
                  }
                  required
                />
              </label>

              <label>
                <HelpLabel helpKey="seguroVehicular">Seguro vehicular (%)</HelpLabel>
                <input
                  max="20"
                  min="0"
                  step="0.01"
                  type="number"
                  value={scenario.request.seguroVehicularPctAnual}
                  onChange={(event) => updateRequest(scenario.id, 'seguroVehicularPctAnual', Number(event.target.value))}
                  required
                />
              </label>

              <label>
                <HelpLabel helpKey="portes">Portes</HelpLabel>
                <input
                  min="0"
                  step="0.01"
                  type="number"
                  value={scenario.request.portesMensuales}
                  onChange={(event) => updateRequest(scenario.id, 'portesMensuales', Number(event.target.value))}
                  required
                />
              </label>

              <label>
                <HelpLabel helpKey="cok">COK anual (%)</HelpLabel>
                <input
                  max="100"
                  min="0"
                  step="0.01"
                  type="number"
                  value={scenario.request.cokAnualPct}
                  onChange={(event) => updateRequest(scenario.id, 'cokAnualPct', Number(event.target.value))}
                  required
                />
              </label>
            </div>

            {scenario.error && <StatusMessage type="error">{scenario.error}</StatusMessage>}

            <ActionBar align="end">
              <button className="secondary-button" type="submit" disabled={isLoading || scenario.isCalculating}>
                {scenario.isCalculating ? 'Calculando...' : `Calcular ${scenario.id}`}
              </button>
            </ActionBar>
          </form>
        ))}
      </div>

      {completedScenarios.length > 0 && (
        <>
        <MetricStrip
          items={[
            { label: 'Escenarios calculados', value: completedScenarios.length },
            { label: 'Menor TCEA', value: bestTcea ? `${bestTcea.scenario.label} · ${formatPercent(bestTcea.value)}` : '-', tone: 'positive' },
            { label: 'Mayor VAN', value: bestVan ? `${bestVan.scenario.label} · ${formatMoney(bestVan.value)}` : '-' },
          ]}
        />
        <div className="dashboard-grid comparison-stat-grid">
          <StatCard
            kicker="Menor TCEA"
            value={bestTcea ? `${bestTcea.scenario.label}` : '-'}
            description={bestTcea ? formatPercent(bestTcea.value) : 'Pendiente'}
          />
          <StatCard
            kicker="Menor total pagado"
            value={bestTotalPagado ? `${bestTotalPagado.scenario.label}` : '-'}
            description={bestTotalPagado ? formatMoney(bestTotalPagado.value) : 'Pendiente'}
          />
          <StatCard
            kicker="Mayor VAN"
            value={bestVan ? `${bestVan.scenario.label}` : '-'}
            description={bestVan ? formatMoney(bestVan.value) : 'Pendiente'}
          />
        </div>
        </>
      )}

      <Panel title="Tabla comparativa" className="dashboard-panel">
        {completedScenarios.length < 2 && (
          <EmptyState>Calcula al menos dos escenarios para ver la comparacion lado a lado.</EmptyState>
        )}

        {completedScenarios.length >= 2 && (
          <div className="table-scroll">
            <table className="comparison-table">
              <thead>
                <tr>
                  <th>Indicador</th>
                  {scenarios.map((scenario) => (
                    <th key={scenario.id}>{scenario.label}</th>
                  ))}
                  {scenarios.slice(1).map((scenario) => (
                    <th key={`${scenario.id}-delta`}>Diferencia {scenario.id} vs A</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {METRICS.map((metric) => (
                  <tr key={metric.key}>
                    <th>{getMetricLabel(metric)}</th>
                    {scenarios.map((scenario) => (
                      <td key={`${metric.key}-${scenario.id}`} className={getMetricCellClass(metric, scenario, scenarios)}>
                        {scenario.response ? formatMetric(metric, metric.getValue(scenario.response)) : '-'}
                      </td>
                    ))}
                    {scenarios.slice(1).map((scenario) => (
                      <td key={`${metric.key}-${scenario.id}-delta`}>
                        {formatDelta(metric, scenarios[0].response, scenario.response)}
                      </td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Panel>
    </section>
  );
}

function createScenario(id: ComparadorEscenarioId): ComparadorEscenario {
  return {
    id,
    label: `Escenario ${id}`,
    selectedVehiculoId: 0,
    selectedConfigId: 0,
    request: {
      ...BASE_REQUEST,
    },
    response: null,
    error: null,
    isCalculating: false,
  };
}

function aplicarDatosBase(
  scenario: ComparadorEscenario,
  vehiculo?: VehiculoResponse,
  configuracion?: ConfiguracionResponse,
): ComparadorEscenario {
  const moneda = configuracion?.moneda ?? scenario.request.moneda;

  return {
    ...scenario,
    selectedVehiculoId: vehiculo?.idVehiculo ?? scenario.selectedVehiculoId,
    selectedConfigId: configuracion?.idConfig ?? scenario.selectedConfigId,
    response: null,
    request: {
      ...scenario.request,
      moneda,
      tipoTasa: configuracion?.tipoTasa ?? scenario.request.tipoTasa,
      capitalizacion: configuracion?.capitalizacion ?? scenario.request.capitalizacion,
      tipoGracia: configuracion?.tipoGracia ?? scenario.request.tipoGracia,
      mesesGracia: configuracion?.mesesGracia ?? scenario.request.mesesGracia,
      precioVehiculo: vehiculo ? obtenerPrecioVehiculo(vehiculo, moneda) : scenario.request.precioVehiculo,
    },
  };
}

function obtenerPrecioVehiculo(vehiculo: VehiculoResponse, moneda: string) {
  return moneda === 'USD' ? vehiculo.precioUsd : vehiculo.precioPen;
}

function normalizarFormulario(form: SimuladorRequest): SimuladorRequest {
  return {
    precioVehiculo: Number(form.precioVehiculo),
    moneda: form.moneda,
    plazoMeses: Number(form.plazoMeses),
    cuotaInicialPct: Number(form.cuotaInicialPct),
    cuotaBalonPct: Number(form.cuotaBalonPct),
    valorTasaPct: Number(form.valorTasaPct),
    tipoTasa: form.tipoTasa,
    capitalizacion: Number(form.capitalizacion),
    tipoGracia: form.tipoGracia,
    mesesGracia: Number(form.mesesGracia),
    seguroDesgravamenPctMensual: Number(form.seguroDesgravamenPctMensual),
    seguroVehicularPctAnual: Number(form.seguroVehicularPctAnual),
    portesMensuales: Number(form.portesMensuales),
    cokAnualPct: Number(form.cokAnualPct),
  };
}

function getMetricCellClass(
  metric: ComparadorMetric,
  scenario: ComparadorEscenario,
  scenarios: ComparadorEscenario[],
) {
  if (!metric.highlight || !scenario.response) {
    return '';
  }

  const values = scenarios
    .filter((item) => item.response)
    .map((item) => metric.getValue(item.response as SimuladorResponse));

  if (values.length < 2) {
    return '';
  }

  const targetValue = metric.highlight === 'min' ? Math.min(...values) : Math.max(...values);
  const currentValue = metric.getValue(scenario.response);
  return currentValue === targetValue ? 'best-metric' : '';
}

function findBestScenario(
  scenarios: ComparadorEscenario[],
  getValue: (response: SimuladorResponse) => number,
  mode: 'min' | 'max',
) {
  const calculatedScenarios = scenarios.filter(
    (scenario): scenario is ComparadorEscenario & { response: SimuladorResponse } => Boolean(scenario.response),
  );

  if (calculatedScenarios.length === 0) {
    return null;
  }

  const scenario = calculatedScenarios.reduce((best, current) => {
    const bestValue = getValue(best.response);
    const currentValue = getValue(current.response);
    const isBetter = mode === 'min' ? currentValue < bestValue : currentValue > bestValue;
    return isBetter ? current : best;
  }, calculatedScenarios[0]);

  return {
    scenario,
    value: getValue(scenario.response),
  };
}

function formatDelta(metric: ComparadorMetric, baseResponse: SimuladorResponse | null, currentResponse: SimuladorResponse | null) {
  if (!baseResponse || !currentResponse) {
    return '-';
  }

  const baseValue = metric.getValue(baseResponse);
  const currentValue = metric.getValue(currentResponse);
  const absoluteDifference = currentValue - baseValue;
  const percentageDifference = baseValue === 0 ? null : (absoluteDifference / Math.abs(baseValue)) * 100;

  return (
    <span className={absoluteDifference < 0 ? 'comparison-delta-positive' : 'comparison-delta'}>
      {formatMetric(metric, absoluteDifference)}
      {percentageDifference !== null && ` (${percentageDifference.toFixed(2)}%)`}
    </span>
  );
}

function formatMetric(metric: ComparadorMetric, value: number) {
  return metric.format === 'percent' ? formatPercent(value) : formatMoney(value);
}

function getMetricLabel(metric: ComparadorMetric) {
  if (metric.key === 'cuotaUniforme') {
    return <HelpLabel helpKey="cuotaUniforme">{metric.label}</HelpLabel>;
  }
  if (metric.key === 'saldoFinanciado') {
    return <HelpLabel helpKey="saldoFinanciado">{metric.label}</HelpLabel>;
  }
  if (metric.key === 'cuotaBalon') {
    return <HelpLabel helpKey="cuotaBalon">{metric.label}</HelpLabel>;
  }
  if (metric.key === 'van') {
    return <HelpLabel helpKey="van">{metric.label}</HelpLabel>;
  }
  if (metric.key === 'tir') {
    return <HelpLabel helpKey="tir">{metric.label}</HelpLabel>;
  }
  if (metric.key === 'tcea') {
    return <HelpLabel helpKey="tcea">{metric.label}</HelpLabel>;
  }
  if (metric.key === 'totalSeguros') {
    return <HelpLabel helpKey="seguroVehicular">{metric.label}</HelpLabel>;
  }
  if (metric.key === 'totalPortes') {
    return <HelpLabel helpKey="portes">{metric.label}</HelpLabel>;
  }
  return metric.label;
}

function formatMoney(value: number) {
  return new Intl.NumberFormat('es-PE', {
    maximumFractionDigits: 2,
    minimumFractionDigits: 2,
  }).format(value);
}

function formatPercent(value: number) {
  return `${Number(value).toFixed(4)}%`;
}

function toErrorMessage(exception: unknown) {
  return exception instanceof Error ? exception.message : 'Ocurrio un error inesperado';
}
