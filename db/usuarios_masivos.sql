-- 3000 usuarios de demostración para probar el panel de administración (MySQL 8).
-- Reparto: 2000 ESTUDIANTE, 700 PROFESOR, 250 COORDINADOR, 50 ADMINISTRATIVO.
-- Cada ESTUDIANTE/PROFESOR necesita su propio registro académico, así que también se crean
-- 2000 estudiantes (DEMO-E0001...) y 700 profesores (DEMO-P001...).
-- Contraseña de todos: Demo1234*   ·   1 de cada 20 cuentas (salvo ADMINISTRATIVO) queda inactiva.
-- Para deshacerlo: db/usuarios_masivos_borrar.sql
USE sistema_academico;

SET SESSION cte_max_recursion_depth = 5000;
SET @hash = '$2a$10$BBvTuUAAk0WmAG7LpzGUfOUVSKpXwl4cUg40AaXvfWZRJ.cEiUo6K';

INSERT INTO estudiantes (ID_Estudiante, Nombre_Estudiante, Fecha_Ingreso, Genero, Celular, Correo, Activo, Estado)
WITH RECURSIVE n(i) AS (SELECT 1 UNION ALL SELECT i + 1 FROM n WHERE i < 2000)
SELECT CONCAT('DEMO-E', LPAD(i, 4, '0')),
       CONCAT(ELT(1 + MOD(i, 20), 'Ana', 'Juan', 'María', 'Carlos', 'Laura', 'Andrés', 'Sofía', 'Diego', 'Valentina', 'Felipe',
                  'Camila', 'Santiago', 'Daniela', 'Mateo', 'Paula', 'Sebastián', 'Isabella', 'Tomás', 'Mariana', 'Julián'),
              ' ',
              ELT(1 + MOD(FLOOR(i / 20), 20), 'García', 'Rodríguez', 'Martínez', 'López', 'Gómez', 'Pérez', 'Sánchez', 'Ramírez',
                  'Torres', 'Díaz', 'Vargas', 'Castro', 'Rojas', 'Moreno', 'Herrera', 'Jiménez', 'Ruiz', 'Álvarez', 'Ortiz', 'Mejía')),
       DATE_ADD('2024-01-15', INTERVAL MOD(i, 900) DAY),
       ELT(1 + MOD(i, 2), 'F', 'M'),
       3000000000 + i,
       CONCAT('demo.e', LPAD(i, 4, '0'), '@demo.edu'),
       TRUE,
       'Activo'
FROM n;

INSERT INTO profesores (ID_Profesor, Nombre_Profesor, Fecha_Ingreso, Genero, Celular, Correo, Activo, Especializacion)
WITH RECURSIVE n(i) AS (SELECT 1 UNION ALL SELECT i + 1 FROM n WHERE i < 700)
SELECT CONCAT('DEMO-P', LPAD(i, 3, '0')),
       CONCAT(ELT(1 + MOD(i, 10), 'Luis', 'Patricia', 'Jorge', 'Claudia', 'Ricardo', 'Natalia', 'Hernán', 'Adriana', 'Óscar', 'Liliana'),
              ' ',
              ELT(1 + MOD(FLOOR(i / 10), 14), 'Restrepo', 'Cardona', 'Osorio', 'Giraldo', 'Muñoz', 'Zapata', 'Henao',
                  'Arango', 'Quintero', 'Salazar', 'Valencia', 'Ospina', 'Correa', 'Londoño')),
       DATE_ADD('2015-02-01', INTERVAL MOD(i * 7, 3600) DAY),
       ELT(1 + MOD(i, 2), 'M', 'F'),
       3100000000 + i,
       CONCAT('demo.p', LPAD(i, 3, '0'), '@demo.edu'),
       TRUE,
       ELT(1 + MOD(i, 6), 'Matemáticas', 'Programación', 'Física', 'Bases de datos', 'Redes', 'Humanidades')
FROM n;

INSERT INTO usuarios (Username, Password_Hash, Rol, Activo, ID_Estudiante, ID_Profesor)
WITH RECURSIVE n(i) AS (SELECT 1 UNION ALL SELECT i + 1 FROM n WHERE i < 2000)
SELECT CONCAT('demo_est', LPAD(i, 4, '0')), @hash, 'ESTUDIANTE', MOD(i, 20) <> 0, CONCAT('DEMO-E', LPAD(i, 4, '0')), NULL
FROM n;

INSERT INTO usuarios (Username, Password_Hash, Rol, Activo, ID_Estudiante, ID_Profesor)
WITH RECURSIVE n(i) AS (SELECT 1 UNION ALL SELECT i + 1 FROM n WHERE i < 700)
SELECT CONCAT('demo_prof', LPAD(i, 3, '0')), @hash, 'PROFESOR', MOD(i, 20) <> 0, NULL, CONCAT('DEMO-P', LPAD(i, 3, '0'))
FROM n;

INSERT INTO usuarios (Username, Password_Hash, Rol, Activo, ID_Estudiante, ID_Profesor)
WITH RECURSIVE n(i) AS (SELECT 1 UNION ALL SELECT i + 1 FROM n WHERE i < 250)
SELECT CONCAT('demo_coord', LPAD(i, 3, '0')), @hash, 'COORDINADOR', MOD(i, 20) <> 0, NULL, NULL
FROM n;

INSERT INTO usuarios (Username, Password_Hash, Rol, Activo, ID_Estudiante, ID_Profesor)
WITH RECURSIVE n(i) AS (SELECT 1 UNION ALL SELECT i + 1 FROM n WHERE i < 50)
SELECT CONCAT('demo_admin', LPAD(i, 2, '0')), @hash, 'ADMINISTRATIVO', TRUE, NULL, NULL
FROM n;
