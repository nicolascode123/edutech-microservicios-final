package com.edutech.msusuarios.unit;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import com.edutech.msusuarios.dto.UsuarioRequest;
import com.edutech.msusuarios.entity.Usuario;
import com.edutech.msusuarios.repository.UsuarioRepository;
import com.edutech.msusuarios.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGuardarUsuario() {
        // Arrange
        UsuarioRequest request = new UsuarioRequest("Carlos", "carlos@example.com");
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNombre("Carlos");
        usuarioMock.setEmail("carlos@example.com");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        // Act
        Usuario resultado = usuarioService.guardar(request);

        // Assert
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Carlos");
        assertThat(resultado.getEmail()).isEqualTo("carlos@example.com");
    }

    @Test
    public void testBuscarUsuarioExistente() {
        // Arrange
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNombre("Carlos");
        usuarioMock.setEmail("carlos@example.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));

        // Act
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId(1L);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Carlos");
    }

    @Test
    public void testBuscarUsuarioNoExistente() {
        // Arrange
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = usuarioService.obtenerUsuarioPorId(99L);

        // Assert
        assertThat(resultado).isNotPresent();
    }
}