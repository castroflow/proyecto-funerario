package com.funeraria.servicio_usuarios.repository;

import com.funeraria.servicio_usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByCedula(String cedula);
    boolean existsByCorreo(String correo);
}