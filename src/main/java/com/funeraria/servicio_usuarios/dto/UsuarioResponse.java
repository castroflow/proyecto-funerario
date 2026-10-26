package com.funeraria.servicio_usuarios.dto;

import lombok.Getter;

@Getter
public class UsuarioResponse {
    private Long id;
    private String cedula;
    private String nombre;
    private String correo;
    private String rol;

    public UsuarioResponse(Long id, String cedula, String nombre, String correo, String rol) {
        this.id = id;
        this.cedula = cedula;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
    }
}
