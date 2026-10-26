package com.funeraria.servicio_usuarios.service;

import com.funeraria.servicio_usuarios.dto.RegistroRequest;
import com.funeraria.servicio_usuarios.dto.UsuarioResponse;
import com.funeraria.servicio_usuarios.exception.ReglaNegocioException;
import com.funeraria.servicio_usuarios.model.Usuario;
import com.funeraria.servicio_usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    private RegistroRequest requestValido() {
        RegistroRequest r = new RegistroRequest();
        r.setCedula("1017123456");
        r.setNombre("Juan Pérez");
        r.setCorreo("juan@correo.com");
        r.setFechaNacimiento(LocalDate.now().minusYears(30));
        r.setPassword("Clave12345");
        return r;
    }

    @Test
    void registrar_conDatosValidos_guardaUsuarioConPasswordCifrada() {
        when(repository.existsByCedula(any())).thenReturn(false);
        when(repository.existsByCorreo(any())).thenReturn(false);
        when(passwordEncoder.encode("Clave12345")).thenReturn("HASH");
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        UsuarioResponse respuesta = service.registrar(requestValido());

        assertEquals("CLIENTE", respuesta.getRol());
        assertEquals(1L, respuesta.getId());

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        assertEquals("HASH", captor.getValue().getPassword());
    }

    @Test
    void registrar_conMenorDeEdad_lanzaExcepcion() {
        RegistroRequest r = requestValido();
        r.setFechaNacimiento(LocalDate.now().minusYears(15));

        assertThrows(ReglaNegocioException.class, () -> service.registrar(r));
        verify(repository, never()).save(any());
    }

    @Test
    void registrar_conCedulaDuplicada_lanzaExcepcion() {
        when(repository.existsByCedula("1017123456")).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> service.registrar(requestValido()));
        verify(repository, never()).save(any());
    }

    @Test
    void registrar_conCorreoDuplicado_lanzaExcepcion() {
        when(repository.existsByCedula(any())).thenReturn(false);
        when(repository.existsByCorreo("juan@correo.com")).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> service.registrar(requestValido()));
        verify(repository, never()).save(any());
    }

    @Test
    void esMayorDeEdad_conExactamente18Anios_retornaTrue() {
        assertTrue(service.esMayorDeEdad(LocalDate.now().minusYears(18)));
    }

    @Test
    void esMayorDeEdad_con17Anios_retornaFalse() {
        assertFalse(service.esMayorDeEdad(LocalDate.now().minusYears(17)));
    }
}
