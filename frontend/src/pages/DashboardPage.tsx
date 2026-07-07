import { useEffect, useState } from 'react';
import { EmptyState } from '../components/ui/EmptyState';
import { MetricStrip } from '../components/ui/MetricStrip';
import { PageHeader } from '../components/ui/PageHeader';
import { Panel } from '../components/ui/Panel';
import { StatCard } from '../components/ui/StatCard';
import { StatusMessage } from '../components/ui/StatusMessage';
import { useAuth } from '../context/AuthContext';
import { listarClientes } from '../services/clienteService';
import { listarConfiguraciones } from '../services/configuracionService';
import { listarOperaciones } from '../services/operacionService';
import { listarVehiculos } from '../services/vehiculoService';
import type { OperacionResponse } from '../types/operacion';

interface DashboardTotals {
  clientes: number;
  vehiculos: number;
  configuraciones: number;
  operaciones: number;
}

const EMPTY_TOTALS: DashboardTotals = {
  clientes: 0,
  vehiculos: 0,
  configuraciones: 0,
  operaciones: 0,
};

export function DashboardPage() {
  const { token } = useAuth();
  const [totals, setTotals] = useState<DashboardTotals>(EMPTY_TOTALS);
  const [ultimasOperaciones, setUltimasOperaciones] = useState<OperacionResponse[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    void cargarResumen();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  async function cargarResumen() {
    if (!token) {
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      const [clientes, vehiculos, configuraciones, operaciones] = await Promise.all([
        listarClientes(token),
        listarVehiculos(token),
        listarConfiguraciones(token),
        listarOperaciones(token),
      ]);

      setTotals({
        clientes: clientes.length,
        vehiculos: vehiculos.length,
        configuraciones: configuraciones.length,
        operaciones: operaciones.length,
      });
      setUltimasOperaciones(operaciones.slice(0, 5));
    } catch (exception) {
      setError(toErrorMessage(exception));
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <section className="page-section" aria-labelledby="dashboard-title">
      <PageHeader
        kicker="Dashboard"
        title="Panel principal"
        titleId="dashboard-title"
        action={
          <button className="secondary-button" type="button" onClick={() => void cargarResumen()}>
            Actualizar resumen
          </button>
        }
      />

      {error && <StatusMessage type="error">{error}</StatusMessage>}
      {isLoading && <StatusMessage type="info">Cargando informacion del sistema...</StatusMessage>}

      <div className="dashboard-hero">
        <div>
          <p className="section-kicker">Resumen operativo</p>
          <h3>Control financiero para créditos vehiculares</h3>
          <p>Monitorea datos maestros, operaciones recientes y accesos criticos desde una vista ejecutiva.</p>
        </div>
        <div className="dashboard-hero-summary">
          <MetricStrip
            items={[
              { label: 'Portafolio', value: `${totals.operaciones} ops`, tone: 'positive' },
              { label: 'Catalogo', value: `${totals.vehiculos} vehiculos` },
              { label: 'Base clientes', value: totals.clientes },
            ]}
          />
          <div className="dashboard-hero-visual" aria-hidden="true">
            <span />
            <span />
            <span />
          </div>
        </div>
      </div>

      <div className="dashboard-grid">
        <StatCard kicker="Clientes" value={totals.clientes} description="Total de clientes registrados" actionLabel="Ver clientes" actionTo="/clientes" />
        <StatCard kicker="Vehiculos" value={totals.vehiculos} description="Total de vehiculos registrados" actionLabel="Ver vehiculos" actionTo="/vehiculos" />
        <StatCard kicker="Configuraciones" value={totals.configuraciones} description="Total de configuraciones registradas" actionLabel="Ver configuraciones" actionTo="/configuraciones" />
        <StatCard kicker="Operaciones" value={totals.operaciones} description="Total de operaciones registradas" actionLabel="Registrar operacion" actionTo="/operaciones" />
      </div>

      <Panel title="Ultimas operaciones" className="dashboard-panel">
        {ultimasOperaciones.length === 0 && <EmptyState>No hay operaciones registradas.</EmptyState>}
        {ultimasOperaciones.length > 0 && (
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
                </tr>
              </thead>
              <tbody>
                {ultimasOperaciones.map((operacion) => (
                  <tr key={operacion.idOperacion}>
                    <td>{operacion.idOperacion}</td>
                    <td>{formatDate(operacion.fecha)}</td>
                    <td>{operacion.clienteNombre}</td>
                    <td>{operacion.vehiculoModelo}</td>
                    <td>{operacion.plazo} meses</td>
                    <td>{operacion.estado}</td>
                    <td>{operacion.indicador ? formatPercent(operacion.indicador.tcea) : 'Pendiente'}</td>
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

function toErrorMessage(exception: unknown) {
  return exception instanceof Error ? exception.message : 'Ocurrio un error inesperado';
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat('es-PE', {
    dateStyle: 'short',
    timeStyle: 'short',
  }).format(new Date(value));
}

function formatPercent(value: number) {
  return `${Number(value).toFixed(4)}%`;
}
