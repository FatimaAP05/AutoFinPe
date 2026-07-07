import { FormEvent, useEffect, useMemo, useRef, useState } from 'react';
import { EmptyState } from '../components/ui/EmptyState';
import { HelpLabel } from '../components/ui/HelpLabel';
import { PageHeader } from '../components/ui/PageHeader';
import { Panel } from '../components/ui/Panel';
import { StatusMessage } from '../components/ui/StatusMessage';
import { VehiculoCard } from '../components/vehiculo/VehiculoCard';
import { useAuth } from '../context/AuthContext';
import {
  actualizarVehiculo,
  buscarVehiculosPorCategoria,
  buscarVehiculosPorMarca,
  crearVehiculo,
  eliminarVehiculo,
  listarVehiculos,
  subirImagenVehiculo,
} from '../services/vehiculoService';
import type { VehiculoRequest, VehiculoResponse } from '../types/vehiculo';
import { toErrorMessage } from '../utils/error';

const EMPTY_FORM: VehiculoRequest = {
  marca: '',
  modelo: '',
  anio: new Date().getFullYear(),
  precioPen: 0,
  precioUsd: 0,
  categoria: '',
};

export function VehiculosPage() {
  const { token } = useAuth();
  const [vehiculos, setVehiculos] = useState<VehiculoResponse[]>([]);
  const [selectedVehiculo, setSelectedVehiculo] = useState<VehiculoResponse | null>(null);
  const [imagenFile, setImagenFile] = useState<File | null>(null);
  const [form, setForm] = useState<VehiculoRequest>(EMPTY_FORM);
  const [marcaBusqueda, setMarcaBusqueda] = useState('');
  const [categoriaBusqueda, setCategoriaBusqueda] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [emptyMessage, setEmptyMessage] = useState('No hay vehiculos para mostrar.');
  const fileInputRef = useRef<HTMLInputElement>(null);

  const isEditing = useMemo(() => Boolean(selectedVehiculo), [selectedVehiculo]);

  useEffect(() => {
    void cargarVehiculos();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  async function cargarVehiculos() {
    if (!token) {
      return;
    }

    setIsLoading(true);
    setError(null);
    setEmptyMessage('No hay vehiculos para mostrar.');

    try {
      setVehiculos(await listarVehiculos(token));
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

      let vehiculoGuardado: VehiculoResponse;

      if (selectedVehiculo) {
        vehiculoGuardado = await actualizarVehiculo(token, selectedVehiculo.idVehiculo, request);
        setSuccessMessage('Vehiculo actualizado correctamente');
      } else {
        vehiculoGuardado = await crearVehiculo(token, request);
        setSuccessMessage('Vehiculo creado correctamente');
      }

      // Paso 2: Subir la imagen si se seleccionó una
      if (imagenFile) {
        await subirImagenVehiculo(token, vehiculoGuardado.idVehiculo, imagenFile);
      }

      limpiarFormulario();
      await cargarVehiculos();
    } catch (exception) {
      setError(toErrorMessage(exception));
    } finally {
      setIsSaving(false);
    }
  }

  async function handleBuscarPorMarca() {
    if (!token || !marcaBusqueda.trim()) {
      await cargarVehiculos();
      return;
    }

    setIsLoading(true);
    setError(null);
    setSuccessMessage(null);

    try {
      const resultados = await buscarVehiculosPorMarca(token, marcaBusqueda.trim());
      setVehiculos(resultados);
      setEmptyMessage('No se encontraron vehiculos para la marca indicada.');
    } catch (exception) {
      setVehiculos([]);
      setError(toErrorMessage(exception));
    } finally {
      setIsLoading(false);
    }
  }

  async function handleBuscarPorCategoria() {
    if (!token || !categoriaBusqueda.trim()) {
      await cargarVehiculos();
      return;
    }

    setIsLoading(true);
    setError(null);
    setSuccessMessage(null);

    try {
      const resultados = await buscarVehiculosPorCategoria(token, categoriaBusqueda.trim());
      setVehiculos(resultados);
      setEmptyMessage('No se encontraron vehiculos para la categoria indicada.');
    } catch (exception) {
      setVehiculos([]);
      setError(toErrorMessage(exception));
    } finally {
      setIsLoading(false);
    }
  }

  async function handleEliminar(vehiculo: VehiculoResponse) {
    if (!token) {
      return;
    }

    const confirmar = window.confirm(`¿Eliminar el vehiculo ${vehiculo.marca} ${vehiculo.modelo}?`);
    if (!confirmar) {
      return;
    }

    setError(null);
    setSuccessMessage(null);

    try {
      await eliminarVehiculo(token, vehiculo.idVehiculo);
      setSuccessMessage('Vehiculo eliminado correctamente');
      if (selectedVehiculo?.idVehiculo === vehiculo.idVehiculo) {
        limpiarFormulario();
      }
      await cargarVehiculos();
    } catch (exception) {
      setError(toErrorMessage(exception));
    }
  }

  function editarVehiculo(vehiculo: VehiculoResponse) {
    setSelectedVehiculo(vehiculo);
    setForm({
      marca: vehiculo.marca,
      modelo: vehiculo.modelo,
      anio: Number(vehiculo.anio),
      precioPen: Number(vehiculo.precioPen),
      precioUsd: Number(vehiculo.precioUsd),
      categoria: vehiculo.categoria,
    });
    setError(null);
    setSuccessMessage(null);
  }

  function limpiarFormulario() {
    setSelectedVehiculo(null);
    setForm(EMPTY_FORM);
    setImagenFile(null);
    setError(null);
    setSuccessMessage(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  }

  function limpiarBusqueda() {
    setMarcaBusqueda('');
    setCategoriaBusqueda('');
    void cargarVehiculos();
  }

  function updateForm<K extends keyof VehiculoRequest>(key: K, value: VehiculoRequest[K]) {
    setError(null);
    setSuccessMessage(null);
    setForm((currentForm) => ({
      ...currentForm,
      [key]: value,
    }));
  }

  function handleFileChange(event: React.ChangeEvent<HTMLInputElement>) {
    setError(null);
    setSuccessMessage(null);
    const file = event.target.files?.[0];
    if (file) {
      setImagenFile(file);
    }
  }

  return (
    <section className="page-section" aria-labelledby="vehiculos-title">
      <PageHeader
        kicker="Vehiculos"
        title="Gestion de vehiculos"
        titleId="vehiculos-title"
        action={
          <button className="secondary-button" type="button" onClick={() => void cargarVehiculos()}>
            Actualizar
          </button>
        }
      />

      <div className="work-grid">
        <form className="form-panel catalog-form-panel" onSubmit={handleSubmit}>
          <div className="panel-heading">
            <h3>{isEditing ? 'Editar vehiculo' : 'Nuevo vehiculo'}</h3>
          </div>

          <div className="form-grid">
            <label>
              Marca
              <input
                maxLength={30}
                value={form.marca}
                onChange={(event) => updateForm('marca', event.target.value)}
                required
              />
            </label>

            <label>
              Modelo
              <input
                maxLength={30}
                value={form.modelo}
                onChange={(event) => updateForm('modelo', event.target.value)}
                required
              />
            </label>

            <label>
              Año
              <input
                max="2155"
                min="2000"
                type="number"
                value={form.anio}
                onChange={(event) => updateForm('anio', event.target.valueAsNumber)}
                required
              />
            </label>

            <label>
              <HelpLabel helpKey="precioVehiculo">Precio PEN</HelpLabel>
              <input
                min="0.01"
                step="0.01"
                type="number"
                value={form.precioPen}
                onChange={(event) => updateForm('precioPen', event.target.valueAsNumber)}
                required
              />
            </label>

            <label>
              <HelpLabel helpKey="precioVehiculo">Precio USD</HelpLabel>
              <input
                min="0.01"
                step="0.01"
                type="number"
                value={form.precioUsd}
                onChange={(event) => updateForm('precioUsd', event.target.valueAsNumber)}
                required
              />
            </label>

            <label>
              <HelpLabel helpKey="categoriaVehiculo">Categoria</HelpLabel>
              <input
                maxLength={30}
                value={form.categoria}
                onChange={(event) => updateForm('categoria', event.target.value)}
                required
              />
            </label>

            <label className="form-grid-full">
              Imagen (JPG, PNG, WEBP)
              <input
                ref={fileInputRef}
                type="file"
                accept="image/jpeg,image/png,image/webp"
                onChange={handleFileChange}
              />
            </label>
          </div>

          {error && <StatusMessage type="error">{error}</StatusMessage>}
          {successMessage && <StatusMessage type="success">{successMessage}</StatusMessage>}

          <div className="button-row">
            <button className="primary-button" type="submit" disabled={isSaving}>
              {isSaving ? 'Guardando...' : isEditing ? 'Actualizar vehiculo' : 'Crear vehiculo'}
            </button>
            {isEditing && (
              <button className="secondary-button" type="button" onClick={limpiarFormulario}>
                Cancelar
              </button>
            )}
          </div>
        </form>

        <Panel title="Catalogo disponible" className="catalog-panel">
          <div className="search-row">
            <input
              aria-label="Buscar por marca"
              maxLength={30}
              placeholder="Buscar por marca"
              value={marcaBusqueda}
              onChange={(event) => setMarcaBusqueda(event.target.value)}
            />
            <button className="secondary-button" type="button" onClick={() => void handleBuscarPorMarca()}>
              Buscar marca
            </button>
            <input
              aria-label="Buscar por categoria"
              maxLength={30}
              placeholder="Buscar por categoria"
              value={categoriaBusqueda}
              onChange={(event) => setCategoriaBusqueda(event.target.value)}
            />
            <button className="secondary-button" type="button" onClick={() => void handleBuscarPorCategoria()}>
              Buscar categoria
            </button>
            <button className="secondary-button" type="button" onClick={limpiarBusqueda}>
              Limpiar
            </button>
          </div>

          <div className="card-grid">
            {vehiculos.map((vehiculo) => (
              <VehiculoCard
                key={vehiculo.idVehiculo}
                vehiculo={vehiculo}
                onEdit={editarVehiculo}
                onDelete={() => void handleEliminar(vehiculo)}
              />
            ))}
          </div>

          {!isLoading && vehiculos.length === 0 && (
            <EmptyState>{emptyMessage}</EmptyState>
          )}

          {isLoading && (
            <p className="loading-text">Cargando vehiculos...</p>
          )}
        </Panel>
      </div>
    </section>
  );
}

function normalizarFormulario(form: VehiculoRequest): VehiculoRequest {
  return {
    marca: form.marca.trim(),
    modelo: form.modelo.trim(),
    anio: Number(form.anio),
    precioPen: Number(form.precioPen),
    precioUsd: Number(form.precioUsd),
    categoria: form.categoria.trim(),
  };
}
