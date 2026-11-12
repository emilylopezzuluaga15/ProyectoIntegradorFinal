import apiClient from './apiClient';

const BASE = '/api/usuarios';

export const getUsuarios = async () => {
  const { data } = await apiClient.get(BASE);
  return data;
};

export const getUsuarioById = async (id) => {
  const { data } = await apiClient.get(`${BASE}/${id}`);
  return data;
};

export const createUsuario = async (usuario) => {
  const { data } = await apiClient.post(BASE, usuario);
  return data;
};

export const updateUsuario = async (id, usuario) => {
  const { data } = await apiClient.put(`${BASE}/${id}`, usuario);
  return data;
};

export const deleteUsuario = async (id) => {
  await apiClient.delete(`${BASE}/${id}`);
};
