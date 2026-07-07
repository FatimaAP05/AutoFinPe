import { type ReactNode, useEffect, useState } from 'react';
import { NavLink, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const NAV_ITEMS = [
  { to: '/dashboard', icon: 'dashboard', label: 'Dashboard' },
  { to: '/clientes', icon: 'clientes', label: 'Clientes' },
  { to: '/vehiculos', icon: 'vehiculos', label: 'Vehiculos' },
  { to: '/configuraciones', icon: 'configuraciones', label: 'Configuraciones' },
  { to: '/operaciones', icon: 'operaciones', label: 'Operaciones' },
  { to: '/simulador', icon: 'simulador', label: 'Simulador' },
  { to: '/comparador', icon: 'comparador', label: 'Comparador' },
];

export function MainLayout() {
  const { usuario, signOut } = useAuth();
  const location = useLocation();
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState(false);
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);

  useEffect(() => {
    setIsDrawerOpen(false);
  }, [location.pathname]);

  return (
    <div className={`app-layout${isSidebarCollapsed ? ' sidebar-collapsed' : ''}${isDrawerOpen ? ' sidebar-open' : ''}`}>
      <button
        className="sidebar-toggle"
        type="button"
        aria-label={isDrawerOpen ? 'Cerrar menu' : 'Abrir menu'}
        aria-expanded={isDrawerOpen}
        onClick={() => setIsDrawerOpen((current) => !current)}
      >
        <span />
        <span />
        <span />
      </button>

      <button
        className="sidebar-overlay"
        type="button"
        aria-label="Cerrar menu lateral"
        tabIndex={isDrawerOpen ? 0 : -1}
        onClick={() => setIsDrawerOpen(false)}
      />

      <aside className="sidebar" aria-label="Navegacion principal">
        <div className="brand-block">
          <div className="brand-mark" aria-hidden="true">
            AF
          </div>
          <div className="brand-copy">
            <p className="brand-kicker">AutoFinPe</p>
            <h1 className="brand-title">Gestion crediticia</h1>
          </div>
        </div>

        <button
          className="sidebar-collapse-button"
          type="button"
          aria-label={isSidebarCollapsed ? 'Expandir menu lateral' : 'Colapsar menu lateral'}
          aria-pressed={isSidebarCollapsed}
          onClick={() => setIsSidebarCollapsed((current) => !current)}
        >
          <span aria-hidden="true">{isSidebarCollapsed ? '>' : '<'}</span>
        </button>

        <nav className="sidebar-nav">
          {NAV_ITEMS.map((item) => (
            <NavLink
              className={({ isActive }) => `nav-item${isActive ? ' active' : ''}`}
              to={item.to}
              key={item.to}
              title={item.label}
            >
              <span className="nav-icon" aria-hidden="true">
                <MenuIcon name={item.icon} />
              </span>
              <span className="nav-label">{item.label}</span>
            </NavLink>
          ))}
        </nav>

        <div className="sidebar-footer">
          <div>
            <p className="topbar-label">Sesion activa</p>
            <strong>{usuario?.nombres ?? 'Usuario'}</strong>
          </div>
          <button className="secondary-button" type="button" onClick={signOut}>
            Cerrar sesion
          </button>
        </div>
      </aside>

      <div className="main-area">
        <main className="content-area">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

function MenuIcon({ name }: { name: string }) {
  const icons: Record<string, ReactNode> = {
    dashboard: (
      <>
        <path d="M4 13h6V4H4v9Z" />
        <path d="M14 20h6V4h-6v16Z" />
        <path d="M4 20h6v-3H4v3Z" />
      </>
    ),
    clientes: (
      <>
        <path d="M16 19c0-2.2-1.8-4-4-4H8c-2.2 0-4 1.8-4 4" />
        <path d="M10 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8Z" />
        <path d="M20 19c0-1.7-1-3.1-2.5-3.7" />
        <path d="M17 4.4a3 3 0 0 1 0 5.2" />
      </>
    ),
    vehiculos: (
      <>
        <path d="M5 16h14l-1.4-5.1A3 3 0 0 0 14.7 9H9.3a3 3 0 0 0-2.9 1.9L5 16Z" />
        <path d="M4 16v3" />
        <path d="M20 16v3" />
        <path d="M7 19h.1" />
        <path d="M17 19h.1" />
        <path d="M8 13h8" />
      </>
    ),
    configuraciones: (
      <>
        <path d="M12 15.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7Z" />
        <path d="M19 13.5v-3l-2-.5a7 7 0 0 0-.8-1.9l1.1-1.8-2.1-2.1-1.8 1.1a7 7 0 0 0-1.9-.8L11 2H8l-.5 2a7 7 0 0 0-1.9.8L3.8 3.7 1.7 5.8l1.1 1.8A7 7 0 0 0 2 9.5l-2 .5v3l2 .5a7 7 0 0 0 .8 1.9l-1.1 1.8 2.1 2.1 1.8-1.1a7 7 0 0 0 1.9.8L8 22h3l.5-2a7 7 0 0 0 1.9-.8l1.8 1.1 2.1-2.1-1.1-1.8a7 7 0 0 0 .8-1.9l2-.5Z" />
      </>
    ),
    operaciones: (
      <>
        <path d="M6 3h10l3 3v15H6V3Z" />
        <path d="M16 3v4h4" />
        <path d="M9 12h6" />
        <path d="M9 16h8" />
      </>
    ),
    simulador: (
      <>
        <path d="M5 4h14v16H5V4Z" />
        <path d="M8 8h8" />
        <path d="M8 12h2" />
        <path d="M12 12h2" />
        <path d="M16 12h.1" />
        <path d="M8 16h2" />
        <path d="M12 16h2" />
        <path d="M16 16h.1" />
      </>
    ),
    comparador: (
      <>
        <path d="M5 20V8" />
        <path d="M12 20V4" />
        <path d="M19 20v-9" />
        <path d="M3 20h18" />
      </>
    ),
  };

  return (
    <svg viewBox="0 0 24 24" role="img" focusable="false">
      <g fill="none" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.9">
        {icons[name]}
      </g>
    </svg>
  );
}
