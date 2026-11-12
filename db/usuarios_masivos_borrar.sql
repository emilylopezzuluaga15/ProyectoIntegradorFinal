-- Deshace db/usuarios_masivos.sql: borra los usuarios demo_* y los estudiantes/profesores DEMO-*.
-- Si también cargaste db/datos_academicos_demo.sql, ejecuta antes db/datos_academicos_demo_borrar.sql
-- Si después de cargarlos les registraste notas, inscripciones o asignaciones, bórralas primero
USE sistema_academico;

DELETE FROM usuarios WHERE Username LIKE 'demo\_%';
DELETE FROM estudiantes WHERE ID_Estudiante LIKE 'DEMO-E%';
DELETE FROM profesores WHERE ID_Profesor LIKE 'DEMO-P%';
