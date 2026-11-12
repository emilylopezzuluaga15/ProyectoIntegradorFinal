-- Datos de demostración. Ejecutar DESPUÉS de schema.sql y sobre una BD VACÍA
-- (usa ids fijos; si ya tienes datos propios, los INSERT IGNORE se saltan lo que choque).
--
-- Usuarios demo, todos con la contraseña: Demo1234*
--   coordinador  -> COORDINADOR
--   profesor1    -> PROFESOR (PROF001, dicta Cálculo I en GRP001)
--   profesor2    -> PROFESOR (PROF002, dicta Programación I en GRP001)
--   est001       -> ESTUDIANTE (EST001)
--   est002       -> ESTUDIANTE (EST002)
-- El usuario "admin" (ADMINISTRATIVO) lo crea la aplicación al arrancar con app.admin.password.

USE sistema_academico;

INSERT IGNORE INTO carreras (ID_Carrera, Nombre_Carrera) VALUES
    ('CAR001', 'Ingeniería de Sistemas');

INSERT IGNORE INTO niveles (ID_Nivel, Nombre_Nivel, ID_Carrera) VALUES
    ('NIV001', 'Primer semestre', 'CAR001'),
    ('NIV002', 'Segundo semestre', 'CAR001');

INSERT IGNORE INTO materias (ID_Materia, Nombre_Materia) VALUES
    ('MAT001', 'Cálculo I'),
    ('MAT002', 'Programación I'),
    ('MAT003', 'Álgebra Lineal');

INSERT IGNORE INTO niveles_materias (ID_Nivel_Materia, ID_Nivel, ID_Materia) VALUES
    (1, 'NIV001', 'MAT001'),
    (2, 'NIV001', 'MAT002'),
    (3, 'NIV002', 'MAT003');

INSERT IGNORE INTO profesores (ID_Profesor, Nombre_Profesor, Fecha_Ingreso, Genero, Celular, Correo, Activo, Especializacion) VALUES
    ('PROF001', 'Carlos Gómez', '2020-02-01', 'M', 3101112233, 'carlos.gomez@demo.edu', TRUE, 'Matemáticas'),
    ('PROF002', 'Laura Martínez', '2021-07-15', 'F', 3104445566, 'laura.martinez@demo.edu', TRUE, 'Ingeniería de Software');

INSERT IGNORE INTO grupos (ID_Grupo, Nombre_Grupo, ID_Nivel, Cupo_Maximo) VALUES
    ('GRP001', 'Sistemas 1A', 'NIV001', 30),
    ('GRP002', 'Sistemas 2A', 'NIV002', NULL);

INSERT IGNORE INTO asignaciones_materia_profesor (ID_Asignacion, ID_Profesor, ID_Grupo, ID_Materia) VALUES
    (1, 'PROF001', 'GRP001', 'MAT001'),
    (2, 'PROF002', 'GRP001', 'MAT002');

INSERT IGNORE INTO estudiantes (ID_Estudiante, Nombre_Estudiante, Fecha_Ingreso, Genero, Celular, Correo, Activo, Estado) VALUES
    ('EST001', 'Juan Pérez', '2026-01-20', 'M', 3001234567, 'juan.perez@demo.edu', TRUE, 'Activo'),
    ('EST002', 'María López', '2026-01-20', 'F', 3007654321, 'maria.lopez@demo.edu', TRUE, 'Activo'),
    ('EST003', 'Andrés Torres', '2026-01-20', 'M', NULL, 'andres.torres@demo.edu', TRUE, 'Activo');

INSERT IGNORE INTO inscripciones (ID_Inscripcion, anio_inscripcion, cohorte, ID_Estudiante, ID_Grupo) VALUES
    (1, 2026, 20261, 'EST001', 'GRP001'),
    (2, 2026, 20261, 'EST002', 'GRP001'),
    (3, 2026, 20261, 'EST003', 'GRP001');

INSERT IGNORE INTO asistencia (ID_Asistencia, Fecha_Asistencia, Asistio, Justificada, Observacion, ID_Estudiante, ID_Grupo) VALUES
    (1, '2026-02-02', TRUE, NULL, NULL, 'EST001', 'GRP001'),
    (2, '2026-02-03', TRUE, NULL, NULL, 'EST001', 'GRP001'),
    (3, '2026-02-02', FALSE, TRUE, 'Excusa médica', 'EST002', 'GRP001'),
    (4, '2026-02-03', FALSE, FALSE, NULL, 'EST002', 'GRP001'),
    (5, '2026-02-02', TRUE, NULL, NULL, 'EST003', 'GRP001');

-- Definitiva = promedio de sus movimientos; Aprobada = definitiva >= 3.0
INSERT IGNORE INTO notas (ID_Nota, Definitiva, Fecha_Registro, Aprobada, ID_Estudiante, ID_Materia) VALUES
    (1, 3.75, '2026-03-01', TRUE, 'EST001', 'MAT001'),
    (2, 2.5, '2026-03-01', FALSE, 'EST002', 'MAT001'),
    (3, NULL, '2026-03-01', NULL, 'EST001', 'MAT002');

INSERT IGNORE INTO movimiento_notas (ID_Movimiento, Fecha_Registro, Calificacion, ID_Nota) VALUES
    (1, '2026-03-10', 4.0, 1),
    (2, '2026-04-10', 3.5, 1),
    (3, '2026-03-10', 2.0, 2),
    (4, '2026-04-10', 3.0, 2);

INSERT IGNORE INTO usuarios (Username, Password_Hash, Rol, Activo, ID_Estudiante, ID_Profesor) VALUES
    ('coordinador', '$2a$10$BBvTuUAAk0WmAG7LpzGUfOUVSKpXwl4cUg40AaXvfWZRJ.cEiUo6K', 'COORDINADOR', TRUE, NULL, NULL),
    ('profesor1', '$2a$10$BBvTuUAAk0WmAG7LpzGUfOUVSKpXwl4cUg40AaXvfWZRJ.cEiUo6K', 'PROFESOR', TRUE, NULL, 'PROF001'),
    ('profesor2', '$2a$10$BBvTuUAAk0WmAG7LpzGUfOUVSKpXwl4cUg40AaXvfWZRJ.cEiUo6K', 'PROFESOR', TRUE, NULL, 'PROF002'),
    ('est001', '$2a$10$BBvTuUAAk0WmAG7LpzGUfOUVSKpXwl4cUg40AaXvfWZRJ.cEiUo6K', 'ESTUDIANTE', TRUE, 'EST001', NULL),
    ('est002', '$2a$10$BBvTuUAAk0WmAG7LpzGUfOUVSKpXwl4cUg40AaXvfWZRJ.cEiUo6K', 'ESTUDIANTE', TRUE, 'EST002', NULL);
