-- Mismos datos que db/data.sql, en SQL compatible con H2. Contraseña de todos: Demo1234*
INSERT INTO carreras (ID_Carrera, Nombre_Carrera) VALUES ('ING-SIS', 'Ingeniería de Sistemas');
INSERT INTO niveles (ID_Nivel, Nombre_Nivel, ID_Carrera) VALUES ('SIS-1', 'Primer semestre', 'ING-SIS');
INSERT INTO materias (ID_Materia, Nombre_Materia) VALUES ('MAT001', 'Cálculo I'), ('MAT002', 'Programación I');
INSERT INTO niveles_materias (ID_Nivel, ID_Materia) VALUES ('SIS-1', 'MAT001'), ('SIS-1', 'MAT002');
INSERT INTO profesores (ID_Profesor, Nombre_Profesor, Fecha_Ingreso, Activo) VALUES
    ('PROF001', 'Carlos Gómez', '2020-02-01', TRUE),
    ('PROF002', 'Laura Martínez', '2021-07-15', TRUE);
INSERT INTO grupos (ID_Grupo, Nombre_Grupo, ID_Nivel, Cupo_Maximo) VALUES ('GRP001', 'Sistemas 1A', 'SIS-1', 30);
INSERT INTO asignaciones_materia_profesor (ID_Profesor, ID_Grupo, ID_Materia) VALUES
    ('PROF001', 'GRP001', 'MAT001'),
    ('PROF002', 'GRP001', 'MAT002');
INSERT INTO estudiantes (ID_Estudiante, Nombre_Estudiante, Fecha_Ingreso, Activo, Estado) VALUES
    ('EST001', 'Juan Pérez', '2026-01-20', TRUE, 'Activo'),
    ('EST002', 'María López', '2026-01-20', TRUE, 'Activo');
INSERT INTO inscripciones (anio_inscripcion, ID_Estudiante, ID_Grupo) VALUES (2026, 'EST001', 'GRP001'), (2026, 'EST002', 'GRP001');
INSERT INTO notas (ID_Nota, Definitiva, Fecha_Registro, Aprobada, ID_Estudiante, ID_Materia) VALUES
    (1, NULL, '2026-03-01', NULL, 'EST001', 'MAT001'),
    (2, NULL, '2026-03-01', NULL, 'EST001', 'MAT002'),
    (3, NULL, '2026-03-01', NULL, 'EST002', 'MAT001');
ALTER TABLE notas ALTER COLUMN ID_Nota RESTART WITH 100;
INSERT INTO usuarios (Username, Password_Hash, Rol, Activo, ID_Estudiante, ID_Profesor) VALUES
    ('coordinador', '$2a$10$BBvTuUAAk0WmAG7LpzGUfOUVSKpXwl4cUg40AaXvfWZRJ.cEiUo6K', 'COORDINADOR', TRUE, NULL, NULL),
    ('profesor1', '$2a$10$BBvTuUAAk0WmAG7LpzGUfOUVSKpXwl4cUg40AaXvfWZRJ.cEiUo6K', 'PROFESOR', TRUE, NULL, 'PROF001'),
    ('est001', '$2a$10$BBvTuUAAk0WmAG7LpzGUfOUVSKpXwl4cUg40AaXvfWZRJ.cEiUo6K', 'ESTUDIANTE', TRUE, 'EST001', NULL);
-- Segundo grupo que profesor1 (PROF001) NO dicta: sirve para comprobar que sus listados no lo incluyen
INSERT INTO grupos (ID_Grupo, Nombre_Grupo, ID_Nivel, Cupo_Maximo) VALUES ('GRP002', 'Sistemas 1B', 'SIS-1', 30);
INSERT INTO asignaciones_materia_profesor (ID_Profesor, ID_Grupo, ID_Materia) VALUES ('PROF002', 'GRP002', 'MAT002');
INSERT INTO estudiantes (ID_Estudiante, Nombre_Estudiante, Fecha_Ingreso, Activo, Estado) VALUES
    ('EST003', 'Ana Ruiz', '2026-01-20', TRUE, 'Activo');
INSERT INTO inscripciones (anio_inscripcion, ID_Estudiante, ID_Grupo) VALUES (2026, 'EST003', 'GRP002');
INSERT INTO asistencia (Fecha_Asistencia, Asistio, Justificada, ID_Estudiante, ID_Grupo) VALUES
    ('2026-03-02', TRUE, FALSE, 'EST001', 'GRP001'),
    ('2026-03-02', FALSE, FALSE, 'EST003', 'GRP002');
