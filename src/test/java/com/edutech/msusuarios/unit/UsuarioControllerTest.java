package com.edutech.msusuarios.unit;
import com.edutech.msusuarios.controller.UsuarioController;
import com.edutech.msusuarios.entity.Usuario;
import com.edutech.msusuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    public void listarUsuarios_devuelve200() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Test");
        usuario.setEmail("test@correo.com");

        when(usuarioRepository.findAll()).thenReturn(Collections.singletonList(usuario));

        mockMvc.perform(get("/usuarios").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usuarioList[0].nombre").value("Test"))
                .andExpect(jsonPath("$._embedded.usuarioList[0].email").value("test@correo.com"))
                .andExpect(jsonPath("$._embedded.usuarioList[0]._links.self.href").exists())
                .andExpect(jsonPath("$._links.self.href").exists());
    }
}
