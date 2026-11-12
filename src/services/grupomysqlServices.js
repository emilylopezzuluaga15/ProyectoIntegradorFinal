import apiClient from './apiClient';

const BASE = '/api/grupos';

export const getGrupos = async () => {
  const { data } = await apiClient.get(BASE);
  return data;
};

export const getGrupoById = async (id) => {
  const { data } = await apiClient.get(`${BASE}/${id}`);
  return data;
};

export const createGrupo = async (grupo) => {
  const { data } = await apiClient.post(BASE, grupo);
  return data;
};

export const updateGrupo = async (id, grupo) => {
  const { data } = await apiClient.put(`${BASE}/${id}`, grupo);
  return data;
};

export const deleteGrupo = async (id) => {
  await apiClient.delete(`${BASE}/${id}`);
};

// { idGrupo, anio, cupoMaximo, inscritos, disponibles }
export const getCupoGrupo = async (id, anio) => {
  const { data } = await apiClient.get(`${BASE}/${id}/cupo`, { params: anio ? { anio } : {} });
  return data;
};
