import apiClient from './apiClient';

const BASE = '/api/niveles-materias';

export const getNivelMaterias = async () => {
  const { data } = await apiClient.get(BASE);
  return data;
};

export const getNivelMateriaById = async (id) => {
  const { data } = await apiClient.get(`${BASE}/${id}`);
  return data;
};

export const createNivelMateria = async (nivelMateria) => {
  const { data } = await apiClient.post(BASE, nivelMateria);
  return data;
};

export const updateNivelMateria = async (id, nivelMateria) => {
  const { data } = await apiClient.put(`${BASE}/${id}`, nivelMateria);
  return data;
};

export const deleteNivelMateria = async (id) => {
  await apiClient.delete(`${BASE}/${id}`);
};
