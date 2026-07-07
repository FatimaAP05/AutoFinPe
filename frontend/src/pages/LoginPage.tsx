import { FormEvent, useState } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { PasswordField } from '../components/ui/PasswordField';
import { useAuth } from '../context/AuthContext';

interface LocationState {
  from?: {
    pathname?: string;
  };
}

export function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { isAuthenticated, signIn } = useAuth();
  const [login, setLogin] = useState('');
  const [clave, setClave] = useState('');
  const [rememberSession, setRememberSession] = useState(() => localStorage.getItem('autofinpe_remember') === 'true');
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const redirectPath = (location.state as LocationState | null)?.from?.pathname ?? '/dashboard';

  if (isAuthenticated) {
    return <Navigate to={redirectPath} replace />;
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    setIsSubmitting(true);

    try {
      localStorage.setItem('autofinpe_remember', String(rememberSession));
      await signIn(login.trim(), clave);
      navigate(redirectPath, { replace: true });
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'No se pudo iniciar sesion');
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="login-shell">
      <section className="login-card" aria-labelledby="login-title">
        <div className="login-visual">
          <div className="login-brand-lockup">
            <div className="brand-mark login-brand-mark" aria-hidden="true">
              AF
            </div>
            <div>
              <p className="brand-kicker">AutoFinPe</p>
              <h1>Financiamiento vehicular</h1>
            </div>
          </div>
          <div className="vehicle-illustration" aria-hidden="true">
            <div className="vehicle-sun" />
            <div className="vehicle-card-shadow" />
            <div className="vehicle-body">
              <span className="vehicle-window" />
              <span className="vehicle-front" />
              <span className="vehicle-wheel vehicle-wheel-left" />
              <span className="vehicle-wheel vehicle-wheel-right" />
            </div>
          </div>
          <div className="login-visual-copy">
            <span>Crédito inteligente</span>
            <strong>Evalúa cuotas, costos y escenarios con una vista financiera clara.</strong>
          </div>
        </div>

        <div className="login-panel">
          <div className="login-heading">
            <p className="brand-kicker">Acceso seguro</p>
            <h2 id="login-title">Iniciar sesion</h2>
            <p>Gestiona clientes, vehiculos, simulaciones y operaciones desde un entorno protegido.</p>
          </div>

          <form className="login-form" onSubmit={handleSubmit}>
            <label htmlFor="login">
              <span>Usuario</span>
              <input
                id="login"
                name="login"
                autoComplete="username"
                value={login}
                onChange={(event) => setLogin(event.target.value)}
                required
              />
            </label>

            <PasswordField id="clave" name="clave" value={clave} onChange={setClave} required />

            <label className="checkbox-row">
              <input
                type="checkbox"
                checked={rememberSession}
                onChange={(event) => setRememberSession(event.target.checked)}
              />
              <span>Recordar sesión en este equipo</span>
            </label>

            {error && <p className="form-error">{error}</p>}

            <button className="primary-button" type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Validando...' : 'Ingresar a AutoFinPe'}
            </button>
          </form>
        </div>
      </section>
    </main>
  );
}
