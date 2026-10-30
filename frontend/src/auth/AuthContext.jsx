import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import api from '../api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(() => JSON.parse(localStorage.getItem('security-auth') || 'null'));

  useEffect(() => {
    const expire = () => {
      localStorage.removeItem('security-auth');
      setAuth(null);
      window.location.assign('/login?expired=1');
    };
    window.addEventListener('auth-expired', expire);
    return () => window.removeEventListener('auth-expired', expire);
  }, []);

  const value = useMemo(() => ({
    auth,
    async login(credentials) {
      const { data } = await api.post('/auth/login', credentials);
      localStorage.setItem('security-auth', JSON.stringify(data));
      setAuth(data);
      return data;
    },
    async register(details) {
      const { data } = await api.post('/auth/register', details);
      localStorage.setItem('security-auth', JSON.stringify(data));
      setAuth(data);
      return data;
    },
    logout() {
      localStorage.removeItem('security-auth');
      setAuth(null);
      window.location.assign('/login');
    },
  }), [auth]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
