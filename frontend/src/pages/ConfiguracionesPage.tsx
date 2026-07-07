import { FormEvent, useEffect, useMemo, useState } from 'react';
import { EmptyState } from '../components/ui/EmptyState';
import { HelpLabel } from '../components/ui/HelpLabel';
import { PageHeader } from '../components/ui/PageHeader';
import { Panel } from '../components/ui/Panel';
import { StatusMessage } from '../components/ui/StatusMessage';
import { useAuth } from '../context/AuthContext';
import {
  actualizarConfiguracion,
  crearConfiguracion,
  eliminarConfiguracion,
  listarConfiguraciones,
} from '../services/configuracionService';
import type { ConfiguracionRequest, ConfiguracionResponse } from '../types/configuracion';

const MONEDAS = ['PEN', 'USD'];
const TIPOS_TASA = [
  { value: 'N', label: 'Nominal' },
  { value: 'E', label: 'Efectiva' },
];
const CAPITALIZACIONES = [1, 2, 4, 12, 365];
const TIPOS_GRACIA = [
  { value: 'S', label: 'Sin gracia' },
  { value: 'T', label: 'Gracia total' },
  { value: 'P', label: 'Gracia parcial' },
];

const EMPTY_FORM: ConfiguracionRequest = {
  moneda: 'PEN',
  tipoTasa: 'E',
  capitalizacion: 12,
  tipoGracia: 'S',
  mesesGracia: 0,
};

export function ConfiguracionesPage() {
  const { token } = useAuth();
  const [configuraciones, setConfiguraciones] = useState<ConfiguracionResponse[]>([]);
  const [selectedConfiguracion, setSelectedConfiguracion] = useState<ConfiguracionResponse | null>(null);
  const [form, setForm] = useState<ConfiguracionRequest>(EMPTY_FORM);
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const isEditing = useMemo(() => Boolean(selectedConfiguracion), [selectedConfiguracion]);

  useEffect(() => {
    void cargarConfiguraciones();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  async function cargarConfiguraciones() {
    if (!token) {
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      setConfiguraciones(await listarConfiguraciones(token));
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

      if (selectedConfiguracion) {
        await actualizarConfiguracion(token, selectedConfiguracion.idConfig, request);
        setSuccessMessage('Configuracion actualizada correctamente');
      } else {
        await crearConfiguracion(token, request);
        setSuccessMessage('Configuracion creada correctamente');
      }

      limpiarFormulario();
      await cargarConfiguraciones();
    } catch (exception) {
      setError(toErrorMessage(exception));
    } finally {
      setIsSaving(false);
    }
  }

  async function handleEliminar(configuracion: ConfiguracionResponse) {
    if (!token) {
      return;
    }

    const confirmar = window.confirm(
      `¿Eliminar la configuracion ${configuracion.moneda} ${configuracion.tipoTasa}?`,
    );
    if (!confirmar) {
      return;
    }

    setError(null);
    setSuccessMessage(null);

    try {
      await eliminarConfiguracion(token, configuracion.idConfig);
      setSuccessMessage('Configuracion eliminada correctamente');
      if (selectedConfiguracion?.idConfig === configuracion.idConfig) {
        limpiarFormulario();
      }
      await cargarConfiguraciones();
    } catch (exception) {
      setError(toErrorMessage(exception));
    }
  }

  function editarConfiguracion(configuracion: ConfiguracionResponse) {
    setSelectedConfiguracion(configuracion);
    setForm({
      moneda: configuracion.moneda,
      tipoTasa: configuracion.tipoTasa,
      capitalizacion: Number(configuracion.capitalizacion),
      tipoGracia: configuracion.tipoGracia,
      mesesGracia: Number(configuracion.mesesGracia),
    });
    setError(null);
    setSuccessMessage(null);
  }

  function limpiarFormulario() {
    setSelectedConfiguracion(null);
    setForm(EMPTY_FORM);
  }

  function updateForm<K extends keyof ConfiguracionRequest>(key: K, value: ConfiguracionRequest[K]) {
    setForm((currentForm) => ({
      ...currentForm,
      [key]: value,
    }));
  }

  return (
    <section className="page-section" aria-labelledby="configuraciones-title">
      <PageHeader
        kicker="Configuraciones"
        title="Gestion de configuraciones"
        titleId="configuraciones-title"
        action={
          <button className="secondary-button" type="button" onClick={() => void cargarConfiguraciones()}>
            Actualizar
          </button>
        }
      />

      <div className="work-grid">
        <form className="form-panel" onSubmit={handleSubmit}>
          <div className="panel-heading">
            <h3>{isEditing ? 'Editar configuracion' : 'Nueva configuracion'}</h3>
          </div>

          <div className="form-grid">
            <label>
              Moneda
              <select
                value={form.moneda}
                onChange={(event) => updateForm('moneda', event.target.value)}
                required
              >
                {MONEDAS.map((moneda) => (
                  <option key={moneda} value={moneda}>
                    {moneda}
                  </option>
                ))}
              </select>
            </label>

            <label>
              <HelpLabel helpKey="tipoTasa">Tipo de tasa</HelpLabel>
              <select
                value={form.tipoTasa}
                onChange={(event) => updateForm('tipoTasa', event.target.value)}
                required
              >
                {TIPOS_TASA.map((tipoTasa) => (
                  <option key={tipoTasa.value} value={tipoTasa.value}>
                    {tipoTasa.label}
                  </option>
                ))}
              </select>
            </label>

            <label>
              <HelpLabel helpKey="capitalizacion">Capitalizacion</HelpLabel>
              <select
                value={form.capitalizacion}
                onChange={(event) => updateForm('capitalizacion', Number(event.target.value))}
                required
              >
                {CAPITALIZACIONES.map((capitalizacion) => (
                  <option key={capitalizacion} value={capitalizacion}>
                    {capitalizacion}
                  </option>
                ))}
              </select>
            </label>

            <label>
              <HelpLabel helpKey="periodoGracia">Tipo de gracia</HelpLabel>
              <select
                value={form.tipoGracia}
                onChange={(event) => updateForm('tipoGracia', event.target.value)}
                required
              >
                {TIPOS_GRACIA.map((tipoGracia) => (
                  <option key={tipoGracia.value} value={tipoGracia.value}>
                    {tipoGracia.label}
                  </option>
                ))}
              </select>
            </label>

            <label className="form-grid-full">
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
          </div>

          {error && <StatusMessage type="error">{error}</StatusMessage>}
          {successMessage && <StatusMessage type="success">{successMessage}</StatusMessage>}

          <div className="button-row">
            <button className="primary-button" type="submit" disabled={isSaving}>
              {isSaving ? 'Guardando...' : isEditing ? 'Actualizar configuracion' : 'Crear configuracion'}
            </button>
            {isEditing && (
              <button className="secondary-button" type="button" onClick={limpiarFormulario}>
                Cancelar
              </button>
            )}
          </div>
        </form>

        <Panel title="Listado">
          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>Moneda</th>
                  <th>Tipo tasa</th>
                  <th>Capitalizacion</th>
                  <th>Tipo gracia</th>
                  <th>Meses gracia</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {configuraciones.map((configuracion) => (
                  <tr key={configuracion.idConfig}>
                    <td>{configuracion.moneda}</td>
                    <td>{formatTipoTasa(configuracion.tipoTasa)}</td>
                    <td>{configuracion.capitalizacion}</td>
                    <td>{formatTipoGracia(configuracion.tipoGracia)}</td>
                    <td>{configuracion.mesesGracia}</td>
                    <td>
                      <div className="table-actions">
                        <button
                          className="text-button"
                          type="button"
                          onClick={() => editarConfiguracion(configuracion)}
                        >
                          Editar
                        </button>
                        <button
                          className="danger-button"
                          type="button"
                          onClick={() => void handleEliminar(configuracion)}
                        >
                          Eliminar
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}

                {!isLoading && configuraciones.length === 0 && (
                  <tr>
                    <td colSpan={6}>
                      <EmptyState>No hay configuraciones para mostrar.</EmptyState>
                    </td>
                  </tr>
                )}

                {isLoading && (
                  <tr>
                    <td colSpan={6}>Cargando configuraciones...</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </Panel>
      </div>
    </section>
  );
}

function normalizarFormulario(form: ConfiguracionRequest): ConfiguracionRequest {
  return {
    moneda: form.moneda,
    tipoTasa: form.tipoTasa,
    capitalizacion: Number(form.capitalizacion),
    tipoGracia: form.tipoGracia,
    mesesGracia: Number(form.mesesGracia),
  };
}

function formatTipoTasa(tipoTasa: string) {
  return tipoTasa === 'N' ? 'Nominal' : 'Efectiva';
}

function formatTipoGracia(tipoGracia: string) {
  if (tipoGracia === 'T') {
    return 'Gracia total';
  }
  if (tipoGracia === 'P') {
    return 'Gracia parcial';
  }
  return 'Sin gracia';
}

function toErrorMessage(exception: unknown) {
  return exception instanceof Error ? exception.message : 'Ocurrio un error inesperado';
}
