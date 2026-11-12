package com.example.gestionacademica.presentation.dto;

import jakarta.validation.groups.Default;

// Grupo de validación del POST: además de las reglas normales exige campos que el PUT no pide
// (el id va en la URL al actualizar, y la contraseña es opcional al editar un usuario)
public interface AlCrear extends Default {
}
