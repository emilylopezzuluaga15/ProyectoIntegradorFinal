import apiClient from './apiClient';

const BASE = '/api/auth';

// Devuelve { token, tipo, expiraEn, usuario: { idUsuario, username, rol, activo, idEstudiante, idProfesor } }
export const login = async (username, password) => {
  const { data } = await apiClient.post(`${BASE}/login`, { username, password });
  return data;
};

export const getUsuarioActual = async () => {
  const { data } = await apiClient.get(`${BASE}/me`);
  return data;
};
