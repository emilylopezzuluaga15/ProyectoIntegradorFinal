import apiClient from './apiClient';

const BASE = '/api/movimiento-notas';

export const getMovimientosNota = async () => {
  const { data } = await apiClient.get(BASE);
  return data;
};

export const getMovimientoNotaById = async (id) => {
  const { data } = await apiClient.get(`${BASE}/${id}`);
  return data;
};

export const createMovimientoNota = async (movimiento) => {
  const { data } = await apiClient.post(BASE, movimiento);
  return data;
};

export const updateMovimientoNota = async (id, movimiento) => {
  const { data } = await apiClient.put(`${BASE}/${id}`, movimiento);
  return data;
};

export const deleteMovimientoNota = async (id) => {
  await apiClient.delete(`${BASE}/${id}`);
};

export const getMovimientosByNota = async (idNota) => {
  const { data } = await apiClient.get(`${BASE}/nota/${idNota}`);
  return data;
};
