import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export function ProtectedRoute() {
  const { auth } = useAuth();
  return auth?.token ? <Outlet /> : <Navigate to="/login" replace />;
}

export function RoleRoute({ roles }) {
  const { auth } = useAuth();
  return roles.includes(auth?.role) ? <Outlet /> : <Navigate to="/dashboard" replace />;
}
