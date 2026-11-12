-- Datos académicos de demostración para los estudiantes/profesores DEMO-* de db/usuarios_masivos.sql
-- (ejecuta ese script primero). Todo usa el prefijo DEMO- y no toca los registros reales.
--   5 carreras · 20 niveles (4 semestres por carrera) · 100 materias (5 por nivel) · 40 grupos (A diurno, B nocturno)
--   200 asignaciones (DEMO-P001..P200 dictan una materia en un grupo) · 2000 inscripciones (50 estudiantes por grupo)
--   10 000 notas (una por materia del nivel) · 30 000 calificaciones (3 cortes por nota) · 20 000 asistencias (10 clases)
-- La definitiva es el promedio de los 3 cortes redondeado a 2 decimales, como lo hace la aplicación.
-- Para deshacerlo: db/datos_academicos_demo_borrar.sql
USE sistema_academico;

SET SESSION cte_max_recursion_depth = 5000;

DROP TEMPORARY TABLE IF EXISTS tmp_carreras, tmp_grupos, tmp_materias;

CREATE TEMPORARY TABLE tmp_carreras (c INT PRIMARY KEY, cod VARCHAR(5), nombre VARCHAR(50), corto VARCHAR(20));
INSERT INTO tmp_carreras VALUES
    (1, 'SIS', 'Ingeniería de Sistemas', 'Sistemas'),
    (2, 'IND', 'Ingeniería Industrial', 'Industrial'),
    (3, 'ADM', 'Administración de Empresas', 'Administración'),
    (4, 'CON', 'Contaduría Pública', 'Contaduría'),
    (5, 'PSI', 'Psicología', 'Psicología');

-- ng = 1..40 → carrera c, semestre s, jornada jor (1 = A diurno, 2 = B nocturno)
CREATE TEMPORARY TABLE tmp_grupos AS
WITH RECURSIVE n(ng) AS (SELECT 1 UNION ALL SELECT ng + 1 FROM n WHERE ng < 40),
     d AS (SELECT ng, FLOOR((ng - 1) / 8) + 1 AS c, MOD(FLOOR((ng - 1) / 2), 4) + 1 AS s, MOD(ng - 1, 2) + 1 AS jor FROM n)
SELECT d.ng, d.c, d.s, d.jor,
       CONCAT('DEMO-', k.cod) AS id_carrera,
       CONCAT('DEMO-', k.cod, '-', d.s) AS id_nivel,
       CONCAT(ELT(d.s, 'Primer', 'Segundo', 'Tercer', 'Cuarto'), ' semestre - ', k.corto) AS nombre_nivel,
       CONCAT('DEMO-', k.cod, '-', d.s, ELT(d.jor, 'A', 'B')) AS id_grupo,
       CONCAT(k.corto, ' ', d.s, ELT(d.jor, 'A', 'B'), ' ', ELT(d.jor, 'Diurno', 'Nocturno')) AS nombre_grupo
FROM d JOIN tmp_carreras k ON k.c = d.c;

-- 5 materias por nivel: familia de la carrera + semestre en romano (Programación I, Programación II...)
CREATE TEMPORARY TABLE tmp_materias AS
SELECT t.id_nivel,
       CONCAT(t.id_nivel, m.k) AS id_materia,
       CONCAT(ELT((t.c - 1) * 5 + m.k,
                  'Programación', 'Cálculo', 'Bases de Datos', 'Redes', 'Ingeniería de Software',
                  'Procesos Industriales', 'Estadística', 'Investigación de Operaciones', 'Logística', 'Gestión de Calidad',
                  'Administración', 'Contabilidad', 'Mercadeo', 'Economía', 'Talento Humano',
                  'Contabilidad Financiera', 'Costos', 'Tributaria', 'Auditoría', 'Finanzas',
                  'Psicología General', 'Neurociencia', 'Psicología Social', 'Psicometría', 'Psicología Clínica'),
              ' ', ELT(t.s, 'I', 'II', 'III', 'IV')) AS nombre_materia
FROM tmp_grupos t
CROSS JOIN (SELECT 1 AS k UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) m
WHERE t.jor = 1;

INSERT INTO carreras (ID_Carrera, Nombre_Carrera)
SELECT CONCAT('DEMO-', cod), nombre FROM tmp_carreras;

INSERT INTO niveles (ID_Nivel, Nombre_Nivel, ID_Carrera)
SELECT id_nivel, nombre_nivel, id_carrera FROM tmp_grupos WHERE jor = 1;

INSERT INTO materias (ID_Materia, Nombre_Materia)
SELECT id_materia, nombre_materia FROM tmp_materias;

INSERT INTO niveles_materias (ID_Nivel, ID_Materia)
SELECT id_nivel, id_materia FROM tmp_materias;

INSERT INTO grupos (ID_Grupo, Nombre_Grupo, ID_Nivel, Cupo_Maximo)
SELECT id_grupo, nombre_grupo, id_nivel, 60 FROM tmp_grupos;

INSERT INTO asignaciones_materia_profesor (ID_Profesor, ID_Grupo, ID_Materia)
SELECT CONCAT('DEMO-P', LPAD(ROW_NUMBER() OVER (ORDER BY t.id_grupo, m.id_materia), 3, '0')), t.id_grupo, m.id_materia
FROM tmp_grupos t JOIN tmp_materias m ON m.id_nivel = t.id_nivel;

-- Estudiante i va al grupo ng = ((i - 1) mod 40) + 1; la cohorte es su año de ingreso
INSERT INTO inscripciones (anio_inscripcion, cohorte, ID_Estudiante, ID_Grupo)
SELECT 2026, YEAR(e.Fecha_Ingreso), e.ID_Estudiante, t.id_grupo
FROM estudiantes e
JOIN tmp_grupos t ON t.ng = MOD(CAST(SUBSTRING(e.ID_Estudiante, 7) AS UNSIGNED) - 1, 40) + 1
WHERE e.ID_Estudiante LIKE 'DEMO-E%';

INSERT INTO notas (Definitiva, Fecha_Registro, Aprobada, ID_Estudiante, ID_Materia)
SELECT NULL, '2026-02-02', NULL, i.ID_Estudiante, m.id_materia
FROM inscripciones i
JOIN grupos gr ON gr.ID_Grupo = i.ID_Grupo
JOIN tmp_materias m ON m.id_nivel = gr.ID_Nivel
WHERE i.ID_Estudiante LIKE 'DEMO-E%';

-- 3 cortes por nota con calificaciones entre 2.0 y 5.0
INSERT INTO movimiento_notas (Fecha_Registro, Calificacion, ID_Nota)
SELECT ELT(c.j, '2026-03-13', '2026-05-08', '2026-06-19'),
       ROUND(2.0 + MOD(n.ID_Nota * 37 + c.j * 53 + FLOOR(n.ID_Nota / 7), 31) / 10, 1),
       n.ID_Nota
FROM notas n
CROSS JOIN (SELECT 1 AS j UNION ALL SELECT 2 UNION ALL SELECT 3) c
WHERE n.ID_Estudiante LIKE 'DEMO-E%';

UPDATE notas n
JOIN (SELECT ID_Nota, ROUND(AVG(Calificacion), 2) AS d FROM movimiento_notas GROUP BY ID_Nota) m ON m.ID_Nota = n.ID_Nota
SET n.Definitiva = m.d, n.Aprobada = m.d >= 3.0
WHERE n.ID_Estudiante LIKE 'DEMO-E%';

-- 10 clases (lunes de febrero a abril); ~90 % de asistencia, un tercio de las faltas justificadas
INSERT INTO asistencia (Fecha_Asistencia, Asistio, Justificada, Observacion, ID_Estudiante, ID_Grupo)
SELECT x.fecha, x.asistio, x.justificada,
       CASE
           WHEN x.asistio THEN ELT(1 + MOD(x.h, 3), 'Asistió puntual', 'Llegó tarde', 'Participó en clase')
           WHEN x.justificada THEN ELT(1 + MOD(x.h, 2), 'Incapacidad médica', 'Calamidad doméstica')
           ELSE 'No presentó excusa'
       END,
       x.ID_Estudiante, x.ID_Grupo
FROM (SELECT i.ID_Estudiante, i.ID_Grupo,
             ELT(c.k, '2026-02-09', '2026-02-16', '2026-02-23', '2026-03-02', '2026-03-09',
                      '2026-03-16', '2026-03-23', '2026-04-06', '2026-04-13', '2026-04-20') AS fecha,
             i.ID_Inscripcion * 31 + c.k * 17 AS h,
             MOD(i.ID_Inscripcion * 31 + c.k * 17, 10) <> 0 AS asistio,
             MOD(i.ID_Inscripcion * 31 + c.k * 17, 10) = 0 AND MOD(i.ID_Inscripcion + c.k, 3) = 0 AS justificada
      FROM inscripciones i
      CROSS JOIN (SELECT 1 AS k UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
                  UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10) c
      WHERE i.ID_Estudiante LIKE 'DEMO-E%') x;

DROP TEMPORARY TABLE tmp_carreras, tmp_grupos, tmp_materias;
