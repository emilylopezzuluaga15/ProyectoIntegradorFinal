import apiClient from './apiClient';

const BASE = '/api/reportes';

export const getBoletinEstudiante = async (idEstudiante) => {
  const { data } = await apiClient.get(`${BASE}/boletin/${idEstudiante}`);
  return data;
};

export const getEstudiantesEnRiesgo = async (idGrupo, anio) => {
  const { data } = await apiClient.get(`${BASE}/grupo/${idGrupo}/en-riesgo`, { params: anio ? { anio } : {} });
  return data;
};

export const getPlanillaNotas = async (idGrupo, idMateria, anio) => {
  const { data } = await apiClient.get(`${BASE}/grupo/${idGrupo}/planilla-notas`, {
    params: anio ? { idMateria, anio } : { idMateria },
  });
  return data;
};
