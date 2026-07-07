import { Navigate, Route, Routes } from 'react-router-dom';
import { MainLayout } from './layouts/MainLayout';
import { ClientesPage } from './pages/ClientesPage';
import { ComparadorPage } from './pages/ComparadorPage';
import { ConfiguracionesPage } from './pages/ConfiguracionesPage';
import { DashboardPage } from './pages/DashboardPage';
import { LoginPage } from './pages/LoginPage';
import { NotFoundPage } from './pages/NotFoundPage';
import { OperacionesPage } from './pages/OperacionesPage';
import { SimuladorPage } from './pages/SimuladorPage';
import { VehiculosPage } from './pages/VehiculosPage';
import { ProtectedRoute } from './routes/ProtectedRoute';

function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<ProtectedRoute />}>
        <Route element={<MainLayout />}>
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/clientes" element={<ClientesPage />} />
          <Route path="/vehiculos" element={<VehiculosPage />} />
          <Route path="/configuraciones" element={<ConfiguracionesPage />} />
          <Route path="/operaciones" element={<OperacionesPage />} />
          <Route path="/simulador" element={<SimuladorPage />} />
          <Route path="/comparador" element={<ComparadorPage />} />
        </Route>
      </Route>
      <Route path="/404" element={<NotFoundPage />} />
      <Route path="*" element={<Navigate to="/404" replace />} />
    </Routes>
  );
}

export default App;
