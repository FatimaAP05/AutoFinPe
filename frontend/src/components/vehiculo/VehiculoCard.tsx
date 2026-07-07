import type { SyntheticEvent } from 'react';
import type { VehiculoResponse } from '../../types/vehiculo';
import { formatCurrency } from '../../utils/formatters';
import './VehiculoCard.css';

const DEFAULT_IMAGE =
  'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 640 400%22%3E%3Crect width=%22640%22 height=%22400%22 fill=%22%23e9f2fb%22/%3E%3Cpath d=%22M142 244h28l35-58c8-13 22-22 38-22h154c16 0 30 9 38 22l35 58h28c14 0 26 12 26 26v46h-48a44 44 0 0 1-88 0H252a44 44 0 0 1-88 0h-48v-46c0-14 12-26 26-26Zm88-43-26 43h232l-26-43c-3-6-9-9-16-9H246c-7 0-13 3-16 9Z%22 fill=%22%231769aa%22/%3E%3Ccircle cx=%22208%22 cy=%22316%22 r=%2224%22 fill=%22%23ffffff%22/%3E%3Ccircle cx=%22432%22 cy=%22316%22 r=%2224%22 fill=%22%23ffffff%22/%3E%3C/svg%3E';

interface VehiculoCardProps {
  vehiculo: VehiculoResponse;
  onEdit: (vehiculo: VehiculoResponse) => void;
  onDelete: (vehiculo: VehiculoResponse) => void;
}

export function VehiculoCard({ vehiculo, onEdit, onDelete }: VehiculoCardProps) {
  function handleImageError(event: SyntheticEvent<HTMLImageElement>) {
    event.currentTarget.src = DEFAULT_IMAGE;
  }

  return (
    <article className="vehiculo-card">
      <div className="vehiculo-card-image-container">
        <img
          src={vehiculo.imagenUrl ?? DEFAULT_IMAGE}
          alt={`${vehiculo.marca} ${vehiculo.modelo}`}
          loading="lazy"
          onError={handleImageError}
        />
      </div>

      <div className="vehiculo-card-content">
        <header>
          <span className="vehiculo-card-category">{vehiculo.categoria}</span>
          <h4 className="vehiculo-card-title">
            {vehiculo.marca} <strong>{vehiculo.modelo}</strong>
          </h4>
          <p className="vehiculo-card-year">{vehiculo.anio}</p>
        </header>

        <div className="vehiculo-card-prices">
          <p>
            <span className="price-label">PEN</span>
            {formatCurrency(vehiculo.precioPen, 'PEN')}
          </p>
          <p>
            <span className="price-label">USD</span>
            {formatCurrency(vehiculo.precioUsd, 'USD')}
          </p>
        </div>

        <footer className="vehiculo-card-actions">
          <button className="text-button" type="button" onClick={() => onEdit(vehiculo)}>
            Editar
          </button>
          <button className="danger-button" type="button" onClick={() => onDelete(vehiculo)}>
            Eliminar
          </button>
        </footer>
      </div>
    </article>
  );
}
