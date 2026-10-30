import { NavLink, Outlet } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

const roleNames = { EMPLOYEE: 'Employé', TECHNICIAN: 'Technicien', ADMIN: 'Administration' };

export default function AppLayout() {
  const { auth, logout } = useAuth();
  const employeeLinks = [
    ['/dashboard', 'Vue générale'], ['/incidents/my', 'Mes incidents'], ['/incidents/new', 'Signaler un incident'], ['/profile', 'Mon profil'],
  ];
  const technicianLinks = [
    ['/dashboard', 'Vue générale'], ['/incidents/assigned', 'File de traitement'], ['/profile', 'Mon profil'],
  ];
  const adminLinks = [
    ['/dashboard', 'Vue générale'], ['/admin/incidents', 'Tous les incidents'], ['/admin/users', 'Utilisateurs'], ['/profile', 'Mon profil'],
  ];
  const links = auth?.role === 'ADMIN' ? adminLinks : auth?.role === 'TECHNICIAN' ? technicianLinks : employeeLinks;

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <NavLink className="brand" to="/dashboard"><span className="brand-mark">S</span><span>Sentinelle<small>Centre de sécurité</small></span></NavLink>
        <div className="side-caption">ESPACE DE TRAVAIL</div>
        <nav className="side-nav">
          {links.map(([to, label]) => <NavLink key={to} to={to} end={to === '/dashboard'} className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}><span className="nav-dot" />{label}</NavLink>)}
        </nav>
        <div className="sidebar-bottom">
          <div className="online-indicator"><span /> API locale · disponible</div>
          <button className="user-chip" onClick={logout} title="Se déconnecter">
            <span className="avatar">{auth?.fullName?.slice(0, 1)?.toUpperCase() || 'U'}</span>
            <span className="user-chip-copy"><strong>{auth?.fullName}</strong><small>{roleNames[auth?.role] || auth?.role}</small></span>
            <span className="logout-glyph">↗</span>
          </button>
        </div>
      </aside>
      <main className="main-area">
        <header className="topbar"><span className="breadcrumb">Sécurité <span>/</span> {roleNames[auth?.role]}</span><span className="topbar-date">Portail de gestion des incidents</span></header>
        <div className="page-wrap"><Outlet /></div>
      </main>
    </div>
  );
}
