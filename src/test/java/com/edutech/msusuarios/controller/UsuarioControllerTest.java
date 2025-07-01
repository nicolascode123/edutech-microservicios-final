package com.edutech.msusuarios.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.edutech.msusuarios.assembler.UsuarioModelAssembler;
import com.edutech.msusuarios.entity.Usuario;
import com.edutech.msusuarios.service.UsuarioService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioModelAssembler assembler;

    @Test
    public void listarUsuarios_devuelve200_conHateoas() throws Exception {

        // Creamos un usuario ficticio
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setEmail("juan@example.com");

        // Mockeamos el servicio para que devuelva una lista con este usuario
        when(usuarioService.obtenerTodosLosUsuarios()).thenReturn(Arrays.asList(usuario));
        when(assembler.toModel(usuario)).thenReturn(
                org.springframework.hateoas.EntityModel.of(usuario)
                        .add(org.springframework.hateoas.Link.of("http://localhost/usuarios/1").withSelfRel())
        );

        mockMvc.perform(get("/usuarios")
                        .accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._embedded.usuarios").exists())
                .andExpect(jsonPath("$._embedded.usuarios[0].nombre").value("Juan"))
                .andExpect(jsonPath("$._embedded.usuarios[0].email").value("juan@example.com"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }
}
