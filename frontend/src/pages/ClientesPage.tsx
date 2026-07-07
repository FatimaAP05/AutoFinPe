import { FormEvent, useEffect, useMemo, useState } from 'react';
import { EmptyState } from '../components/ui/EmptyState';
import { PageHeader } from '../components/ui/PageHeader';
import { Panel } from '../components/ui/Panel';
import { StatusMessage } from '../components/ui/StatusMessage';
import { useAuth } from '../context/AuthContext';
import {
  actualizarCliente,
  buscarClientePorDni,
  crearCliente,
  eliminarCliente,
  listarClientes,
} from '../services/clienteService';
import type { ClienteRequest, ClienteResponse } from '../types/cliente';

const CALIFICACIONES = ['A', 'B', 'C', 'D', 'E', 'SIN_CALIFICAR'];

const EMPTY_FORM: ClienteRequest = {
  dni: '',
  nombres: '',
  apellidos: '',
  ingresoMensual: 0,
  calificacion: 'SIN_CALIFICAR',
  telefono: '',
  email: '',
};

export function ClientesPage() {
  const { token } = useAuth();
  const [clientes, setClientes] = useState<ClienteResponse[]>([]);
  const [selectedCliente, setSelectedCliente] = useState<ClienteResponse | null>(null);
  const [form, setForm] = useState<ClienteRequest>(EMPTY_FORM);
  const [dniBusqueda, setDniBusqueda] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const isEditing = useMemo(() => Boolean(selectedCliente), [selectedCliente]);

  useEffect(() => {
    void cargarClientes();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  async function cargarClientes() {
    if (!token) {
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      setClientes(await listarClientes(token));
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

      if (selectedCliente) {
        await actualizarCliente(token, selectedCliente.idCliente, request);
        setSuccessMessage('Cliente actualizado correctamente');
      } else {
        await crearCliente(token, request);
        setSuccessMessage('Cliente creado correctamente');
      }

      limpiarFormulario();
      await cargarClientes();
    } catch (exception) {
      setError(toErrorMessage(exception));
    } finally {
      setIsSaving(false);
    }
  }

  async function handleBuscarPorDni() {
    if (!token || !dniBusqueda.trim()) {
      await cargarClientes();
      return;
    }

    setIsLoading(true);
    setError(null);
    setSuccessMessage(null);

    try {
      const cliente = await buscarClientePorDni(token, dniBusqueda.trim());
      setClientes([cliente]);
    } catch (exception) {
      setClientes([]);
      setError(toErrorMessage(exception));
    } finally {
      setIsLoading(false);
    }
  }

  async function handleEliminar(cliente: ClienteResponse) {
    if (!token) {
      return;
    }

    const confirmar = window.confirm(`¿Eliminar al cliente ${cliente.nombres} ${cliente.apellidos}?`);
    if (!confirmar) {
      return;
    }

    setError(null);
    setSuccessMessage(null);

    try {
      await eliminarCliente(token, cliente.idCliente);
      setSuccessMessage('Cliente eliminado correctamente');
      if (selectedCliente?.idCliente === cliente.idCliente) {
        limpiarFormulario();
      }
      await cargarClientes();
    } catch (exception) {
      setError(toErrorMessage(exception));
    }
  }

  function editarCliente(cliente: ClienteResponse) {
    setSelectedCliente(cliente);
    setForm({
      dni: cliente.dni,
      nombres: cliente.nombres,
      apellidos: cliente.apellidos,
      ingresoMensual: Number(cliente.ingresoMensual),
      calificacion: cliente.calificacion,
      telefono: cliente.telefono ?? '',
      email: cliente.email ?? '',
    });
    setError(null);
    setSuccessMessage(null);
  }

  function limpiarFormulario() {
    setSelectedCliente(null);
    setForm(EMPTY_FORM);
  }

  function updateForm<K extends keyof ClienteRequest>(key: K, value: ClienteRequest[K]) {
    setForm((currentForm) => ({
      ...currentForm,
      [key]: value,
    }));
  }

  return (
    <section className="page-section" aria-labelledby="clientes-title">
      <PageHeader
        kicker="Clientes"
        title="Gestion de clientes"
        titleId="clientes-title"
        action={
          <button className="secondary-button" type="button" onClick={() => void cargarClientes()}>
            Actualizar
          </button>
        }
      />

      <div className="work-grid">
        <form className="form-panel" onSubmit={handleSubmit}>
          <div className="panel-heading">
            <h3>{isEditing ? 'Editar cliente' : 'Nuevo cliente'}</h3>
          </div>

          <div className="form-grid">
            <label>
              DNI
              <input
                value={form.dni}
                maxLength={8}
                onChange={(event) => updateForm('dni', event.target.value)}
                required
              />
            </label>

            <label>
              Nombres
              <input
                value={form.nombres}
                onChange={(event) => updateForm('nombres', event.target.value)}
                required
              />
            </label>

            <label>
              Apellidos
              <input
                value={form.apellidos}
                onChange={(event) => updateForm('apellidos', event.target.value)}
                required
              />
            </label>

            <label>
              Ingreso mensual
              <input
                min="0"
                step="0.01"
                type="number"
                value={form.ingresoMensual}
                onChange={(event) => updateForm('ingresoMensual', Number(event.target.value))}
                required
              />
            </label>

            <label>
              Calificacion
              <select
                value={form.calificacion}
                onChange={(event) => updateForm('calificacion', event.target.value)}
                required
              >
                {CALIFICACIONES.map((calificacion) => (
                  <option key={calificacion} value={calificacion}>
                    {calificacion}
                  </option>
                ))}
              </select>
            </label>

            <label>
              Telefono
              <input
                value={form.telefono}
                onChange={(event) => updateForm('telefono', event.target.value)}
              />
            </label>

            <label className="form-grid-full">
              Email
              <input
                type="email"
                value={form.email}
                onChange={(event) => updateForm('email', event.target.value)}
              />
            </label>
          </div>

          {error && <StatusMessage type="error">{error}</StatusMessage>}
          {successMessage && <StatusMessage type="success">{successMessage}</StatusMessage>}

          <div className="button-row">
            <button className="primary-button" type="submit" disabled={isSaving}>
              {isSaving ? 'Guardando...' : isEditing ? 'Actualizar cliente' : 'Crear cliente'}
            </button>
            {isEditing && (
              <button className="secondary-button" type="button" onClick={limpiarFormulario}>
                Cancelar
              </button>
            )}
          </div>
        </form>

        <Panel title="Listado">
          <div className="search-row">
            <input
              aria-label="Buscar por DNI"
              maxLength={8}
              placeholder="Buscar por DNI"
              value={dniBusqueda}
              onChange={(event) => setDniBusqueda(event.target.value)}
            />
            <button className="secondary-button" type="button" onClick={() => void handleBuscarPorDni()}>
              Buscar
            </button>
            <button
              className="secondary-button"
              type="button"
              onClick={() => {
                setDniBusqueda('');
                void cargarClientes();
              }}
            >
              Limpiar
            </button>
          </div>

          <div className="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>DNI</th>
                  <th>Cliente</th>
                  <th>Ingreso</th>
                  <th>Calificacion</th>
                  <th>Contacto</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {clientes.map((cliente) => (
                  <tr key={cliente.idCliente}>
                    <td>{cliente.dni}</td>
                    <td>
                      <strong>{cliente.nombres}</strong>
                      <span>{cliente.apellidos}</span>
                    </td>
                    <td>{formatCurrency(cliente.ingresoMensual)}</td>
                    <td>{cliente.calificacion}</td>
                    <td>
                      <span>{cliente.telefono || '-'}</span>
                      <span>{cliente.email || '-'}</span>
                    </td>
                    <td>
                      <div className="table-actions">
                        <button className="text-button" type="button" onClick={() => editarCliente(cliente)}>
                          Editar
                        </button>
                        <button
                          className="danger-button"
                          type="button"
                          onClick={() => void handleEliminar(cliente)}
                        >
                          Eliminar
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}

                {!isLoading && clientes.length === 0 && (
                  <tr>
                    <td colSpan={6}>
                      <EmptyState>No hay clientes para mostrar.</EmptyState>
                    </td>
                  </tr>
                )}

                {isLoading && (
                  <tr>
                    <td colSpan={6}>Cargando clientes...</td>
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

function normalizarFormulario(form: ClienteRequest): ClienteRequest {
  return {
    dni: form.dni.trim(),
    nombres: form.nombres.trim(),
    apellidos: form.apellidos.trim(),
    ingresoMensual: Number(form.ingresoMensual),
    calificacion: form.calificacion,
    telefono: form.telefono?.trim() || undefined,
    email: form.email?.trim() || undefined,
  };
}

function formatCurrency(value: number) {
  return new Intl.NumberFormat('es-PE', {
    currency: 'PEN',
    style: 'currency',
  }).format(value);
}

function toErrorMessage(exception: unknown) {
  return exception instanceof Error ? exception.message : 'Ocurrio un error inesperado';
}
