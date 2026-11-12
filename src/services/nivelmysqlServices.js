import apiClient from './apiClient';

const BASE = '/api/niveles';

export const getNiveles = async () => {
  const { data } = await apiClient.get(BASE);
  return data;
};

export const getNivelById = async (id) => {
  const { data } = await apiClient.get(`${BASE}/${id}`);
  return data;
};

export const createNivel = async (nivel) => {
  const { data } = await apiClient.post(BASE, nivel);
  return data;
};

export const updateNivel = async (id, nivel) => {
  const { data } = await apiClient.put(`${BASE}/${id}`, nivel);
  return data;
};

export const deleteNivel = async (id) => {
  await apiClient.delete(`${BASE}/${id}`);
};
