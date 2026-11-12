import apiClient from './apiClient';

const BASE = '/api/estudiantes';

export const getEstudiantes = async () => {
  const { data } = await apiClient.get(BASE);
  return data;
};

export const getEstudianteById = async (id) => {
  const { data } = await apiClient.get(`${BASE}/${id}`);
  return data;
};

export const createEstudiante = async (estudiante) => {
  const { data } = await apiClient.post(BASE, estudiante);
  return data;
};

export const updateEstudiante = async (id, estudiante) => {
  const { data } = await apiClient.put(`${BASE}/${id}`, estudiante);
  return data;
};

export const deleteEstudiante = async (id) => {
  await apiClient.delete(`${BASE}/${id}`);
};
