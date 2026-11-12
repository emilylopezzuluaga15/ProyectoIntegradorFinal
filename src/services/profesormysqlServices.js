import apiClient from './apiClient';

const BASE = '/api/profesores';

export const getProfesores = async () => {
  const { data } = await apiClient.get(BASE);
  return data;
};

export const getProfesorById = async (id) => {
  const { data } = await apiClient.get(`${BASE}/${id}`);
  return data;
};

export const createProfesor = async (profesor) => {
  const { data } = await apiClient.post(BASE, profesor);
  return data;
};

export const updateProfesor = async (id, profesor) => {
  const { data } = await apiClient.put(`${BASE}/${id}`, profesor);
  return data;
};

export const deleteProfesor = async (id) => {
  await apiClient.delete(`${BASE}/${id}`);
};
