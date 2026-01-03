import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { authService } from '../services/auth';
import type { UserProfile } from '../types/user';

interface AuthContextValue {
  user: UserProfile | null;
  loading: boolean;
  login: (username: string, password: string) => Promise<void>;
  register: (username: string, email: string, password: string) => Promise<void>;
  logout: () => void;
  refreshProfile: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);

  const loadProfile = async () => {
    try {
      const profile = await authService.profile();
      setUser(profile);
    } catch {
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (localStorage.getItem('token')) {
      loadProfile().catch(() => undefined);
    } else {
      setLoading(false);
    }
  }, []);

  const login = async (username: string, password: string) => {
    setLoading(true);
    const { user, token } = await authService.login({ username, password });

    localStorage.setItem('token', token);
    setUser(user); // 🔥 必须加这个
    setLoading(false);
  };

  const register = async (username: string, email: string, password: string) => {
    await authService.register({ username, email, password });
    await login(username, password);
  };

  const logout = () => {
    localStorage.removeItem('token');
    setUser(null);
  };

  const refreshProfile = async () => {
    await loadProfile();
  };

  const value = useMemo(
    () => ({ user, loading, login, register, logout, refreshProfile }),
    [user, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuthContext() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuthContext 必须在 AuthProvider 中使用');
  }
  return ctx;
}

