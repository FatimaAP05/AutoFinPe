import { Link } from 'react-router-dom';

export function NotFoundPage() {
  return (
    <main className="login-shell">
      <section className="login-panel" aria-labelledby="not-found-title">
        <div className="login-heading">
          <p className="brand-kicker">AutoFinPe</p>
          <h1 id="not-found-title">Pagina no encontrada</h1>
          <p>La ruta solicitada no existe.</p>
        </div>
        <Link className="primary-button link-button" to="/">
          Volver
        </Link>
      </section>
    </main>
  );
}
