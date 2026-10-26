package com.funeraria.servicio_usuarios.service;

import com.funeraria.servicio_usuarios.dto.RegistroRequest;
import com.funeraria.servicio_usuarios.dto.UsuarioResponse;
import com.funeraria.servicio_usuarios.exception.ReglaNegocioException;
import com.funeraria.servicio_usuarios.model.Rol;
import com.funeraria.servicio_usuarios.model.Usuario;
import com.funeraria.servicio_usuarios.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;

@Service
public class UsuarioService {

    private static final int EDAD_MINIMA = 18;

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponse registrar(RegistroRequest request) {
        if (!esMayorDeEdad(request.getFechaNacimiento())) {
            throw new ReglaNegocioException("Debe ser mayor de 18 años para registrarse");
        }
        if (repository.existsByCedula(request.getCedula())) {
            throw new ReglaNegocioException("La cédula ya está registrada");
        }
        if (repository.existsByCorreo(request.getCorreo())) {
            throw new ReglaNegocioException("El correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setCedula(request.getCedula());
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(Rol.CLIENTE);

        Usuario guardado = repository.save(usuario);
        return new UsuarioResponse(guardado.getId(), guardado.getCedula(),
                guardado.getNombre(), guardado.getCorreo(), guardado.getRol().name());
    }

    boolean esMayorDeEdad(LocalDate fechaNacimiento) {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears() >= EDAD_MINIMA;
    }
}