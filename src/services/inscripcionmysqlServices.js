import apiClient from './apiClient';

const BASE = '/api/inscripciones';

export const getInscripciones = async () => {
  const { data } = await apiClient.get(BASE);
  return data;
};

export const getInscripcionById = async (id) => {
  const { data } = await apiClient.get(`${BASE}/${id}`);
  return data;
};

export const createInscripcion = async (inscripcion) => {
  const { data } = await apiClient.post(BASE, inscripcion);
  return data;
};

export const updateInscripcion = async (id, inscripcion) => {
  const { data } = await apiClient.put(`${BASE}/${id}`, inscripcion);
  return data;
};

export const deleteInscripcion = async (id) => {
  await apiClient.delete(`${BASE}/${id}`);
};

export const getInscripcionesByEstudiante = async (idEstudiante) => {
  const { data } = await apiClient.get(`${BASE}/estudiante/${idEstudiante}`);
  return data;
};

export const getInscripcionesByGrupo = async (idGrupo) => {
  const { data } = await apiClient.get(`${BASE}/grupo/${idGrupo}`);
  return data;
};
