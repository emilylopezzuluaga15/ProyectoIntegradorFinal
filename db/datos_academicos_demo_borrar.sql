-- Deshace db/datos_academicos_demo.sql (ejecútalo antes de db/usuarios_masivos_borrar.sql)
USE sistema_academico;

DELETE FROM asistencia WHERE ID_Estudiante LIKE 'DEMO-E%' OR ID_Grupo LIKE 'DEMO-%';
DELETE mv FROM movimiento_notas mv JOIN notas n ON n.ID_Nota = mv.ID_Nota
WHERE n.ID_Estudiante LIKE 'DEMO-E%' OR n.ID_Materia LIKE 'DEMO-%';
DELETE FROM notas WHERE ID_Estudiante LIKE 'DEMO-E%' OR ID_Materia LIKE 'DEMO-%';
DELETE FROM inscripciones WHERE ID_Estudiante LIKE 'DEMO-E%' OR ID_Grupo LIKE 'DEMO-%';
DELETE FROM asignaciones_materia_profesor WHERE ID_Profesor LIKE 'DEMO-P%' OR ID_Grupo LIKE 'DEMO-%';
DELETE FROM grupos WHERE ID_Grupo LIKE 'DEMO-%';
DELETE FROM niveles_materias WHERE ID_Nivel LIKE 'DEMO-%' OR ID_Materia LIKE 'DEMO-%';
DELETE FROM materias WHERE ID_Materia LIKE 'DEMO-%';
DELETE FROM niveles WHERE ID_Nivel LIKE 'DEMO-%';
DELETE FROM carreras WHERE ID_Carrera LIKE 'DEMO-%';
