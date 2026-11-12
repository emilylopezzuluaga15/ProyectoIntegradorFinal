import { createContext, useContext, useState } from 'react';
import { login as loginRequest } from '../services/authServices';

const AuthContext = createContext(null);

// Lee el usuario guardado para que la sesión no se pierda al recargar la página
const leerUsuarioGuardado = () => {
  try {
    const guardado = localStorage.getItem('usuario');
    return guardado ? JSON.parse(guardado) : null;
  } catch {
    return null;
  }
};

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(leerUsuarioGuardado);

  const login = async (username, password) => {
    const data = await loginRequest(username, password);
    localStorage.setItem('token', data.token);
    localStorage.setItem('usuario', JSON.stringify(data.usuario));
    setUsuario(data.usuario);
    return data.usuario;
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
    setUsuario(null);
  };

  return (
    <AuthContext.Provider value={{ usuario, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

// Uso en cualquier componente: const { usuario, login, logout } = useAuth();
// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => useContext(AuthContext);
