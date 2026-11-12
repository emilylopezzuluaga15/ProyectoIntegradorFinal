import apiClient from './apiClient';

const BASE = '/api/carreras';

export const getCarreras = async () => {
  const { data } = await apiClient.get(BASE);
  return data;
};

export const getCarreraById = async (id) => {
  const { data } = await apiClient.get(`${BASE}/${id}`);
  return data;
};

export const createCarrera = async (carrera) => {
  const { data } = await apiClient.post(BASE, carrera);
  return data;
};

export const updateCarrera = async (id, carrera) => {
  const { data } = await apiClient.put(`${BASE}/${id}`, carrera);
  return data;
};

export const deleteCarrera = async (id) => {
  await apiClient.delete(`${BASE}/${id}`);
};
