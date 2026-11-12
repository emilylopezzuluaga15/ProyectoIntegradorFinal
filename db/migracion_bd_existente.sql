-- Ajusta una BD sistema_academico creada antes de db/schema.sql para que coincida con él.
-- Ejecútalo UNA sola vez, después de schema.sql (si un índice ya existe, MySQL avisa con "Duplicate key name").
-- Antes de agregar los índices únicos, revisa que no haya filas duplicadas.

USE sistema_academico;

-- La entidad Inscripcion mapea el año como Integer, no como YEAR
ALTER TABLE inscripciones MODIFY anio_inscripcion INT NOT NULL;

ALTER TABLE niveles_materias ADD UNIQUE KEY uk_nivel_materia (ID_Nivel, ID_Materia);
ALTER TABLE profesores ADD UNIQUE KEY uk_profesores_correo (Correo);
ALTER TABLE estudiantes ADD UNIQUE KEY uk_estudiantes_correo (Correo);
ALTER TABLE asignaciones_materia_profesor ADD UNIQUE KEY uk_asignacion_grupo_materia (ID_Grupo, ID_Materia);
ALTER TABLE inscripciones ADD UNIQUE KEY uk_inscripcion (ID_Estudiante, ID_Grupo, anio_inscripcion);
ALTER TABLE asistencia ADD UNIQUE KEY uk_asistencia_dia (ID_Estudiante, ID_Grupo, Fecha_Asistencia);
