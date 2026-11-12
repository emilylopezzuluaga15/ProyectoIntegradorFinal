import apiClient from './apiClient';

const BASE = '/api/asignaciones-materia-profesor';

export const getAsignaciones = async () => {
  const { data } = await apiClient.get(BASE);
  return data;
};

export const getAsignacionById = async (id) => {
  const { data } = await apiClient.get(`${BASE}/${id}`);
  return data;
};

export const createAsignacion = async (asignacion) => {
  const { data } = await apiClient.post(BASE, asignacion);
  return data;
};

export const updateAsignacion = async (id, asignacion) => {
  const { data } = await apiClient.put(`${BASE}/${id}`, asignacion);
  return data;
};

export const deleteAsignacion = async (id) => {
  await apiClient.delete(`${BASE}/${id}`);
};

export const getAsignacionesByProfesor = async (idProfesor) => {
  const { data } = await apiClient.get(`${BASE}/profesor/${idProfesor}`);
  return data;
};
