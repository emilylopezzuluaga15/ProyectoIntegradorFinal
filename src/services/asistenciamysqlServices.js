import apiClient from './apiClient';

const BASE = '/api/asistencia';

export const getAsistencias = async () => {
  const { data } = await apiClient.get(BASE);
  return data;
};

export const getAsistenciaById = async (id) => {
  const { data } = await apiClient.get(`${BASE}/${id}`);
  return data;
};

export const createAsistencia = async (asistencia) => {
  const { data } = await apiClient.post(BASE, asistencia);
  return data;
};

export const updateAsistencia = async (id, asistencia) => {
  const { data } = await apiClient.put(`${BASE}/${id}`, asistencia);
  return data;
};

export const deleteAsistencia = async (id) => {
  await apiClient.delete(`${BASE}/${id}`);
};

export const getAsistenciasByEstudiante = async (idEstudiante) => {
  const { data } = await apiClient.get(`${BASE}/estudiante/${idEstudiante}`);
  return data;
};

// { idEstudiante, totalClases, asistidas, inasistencias, inasistenciasJustificadas, porcentajeAsistencia }
export const getResumenAsistencia = async (idEstudiante) => {
  const { data } = await apiClient.get(`${BASE}/estudiante/${idEstudiante}/resumen`);
  return data;
};
