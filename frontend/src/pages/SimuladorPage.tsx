import { FormEvent, useEffect, useState } from 'react';
import { EmptyState } from '../components/ui/EmptyState';
import { ActionBar } from '../components/ui/ActionBar';
import { HelpLabel } from '../components/ui/HelpLabel';
import { MetricStrip } from '../components/ui/MetricStrip';
import { PageHeader } from '../components/ui/PageHeader';
import { Panel } from '../components/ui/Panel';
import { StatusMessage } from '../components/ui/StatusMessage';
import { useAuth } from '../context/AuthContext';
import { listarConfiguraciones } from '../services/configuracionService';
import { calcularSimulacion } from '../services/simuladorService';
import { listarVehiculos } from '../services/vehiculoService';
import type { ConfiguracionResponse } from '../types/configuracion';
import type { SimuladorRequest, SimuladorResponse } from '../types/simulador';
import type { VehiculoResponse } from '../types/vehiculo';

const EMPTY_FORM: SimuladorRequest = {
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

export function SimuladorPage() {
  const { token } = useAuth();
  const [vehiculos, setVehiculos] = useState<VehiculoResponse[]>([]);
  const [configuraciones, setConfiguraciones] = useState<ConfiguracionResponse[]>([]);
  const [selectedVehiculoId, setSelectedVehiculoId] = useState(0);
  const [selectedConfigId, setSelectedConfigId] = useState(0);
  const [form, setForm] = useState<SimuladorRequest>(EMPTY_FORM);
  const [resultado, setResultado] = useState<SimuladorResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isCalculating, setIsCalculating] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

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

      const vehiculoInicial = vehiculosResponse[0];
      const configuracionInicial = configuracionesResponse[0];

      setSelectedVehiculoId(vehiculoInicial?.idVehiculo ?? 0);
      setSelectedConfigId(configuracionInicial?.idConfig ?? 0);
      setForm((currentForm) => aplicarSeleccionInicial(currentForm, vehiculoInicial, configuracionInicial));
    } catch (exception) {
      setError(toErrorMessage(exception));
    } finally {
      setIsLoading(false);
    }
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!token) {
      return;
    }

    setIsCalculating(true);
    setError(null);
    setSuccessMessage(null);

    try {
      const simulacion = await calcularSimulacion(token, normalizarFormulario(form));
      setResultado(simulacion);
      setSuccessMessage('Simulacion calculada correctamente');
    } catch (exception) {
      setError(toErrorMessage(exception));
    } finally {
      setIsCalculating(false);
    }
  }

  function handleVehiculoChange(idVehiculo: number) {
    const vehiculo = vehiculos.find((item) => item.idVehiculo === idVehiculo);
    setSelectedVehiculoId(idVehiculo);

    setForm((currentForm) => ({
      ...currentForm,
      precioVehiculo: vehiculo ? obtenerPrecioVehiculo(vehiculo, currentForm.moneda) : currentForm.precioVehiculo,
    }));
  }

  function handleConfiguracionChange(idConfig: number) {
    const configuracion = configuraciones.find((item) => item.idConfig === idConfig);
    setSelectedConfigId(idConfig);

    if (!configuracion) {
      return;
    }

    setForm((currentForm) => ({
      ...currentForm,
      moneda: configuracion.moneda,
      tipoTasa: configuracion.tipoTasa,
      capitalizacion: configuracion.capitalizacion,
      tipoGracia: configuracion.tipoGracia,
      mesesGracia: configuracion.mesesGracia,
      precioVehiculo: obtenerPrecioVehiculoPorMoneda(selectedVehiculoId, configuracion.moneda, currentForm.precioVehiculo),
    }));
  }

  function updateForm<K extends keyof SimuladorRequest>(key: K, value: SimuladorRequest[K]) {
    setForm((currentForm) => ({
      ...currentForm,
      [key]: value,
    }));
  }

  function handleMonedaChange(moneda: string) {
    setForm((currentForm) => ({
      ...currentForm,
      moneda,
      precioVehiculo: obtenerPrecioVehiculoPorMoneda(selectedVehiculoId, moneda, currentForm.precioVehiculo),
    }));
  }

  function obtenerPrecioVehiculoPorMoneda(idVehiculo: number, moneda: string, fallback: number) {
    const vehiculo = vehiculos.find((item) => item.idVehiculo === idVehiculo);
    return vehiculo ? obtenerPrecioVehiculo(vehiculo, moneda) : fallback;
  }

  return (
    <section className="page-section" aria-labelledby="simulador-title">
      <PageHeader
        kicker="Simulador"
        title="Simulacion financiera"
        titleId="simulador-title"
        action={
          <button className="secondary-button" type="button" onClick={() => void cargarDatosBase()}>
            Actualizar datos
          </button>
        }
      />

      <div className="work-grid">
        <form className="form-panel" onSubmit={handleSubmit}>
          <div className="panel-heading">
            <h3>Parametros</h3>
            <p>Configura el credito y calcula el cronograma directamente con el backend financiero.</p>
          </div>

          <div className="form-grid">
            <label className="form-grid-full">
              <HelpLabel helpKey="vehiculo">Vehiculo</HelpLabel>
              <select
                value={selectedVehiculoId}
                onChange={(event) => handleVehiculoChange(Number(event.target.value))}
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
                value={selectedConfigId}
                onChange={(event) => handleConfiguracionChange(Number(event.target.value))}
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
                value={form.precioVehiculo}
                onChange={(event) => updateForm('precioVehiculo', Number(event.target.value))}
                required
              />
            </label>

            <label>
              <HelpLabel helpKey="moneda">Moneda</HelpLabel>
              <select value={form.moneda} onChange={(event) => handleMonedaChange(event.target.value)} required>
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
                value={form.plazoMeses}
                onChange={(event) => updateForm('plazoMeses', Number(event.target.value))}
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
                value={form.cuotaInicialPct}
                onChange={(event) => updateForm('cuotaInicialPct', Number(event.target.value))}
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
                value={form.cuotaBalonPct}
                onChange={(event) => updateForm('cuotaBalonPct', Number(event.target.value))}
                required
              />
            </label>

            <label>
              <HelpLabel helpKey="valorTasa">Valor de tasa (%)</HelpLabel>
              <input
                max="100"
                min="0"
                step="0.01"
                type="number"
                value={form.valorTasaPct}
                onChange={(event) => updateForm('valorTasaPct', Number(event.target.value))}
                required
              />
            </label>

            <label>
              <HelpLabel helpKey="tipoTasa">Tipo de tasa</HelpLabel>
              <select value={form.tipoTasa} onChange={(event) => updateForm('tipoTasa', event.target.value)} required>
                <option value="E">Efectiva</option>
                <option value="N">Nominal</option>
              </select>
            </label>

            <label>
              <HelpLabel helpKey="capitalizacion">Capitalizacion</HelpLabel>
              <select
                value={form.capitalizacion}
                onChange={(event) => updateForm('capitalizacion', Number(event.target.value))}
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
              <HelpLabel helpKey="periodoGracia">Tipo de gracia</HelpLabel>
              <select
                value={form.tipoGracia}
                onChange={(event) => updateForm('tipoGracia', event.target.value)}
                required
              >
                <option value="S">Sin gracia</option>
                <option value="T">Gracia total</option>
                <option value="P">Gracia parcial</option>
              </select>
            </label>

            <label>
              <HelpLabel helpKey="periodoGracia">Meses de gracia</HelpLabel>
              <input
                max="6"
                min="0"
                type="number"
                value={form.mesesGracia}
                onChange={(event) => updateForm('mesesGracia', Number(event.target.value))}
                required
              />
            </label>

            <label>
              <HelpLabel helpKey="seguroDesgravamen">Seguro desgravamen mensual (%)</HelpLabel>
              <input
                max="10"
                min="0"
                step="0.01"
                type="number"
                value={form.seguroDesgravamenPctMensual}
                onChange={(event) => updateForm('seguroDesgravamenPctMensual', Number(event.target.value))}
                required
              />
            </label>

            <label>
              <HelpLabel helpKey="seguroVehicular">Seguro vehicular anual (%)</HelpLabel>
              <input
                max="20"
                min="0"
                step="0.01"
                type="number"
                value={form.seguroVehicularPctAnual}
                onChange={(event) => updateForm('seguroVehicularPctAnual', Number(event.target.value))}
                required
              />
            </label>

            <label>
              <HelpLabel helpKey="portes">Portes mensuales</HelpLabel>
              <input
                min="0"
                step="0.01"
                type="number"
                value={form.portesMensuales}
                onChange={(event) => updateForm('portesMensuales', Number(event.target.value))}
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
                value={form.cokAnualPct}
                onChange={(event) => updateForm('cokAnualPct', Number(event.target.value))}
                required
              />
            </label>
          </div>

          {error && <StatusMessage type="error">{error}</StatusMessage>}
          {successMessage && <StatusMessage type="success">{successMessage}</StatusMessage>}
          {isLoading && <StatusMessage type="info">Cargando datos disponibles...</StatusMessage>}

          <ActionBar align="end">
            <button className="primary-button" type="submit" disabled={isCalculating || isLoading}>
              {isCalculating ? 'Calculando...' : 'Calcular simulacion'}
            </button>
          </ActionBar>
        </form>

        <Panel title="Resumen financiero">
          {!resultado && <EmptyState>No hay una simulacion calculada en esta sesion.</EmptyState>}

          {resultado && (
            <>
              <MetricStrip
                items={[
                  { label: 'Cuota uniforme', value: formatMoney(resultado.cuotaUniforme), tone: 'positive' },
                  { label: 'Saldo financiado', value: formatMoney(resultado.saldoFinanciado) },
                  { label: 'TCEA', value: formatPercent(resultado.indicadores.tcea), tone: 'warning' },
                ]}
              />
              <table className="summary-table">
                <tbody>
                  <tr>
                    <th>Moneda</th>
                    <td>{resultado.moneda}</td>
                  </tr>
                  <tr>
                    <th>Plazo</th>
                    <td>{resultado.plazoMeses} meses</td>
                  </tr>
                  <tr>
                    <th>Precio vehiculo</th>
                    <td>{formatMoney(resultado.precioVehiculo)}</td>
                  </tr>
                  <tr>
                    <th>Cuota inicial</th>
                    <td>{formatMoney(resultado.cuotaInicial)}</td>
                  </tr>
                  <tr>
                    <th><HelpLabel helpKey="saldoFinanciado">Saldo financiado</HelpLabel></th>
                    <td>{formatMoney(resultado.saldoFinanciado)}</td>
                  </tr>
                  <tr>
                    <th><HelpLabel helpKey="tea">TEA</HelpLabel></th>
                    <td>{formatPercent(resultado.tea)}</td>
                  </tr>
                  <tr>
                    <th>TEP mensual</th>
                    <td>{formatPercent(resultado.tepMensual)}</td>
                  </tr>
                  <tr>
                    <th><HelpLabel helpKey="cuotaBalon">Cuota balon</HelpLabel></th>
                    <td>{formatMoney(resultado.cuotaBalon)}</td>
                  </tr>
                  <tr>
                    <th><HelpLabel helpKey="cuotaUniforme">Cuota uniforme</HelpLabel></th>
                    <td>{formatMoney(resultado.cuotaUniforme)}</td>
                  </tr>
                </tbody>
              </table>

              <div className="panel-heading">
                <h3>Indicadores</h3>
              </div>
              <MetricStrip
                items={[
                  { label: 'VAN', value: formatMoney(resultado.indicadores.van), tone: 'positive' },
                  { label: 'TIR', value: formatPercent(resultado.indicadores.tirAnual) },
                  { label: 'Total pagado', value: formatMoney(resultado.indicadores.totalPagado) },
                ]}
              />
              <table className="summary-table">
                <tbody>
                  <tr>
                    <th><HelpLabel helpKey="van">VAN</HelpLabel></th>
                    <td>{formatMoney(resultado.indicadores.van)}</td>
                  </tr>
                  <tr>
                    <th><HelpLabel helpKey="tir">TIR</HelpLabel></th>
                    <td>{formatPercent(resultado.indicadores.tirAnual)}</td>
                  </tr>
                  <tr>
                    <th><HelpLabel helpKey="tcea">TCEA</HelpLabel></th>
                    <td>{formatPercent(resultado.indicadores.tcea)}</td>
                  </tr>
                  <tr>
                    <th>Total intereses</th>
                    <td>{formatMoney(resultado.indicadores.totalIntereses)}</td>
                  </tr>
                  <tr>
                    <th>Total amortizacion</th>
                    <td>{formatMoney(resultado.indicadores.totalAmortizacion)}</td>
                  </tr>
                  <tr>
                    <th>Total seguros</th>
                    <td>{formatMoney(resultado.indicadores.totalSeguros)}</td>
                  </tr>
                  <tr>
                    <th>Total portes</th>
                    <td>{formatMoney(resultado.indicadores.totalPortes)}</td>
                  </tr>
                  <tr>
                    <th>Total pagado</th>
                    <td>{formatMoney(resultado.indicadores.totalPagado)}</td>
                  </tr>
                </tbody>
              </table>
            </>
          )}
        </Panel>
      </div>

      {resultado && resultado.cronograma.length > 0 && (
        <Panel title="Cronograma" className="dashboard-panel">
          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>N°</th>
                  <th>Saldo inicial</th>
                  <th>Interes</th>
                  <th>Amortizacion</th>
                  <th>Seguro desgravamen</th>
                  <th>Seguro vehicular</th>
                  <th>Portes</th>
                  <th>Cuota credito</th>
                  <th>Cuota total</th>
                  <th>Saldo final</th>
                </tr>
              </thead>
              <tbody>
                {resultado.cronograma.map((cuota) => (
                  <tr key={cuota.nroCuota}>
                    <td>{cuota.nroCuota}</td>
                    <td>{formatMoney(cuota.saldoInicial)}</td>
                    <td>{formatMoney(cuota.interes)}</td>
                    <td>{formatMoney(cuota.amortizacion)}</td>
                    <td>{formatMoney(cuota.seguroDesgravamen)}</td>
                    <td>{formatMoney(cuota.seguroVehicular)}</td>
                    <td>{formatMoney(cuota.portes)}</td>
                    <td>{formatMoney(cuota.cuotaCredito)}</td>
                    <td>{formatMoney(cuota.cuotaTotal)}</td>
                    <td>{formatMoney(cuota.saldoFinal)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Panel>
      )}
    </section>
  );
}

function aplicarSeleccionInicial(
  currentForm: SimuladorRequest,
  vehiculo?: VehiculoResponse,
  configuracion?: ConfiguracionResponse,
): SimuladorRequest {
  const moneda = configuracion?.moneda ?? currentForm.moneda;

  return {
    ...currentForm,
    moneda,
    tipoTasa: configuracion?.tipoTasa ?? currentForm.tipoTasa,
    capitalizacion: configuracion?.capitalizacion ?? currentForm.capitalizacion,
    tipoGracia: configuracion?.tipoGracia ?? currentForm.tipoGracia,
    mesesGracia: configuracion?.mesesGracia ?? currentForm.mesesGracia,
    precioVehiculo: vehiculo ? obtenerPrecioVehiculo(vehiculo, moneda) : currentForm.precioVehiculo,
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
