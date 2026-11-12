import apiClient from './apiClient';

const BASE = '/api/notas';

export const getNotas = async () => {
  const { data } = await apiClient.get(BASE);
  return data;
};

export const getNotaById = async (id) => {
  const { data } = await apiClient.get(`${BASE}/${id}`);
  return data;
};

export const createNota = async (nota) => {
  const { data } = await apiClient.post(BASE, nota);
  return data;
};

export const updateNota = async (id, nota) => {
  const { data } = await apiClient.put(`${BASE}/${id}`, nota);
  return data;
};

export const deleteNota = async (id) => {
  await apiClient.delete(`${BASE}/${id}`);
};

export const getNotasByEstudiante = async (idEstudiante) => {
  const { data } = await apiClient.get(`${BASE}/estudiante/${idEstudiante}`);
  return data;
};

// { idEstudiante, materiasCalificadas, promedio, aprobadas, reprobadas }
export const getPromedioEstudiante = async (idEstudiante) => {
  const { data } = await apiClient.get(`${BASE}/estudiante/${idEstudiante}/promedio`);
  return data;
};
