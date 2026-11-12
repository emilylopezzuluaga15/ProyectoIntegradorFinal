import apiClient from './apiClient';

const BASE = '/api/materias';

export const getMaterias = async () => {
  const { data } = await apiClient.get(BASE);
  return data;
};

export const getMateriaById = async (id) => {
  const { data } = await apiClient.get(`${BASE}/${id}`);
  return data;
};

export const createMateria = async (materia) => {
  const { data } = await apiClient.post(BASE, materia);
  return data;
};

export const updateMateria = async (id, materia) => {
  const { data } = await apiClient.put(`${BASE}/${id}`, materia);
  return data;
};

export const deleteMateria = async (id) => {
  await apiClient.delete(`${BASE}/${id}`);
};
