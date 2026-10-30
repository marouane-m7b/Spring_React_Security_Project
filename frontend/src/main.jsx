import React from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './auth/AuthContext';
import AppLayout from './components/AppLayout';
import { ProtectedRoute, RoleRoute } from './components/RouteGuards';
import { AdminUsersPage, DashboardPage, IncidentDetailPage, IncidentListPage, LoginPage, NewIncidentPage, ProfilePage, RegisterPage } from './pages/Pages';
import './style.css';

function App() {
  return <BrowserRouter><AuthProvider><Routes>
    <Route path="/login" element={<LoginPage />} />
    <Route path="/register" element={<RegisterPage />} />
    <Route element={<ProtectedRoute />}><Route element={<AppLayout />}>
      <Route index element={<Navigate to="/dashboard" replace />} />
      <Route path="dashboard" element={<DashboardPage />} />
      <Route path="profile" element={<ProfilePage />} />
      <Route path="incidents/:id" element={<IncidentDetailPage />} />
      <Route element={<RoleRoute roles={['EMPLOYEE']} />}>
        <Route path="incidents/my" element={<IncidentListPage mode="my" />} />
        <Route path="incidents/new" element={<NewIncidentPage />} />
      </Route>
      <Route element={<RoleRoute roles={['TECHNICIAN']} />}>
        <Route path="incidents/assigned" element={<IncidentListPage mode="assigned" />} />
      </Route>
      <Route element={<RoleRoute roles={['ADMIN']} />}>
        <Route path="admin/incidents" element={<IncidentListPage mode="all" />} />
        <Route path="admin/users" element={<AdminUsersPage />} />
      </Route>
    </Route></Route>
    <Route path="*" element={<Navigate to="/dashboard" replace />} />
  </Routes></AuthProvider></BrowserRouter>;
}

createRoot(document.getElementById('root')).render(<React.StrictMode><App /></React.StrictMode>);
