-- Esquema de la BD sistema_academico (MySQL 8).
-- Reconstruido a partir de las entidades JPA. Usa CREATE TABLE IF NOT EXISTS, así que
-- sobre una BD que ya existe solo crea lo que falte (por ejemplo, la tabla usuarios).

CREATE DATABASE IF NOT EXISTS sistema_academico CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sistema_academico;

CREATE TABLE IF NOT EXISTS carreras (
    ID_Carrera     VARCHAR(20) NOT NULL,
    Nombre_Carrera VARCHAR(50) NOT NULL,
    PRIMARY KEY (ID_Carrera)
);

CREATE TABLE IF NOT EXISTS niveles (
    ID_Nivel     VARCHAR(20) NOT NULL,
    Nombre_Nivel VARCHAR(50) NOT NULL,
    ID_Carrera   VARCHAR(20) NOT NULL,
    PRIMARY KEY (ID_Nivel),
    CONSTRAINT fk_niveles_carrera FOREIGN KEY (ID_Carrera) REFERENCES carreras (ID_Carrera)
);

CREATE TABLE IF NOT EXISTS materias (
    ID_Materia     VARCHAR(20) NOT NULL,
    Nombre_Materia VARCHAR(50) NOT NULL,
    PRIMARY KEY (ID_Materia)
);

-- Plan de estudios: qué materias se ven en cada nivel
CREATE TABLE IF NOT EXISTS niveles_materias (
    ID_Nivel_Materia BIGINT      NOT NULL AUTO_INCREMENT,
    ID_Nivel         VARCHAR(20) NOT NULL,
    ID_Materia       VARCHAR(20) NOT NULL,
    PRIMARY KEY (ID_Nivel_Materia),
    UNIQUE KEY uk_nivel_materia (ID_Nivel, ID_Materia),
    CONSTRAINT fk_nm_nivel FOREIGN KEY (ID_Nivel) REFERENCES niveles (ID_Nivel),
    CONSTRAINT fk_nm_materia FOREIGN KEY (ID_Materia) REFERENCES materias (ID_Materia)
);

CREATE TABLE IF NOT EXISTS profesores (
    ID_Profesor     VARCHAR(20) NOT NULL,
    Nombre_Profesor VARCHAR(50) NOT NULL,
    Fecha_Ingreso   DATE        NOT NULL,
    Genero          ENUM ('M', 'F', 'Otro'),
    Celular         BIGINT,
    Correo          VARCHAR(50),
    Activo          BOOLEAN     NOT NULL DEFAULT TRUE,
    Especializacion VARCHAR(50),
    PRIMARY KEY (ID_Profesor),
    UNIQUE KEY uk_profesores_correo (Correo)
);

CREATE TABLE IF NOT EXISTS grupos (
    ID_Grupo     VARCHAR(20) NOT NULL,
    Nombre_Grupo VARCHAR(50) NOT NULL,
    ID_Nivel     VARCHAR(20) NOT NULL,
    Cupo_Maximo  INT, -- NULL = sin límite
    PRIMARY KEY (ID_Grupo),
    CONSTRAINT fk_grupos_nivel FOREIGN KEY (ID_Nivel) REFERENCES niveles (ID_Nivel)
);

-- Cada materia tiene un solo profesor por grupo
CREATE TABLE IF NOT EXISTS asignaciones_materia_profesor (
    ID_Asignacion BIGINT      NOT NULL AUTO_INCREMENT,
    ID_Profesor   VARCHAR(20) NOT NULL,
    ID_Grupo      VARCHAR(20) NOT NULL,
    ID_Materia    VARCHAR(20) NOT NULL,
    PRIMARY KEY (ID_Asignacion),
    UNIQUE KEY uk_asignacion_grupo_materia (ID_Grupo, ID_Materia),
    CONSTRAINT fk_asig_profesor FOREIGN KEY (ID_Profesor) REFERENCES profesores (ID_Profesor),
    CONSTRAINT fk_asig_grupo FOREIGN KEY (ID_Grupo) REFERENCES grupos (ID_Grupo),
    CONSTRAINT fk_asig_materia FOREIGN KEY (ID_Materia) REFERENCES materias (ID_Materia)
);

CREATE TABLE IF NOT EXISTS estudiantes (
    ID_Estudiante     VARCHAR(20) NOT NULL,
    Nombre_Estudiante VARCHAR(50) NOT NULL,
    Fecha_Ingreso     DATE        NOT NULL,
    Genero            ENUM ('M', 'F', 'Otro'),
    Celular           BIGINT,
    Correo            VARCHAR(50),
    Activo            BOOLEAN     NOT NULL DEFAULT TRUE,
    Estado            ENUM ('Activo', 'Retirado', 'Graduado', 'Suspendido'),
    PRIMARY KEY (ID_Estudiante),
    UNIQUE KEY uk_estudiantes_correo (Correo)
);

CREATE TABLE IF NOT EXISTS inscripciones (
    ID_Inscripcion   BIGINT      NOT NULL AUTO_INCREMENT,
    anio_inscripcion INT         NOT NULL,
    cohorte          BIGINT,
    ID_Estudiante    VARCHAR(20) NOT NULL,
    ID_Grupo         VARCHAR(20) NOT NULL,
    PRIMARY KEY (ID_Inscripcion),
    UNIQUE KEY uk_inscripcion (ID_Estudiante, ID_Grupo, anio_inscripcion),
    CONSTRAINT fk_insc_estudiante FOREIGN KEY (ID_Estudiante) REFERENCES estudiantes (ID_Estudiante),
    CONSTRAINT fk_insc_grupo FOREIGN KEY (ID_Grupo) REFERENCES grupos (ID_Grupo)
);

CREATE TABLE IF NOT EXISTS asistencia (
    ID_Asistencia    BIGINT      NOT NULL AUTO_INCREMENT,
    Fecha_Asistencia DATE        NOT NULL,
    Asistio          BOOLEAN     NOT NULL,
    Justificada      BOOLEAN, -- solo aplica a inasistencias
    Observacion      VARCHAR(100),
    ID_Estudiante    VARCHAR(20) NOT NULL,
    ID_Grupo         VARCHAR(20) NOT NULL,
    PRIMARY KEY (ID_Asistencia),
    UNIQUE KEY uk_asistencia_dia (ID_Estudiante, ID_Grupo, Fecha_Asistencia),
    CONSTRAINT fk_asis_estudiante FOREIGN KEY (ID_Estudiante) REFERENCES estudiantes (ID_Estudiante),
    CONSTRAINT fk_asis_grupo FOREIGN KEY (ID_Grupo) REFERENCES grupos (ID_Grupo)
);

-- Definitiva NULL = materia en curso; Aprobada la calcula la aplicación (>= 3.0)
CREATE TABLE IF NOT EXISTS notas (
    ID_Nota        BIGINT      NOT NULL AUTO_INCREMENT,
    Definitiva     FLOAT,
    Fecha_Registro DATE        NOT NULL,
    Aprobada       BOOLEAN,
    ID_Estudiante  VARCHAR(20) NOT NULL,
    ID_Materia     VARCHAR(20) NOT NULL,
    PRIMARY KEY (ID_Nota),
    CONSTRAINT fk_notas_estudiante FOREIGN KEY (ID_Estudiante) REFERENCES estudiantes (ID_Estudiante),
    CONSTRAINT fk_notas_materia FOREIGN KEY (ID_Materia) REFERENCES materias (ID_Materia)
);

-- Calificaciones parciales; la definitiva de la nota es su promedio
CREATE TABLE IF NOT EXISTS movimiento_notas (
    ID_Movimiento  BIGINT NOT NULL AUTO_INCREMENT,
    Fecha_Registro DATE   NOT NULL,
    Calificacion   FLOAT  NOT NULL,
    ID_Nota        BIGINT NOT NULL,
    PRIMARY KEY (ID_Movimiento),
    CONSTRAINT fk_mov_nota FOREIGN KEY (ID_Nota) REFERENCES notas (ID_Nota)
);

-- Cuentas de acceso. ESTUDIANTE y PROFESOR quedan vinculados a su registro académico
CREATE TABLE IF NOT EXISTS usuarios (
    ID_Usuario    BIGINT       NOT NULL AUTO_INCREMENT,
    Username      VARCHAR(50)  NOT NULL,
    Password_Hash VARCHAR(100) NOT NULL, -- BCrypt
    Rol           ENUM ('ESTUDIANTE', 'PROFESOR', 'COORDINADOR', 'ADMINISTRATIVO') NOT NULL,
    Activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    ID_Estudiante VARCHAR(20),
    ID_Profesor   VARCHAR(20),
    PRIMARY KEY (ID_Usuario),
    UNIQUE KEY uk_usuarios_username (Username),
    UNIQUE KEY uk_usuarios_estudiante (ID_Estudiante),
    UNIQUE KEY uk_usuarios_profesor (ID_Profesor),
    CONSTRAINT fk_usuarios_estudiante FOREIGN KEY (ID_Estudiante) REFERENCES estudiantes (ID_Estudiante),
    CONSTRAINT fk_usuarios_profesor FOREIGN KEY (ID_Profesor) REFERENCES profesores (ID_Profesor)
);
