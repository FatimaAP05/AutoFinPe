import { FormEvent, useEffect, useState } from 'react';
import { EmptyState } from '../components/ui/EmptyState';
import { ActionBar } from '../components/ui/ActionBar';
import { FormSection } from '../components/ui/FormSection';
import { HelpLabel } from '../components/ui/HelpLabel';
import { MetricStrip } from '../components/ui/MetricStrip';
import { PageHeader } from '../components/ui/PageHeader';
import { Panel } from '../components/ui/Panel';
import { StatusMessage } from '../components/ui/StatusMessage';
import { useAuth } from '../context/AuthContext';
import { listarClientes } from '../services/clienteService';
import { listarConfiguraciones } from '../services/configuracionService';
import {
  crearOperacion,
  descargarOperacionPdf,
  listarOperaciones,
  obtenerCronograma,
  obtenerIndicadores,
  obtenerOperacion,
} from '../services/operacionService';
import { listarVehiculos } from '../services/vehiculoService';
import type { ClienteResponse } from '../types/cliente';
import type { ConfiguracionResponse } from '../types/configuracion';
import type {
  CronogramaResponse,
  IndicadorResponse,
  OperacionRequest,
  OperacionResponse,
} from '../types/operacion';
import type { VehiculoResponse } from '../types/vehiculo';

const EMPTY_FORM: OperacionRequest = {
  idCliente: 0,
  idVehiculo: 0,
  idConfiguracion: 0,
  moneda: 'PEN',
  plazoMeses: 48,
  cuotaInicialPct: 20,
  cuotaBalonPct: 25,
  valorTasaPct: 14.5,
  tipoTasa: 'E',
  tipoGracia: 'S',
  mesesGracia: 0,
  seguroDesgravamenPct: 0.05,
  seguroVehicularPct: 0.12,
  portesMensuales: 10,
  cokAnualPct: 10,
};

export function OperacionesPage() {
  const { token } = useAuth();
  const [clientes, setClientes] = useState<ClienteResponse[]>([]);
  const [vehiculos, setVehiculos] = useState<VehiculoResponse[]>([]);
  const [configuraciones, setConfiguraciones] = useState<ConfiguracionResponse[]>([]);
  const [operaciones, setOperaciones] = useState<OperacionResponse[]>([]);
  const [form, setForm] = useState<OperacionRequest>(EMPTY_FORM);
  const [operacion, setOperacion] = useState<OperacionResponse | null>(null);
  const [cronograma, setCronograma] = useState<CronogramaResponse[]>([]);
  const [indicadores, setIndicadores] = useState<IndicadorResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [downloadingKey, setDownloadingKey] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  useEffect(() => {
    void cargarDatos();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  async function cargarDatos() {
    if (!token) {
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      const [clientesResponse, vehiculosResponse, configuracionesResponse, operacionesResponse] = await Promise.all([
        listarClientes(token),
        listarVehiculos(token),
        listarConfiguraciones(token),
        listarOperaciones(token),
      ]);

      setClientes(clientesResponse);
      setVehiculos(vehiculosResponse);
      setConfiguraciones(configuracionesResponse);
      setOperaciones(operacionesResponse);
      setForm((currentForm) => ({
        ...currentForm,
        idCliente: currentForm.idCliente || clientesResponse[0]?.idCliente || 0,
        idVehiculo: currentForm.idVehiculo || vehiculosResponse[0]?.idVehiculo || 0,
        idConfiguracion: currentForm.idConfiguracion || configuracionesResponse[0]?.idConfig || 0,
      }));
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

    setIsSaving(true);
    setError(null);
    setSuccessMessage(null);

    try {
      const request = normalizarFormulario(form);
      const operacionCreada = await crearOperacion(token, request);
      const [detalle, cronogramaResponse, indicadoresResponse] = await Promise.all([
        obtenerOperacion(token, operacionCreada.idOperacion),
        obtenerCronograma(token, operacionCreada.idOperacion),
        obtenerIndicadores(token, operacionCreada.idOperacion),
      ]);

      setOperacion(detalle);
      setCronograma(cronogramaResponse);
      setIndicadores(indicadoresResponse);
      setSuccessMessage('Operacion creada correctamente');
      await cargarOperaciones();
    } catch (exception) {
      setError(toErrorMessage(exception));
    } finally {
      setIsSaving(false);
    }
  }

  function updateForm<K extends keyof OperacionRequest>(key: K, value: OperacionRequest[K]) {
    setForm((currentForm) => ({
      ...currentForm,
      [key]: value,
    }));
  }

  async function cargarOperaciones() {
    if (!token) {
      return;
    }

    setOperaciones(await listarOperaciones(token));
  }

  async function handleDownloadPdf(idOperacion: number) {
    if (!token) {
      return;
    }

    const key = `${idOperacion}-pdf`;
    setDownloadingKey(key);
    setError(null);

    try {
      const blob = await descargarOperacionPdf(token, idOperacion);
      downloadBlob(blob, `autofinpe-operacion-${idOperacion}.pdf`);
    } catch (exception) {
      setError(toErrorMessage(exception));
    } finally {
      setDownloadingKey(null);
    }
  }

  return (
    <section className="page-section" aria-labelledby="operaciones-title">
      <PageHeader
        kicker="Operaciones"
        title="Registro de operaciones"
        titleId="operaciones-title"
        action={
          <button className="secondary-button" type="button" onClick={() => void cargarDatos()}>
            Actualizar datos
          </button>
        }
      />

      <div className="work-grid">
        <form className="form-panel" onSubmit={handleSubmit}>
          <div className="panel-heading">
            <h3>Nueva operacion</h3>
          </div>

          <div className="wizard-steps" aria-hidden="true">
            <span className="active">1 Datos</span>
            <span>2 Condiciones</span>
            <span>3 Costos</span>
          </div>

          <FormSection step="1" title="Cliente y activo" description="Selecciona los datos base de la operacion.">
            <label className="form-grid-full">
              <HelpLabel helpKey="cliente">Cliente</HelpLabel>
              <select
                value={form.idCliente}
                onChange={(event) => updateForm('idCliente', Number(event.target.value))}
                required
              >
                <option value={0}>Seleccione</option>
                {clientes.map((cliente) => (
                  <option key={cliente.idCliente} value={cliente.idCliente}>
                    {cliente.dni} - {cliente.nombres} {cliente.apellidos}
                  </option>
                ))}
              </select>
            </label>

            <label className="form-grid-full">
              <HelpLabel helpKey="vehiculo">Vehiculo</HelpLabel>
              <select
                value={form.idVehiculo}
                onChange={(event) => updateForm('idVehiculo', Number(event.target.value))}
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
                value={form.idConfiguracion}
                onChange={(event) => updateForm('idConfiguracion', Number(event.target.value))}
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
          </FormSection>

          <FormSection step="2" title="Condiciones del credito" description="Define moneda, plazo, tasa y cuotas.">
            <label>
              <HelpLabel helpKey="moneda">Moneda</HelpLabel>
              <select value={form.moneda} onChange={(event) => updateForm('moneda', event.target.value)} required>
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
              <select
                value={form.tipoTasa}
                onChange={(event) => updateForm('tipoTasa', event.target.value)}
                required
              >
                <option value="E">Efectiva</option>
                <option value="N">Nominal</option>
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
          </FormSection>

          <FormSection step="3" title="Seguros y costos" description="Completa los costos que impactan el cronograma.">
            <label>
              <HelpLabel helpKey="seguroDesgravamen">Seguro desgravamen (%)</HelpLabel>
              <input
                min="0"
                step="0.01"
                type="number"
                value={form.seguroDesgravamenPct}
                onChange={(event) => updateForm('seguroDesgravamenPct', Number(event.target.value))}
                required
              />
            </label>

            <label>
              <HelpLabel helpKey="seguroVehicular">Seguro vehicular (%)</HelpLabel>
              <input
                min="0"
                step="0.01"
                type="number"
                value={form.seguroVehicularPct}
                onChange={(event) => updateForm('seguroVehicularPct', Number(event.target.value))}
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
                min="0"
                step="0.01"
                type="number"
                value={form.cokAnualPct}
                onChange={(event) => updateForm('cokAnualPct', Number(event.target.value))}
                required
              />
            </label>
          </FormSection>

          {error && <StatusMessage type="error">{error}</StatusMessage>}
          {successMessage && <StatusMessage type="success">{successMessage}</StatusMessage>}
          {isLoading && <StatusMessage type="info">Cargando datos disponibles...</StatusMessage>}

          <ActionBar align="end">
            <button className="primary-button" type="submit" disabled={isSaving || isLoading}>
              {isSaving ? 'Registrando...' : 'Registrar operacion'}
            </button>
          </ActionBar>
        </form>

        <Panel title="Resultado">
          {!operacion && <EmptyState>No hay una operacion registrada en esta sesion.</EmptyState>}

          {operacion && (
            <>
              <MetricStrip
                items={[
                  { label: 'Operacion', value: `#${operacion.idOperacion}` },
                  { label: 'Plazo', value: `${operacion.plazo} meses` },
                  { label: 'Estado', value: operacion.estado, tone: 'positive' },
                ]}
              />
              <table className="summary-table">
                <tbody>
                  <tr>
                    <th>ID</th>
                    <td>{operacion.idOperacion}</td>
                  </tr>
                  <tr>
                    <th>Cliente</th>
                    <td>{operacion.clienteNombre}</td>
                  </tr>
                  <tr>
                    <th>Vehiculo</th>
                    <td>{operacion.vehiculoModelo}</td>
                  </tr>
                  <tr>
                    <th>Plazo</th>
                    <td>{operacion.plazo} meses</td>
                  </tr>
                  <tr>
                    <th>Estado</th>
                    <td>{operacion.estado}</td>
                  </tr>
                </tbody>
              </table>

              <div className="button-row">
                <button
                  className="secondary-button"
                  type="button"
                  disabled={downloadingKey === `${operacion.idOperacion}-pdf`}
                  onClick={() => void handleDownloadPdf(operacion.idOperacion)}
                >
                  {downloadingKey === `${operacion.idOperacion}-pdf` ? 'Descargando...' : 'Descargar PDF'}
                </button>
              </div>

              {indicadores && (
                <>
                  <div className="panel-heading">
                    <h3>Indicadores</h3>
                  </div>
                  <MetricStrip
                    items={[
                      { label: 'VAN', value: formatMoney(indicadores.van), tone: 'positive' },
                      { label: 'TIR', value: formatPercent(indicadores.tir) },
                      { label: 'TCEA', value: formatPercent(indicadores.tcea), tone: 'warning' },
                      { label: 'Total pagado', value: formatMoney(indicadores.totalPagado) },
                    ]}
                  />
                  <table className="summary-table">
                    <tbody>
                      <tr>
                        <th>Total intereses</th>
                        <td>{formatMoney(indicadores.totalIntereses)}</td>
                      </tr>
                      <tr>
                        <th>Total amortizacion</th>
                        <td>{formatMoney(indicadores.totalAmortizacion)}</td>
                      </tr>
                      <tr>
                        <th>Total seguros</th>
                        <td>{formatMoney(indicadores.totalSeguros)}</td>
                      </tr>
                      <tr>
                        <th>Total portes</th>
                        <td>{formatMoney(indicadores.totalPortes)}</td>
                      </tr>
                    </tbody>
                  </table>
                </>
              )}
            </>
          )}
        </Panel>
      </div>

      {cronograma.length > 0 && (
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
                {cronograma.map((cuota) => (
                  <tr key={cuota.nroCuota}>
                    <td>{cuota.nroCuota}</td>
                    <td>{formatMoney(cuota.saldoInicial)}</td>
                    <td>{formatMoney(cuota.interes)}</td>
                    <td>{formatMoney(cuota.amortizacion)}</td>
                    <td>{formatMoney(cuota.seguroDesgrav)}</td>
                    <td>{formatMoney(cuota.seguroVehic)}</td>
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

      <Panel title="Operaciones registradas" className="dashboard-panel">
        {operaciones.length === 0 && <EmptyState>No hay operaciones registradas.</EmptyState>}
        {operaciones.length > 0 && (
          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Fecha</th>
                  <th>Cliente</th>
                  <th>Vehiculo</th>
                  <th>Plazo</th>
                  <th>Estado</th>
                  <th>TCEA</th>
                  <th>Exportar</th>
                </tr>
              </thead>
              <tbody>
                {operaciones.map((item) => (
                  <tr key={item.idOperacion}>
                    <td>{item.idOperacion}</td>
                    <td>{formatDate(item.fecha)}</td>
                    <td>{item.clienteNombre}</td>
                    <td>{item.vehiculoModelo}</td>
                    <td>{item.plazo} meses</td>
                    <td>{item.estado}</td>
                    <td>{item.indicador ? formatPercent(item.indicador.tcea) : 'Pendiente'}</td>
                    <td>
                      <div className="button-row">
                        <button
                          className="secondary-button"
                          type="button"
                          disabled={downloadingKey === `${item.idOperacion}-pdf`}
                          onClick={() => void handleDownloadPdf(item.idOperacion)}
                        >
                          PDF
                        </button>
                      </div>
                    </td>
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

function normalizarFormulario(form: OperacionRequest): OperacionRequest {
  return {
    idCliente: Number(form.idCliente),
    idVehiculo: Number(form.idVehiculo),
    idConfiguracion: Number(form.idConfiguracion),
    moneda: form.moneda,
    plazoMeses: Number(form.plazoMeses),
    cuotaInicialPct: Number(form.cuotaInicialPct),
    cuotaBalonPct: Number(form.cuotaBalonPct),
    valorTasaPct: Number(form.valorTasaPct),
    tipoTasa: form.tipoTasa,
    tipoGracia: form.tipoGracia,
    mesesGracia: Number(form.mesesGracia),
    seguroDesgravamenPct: Number(form.seguroDesgravamenPct),
    seguroVehicularPct: Number(form.seguroVehicularPct),
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

function formatDate(value: string) {
  return new Intl.DateTimeFormat('es-PE', {
    dateStyle: 'short',
    timeStyle: 'short',
  }).format(new Date(value));
}

function downloadBlob(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  link.style.display = 'none';
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.setTimeout(() => URL.revokeObjectURL(url), 1000);
}

function toErrorMessage(exception: unknown) {
  return exception instanceof Error ? exception.message : 'Ocurrio un error inesperado';
}
