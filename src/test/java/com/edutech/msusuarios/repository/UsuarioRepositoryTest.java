package com.edutech.msusuarios.repository;
import com.edutech.msusuarios.entity.Usuario;
import com.edutech.msusuarios.repository.UsuarioRepository;
import com.edutech.msusuarios.repository.UsuarioRepositoryTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    public void guardarUsuario_exito() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Prueba");
        usuario.setEmail("prueba@correo.com");

        Usuario guardado = usuarioRepository.save(usuario);

        assertThat(guardado).isNotNull();
        assertThat(guardado.getId()).isNotNull();
    }
}
