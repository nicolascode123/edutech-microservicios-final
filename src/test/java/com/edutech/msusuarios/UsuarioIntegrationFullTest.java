package com.edutech.msusuarios;

import com.edutech.msusuarios.entity.Usuario;
import com.edutech.msusuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort; 

import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsuarioIntegrationFullTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    public void crearYObtenerUsuario_exito() {
        Usuario usuario = new Usuario();
        usuario.setNombre("TestUser");
        usuario.setEmail("test@example.com");

        ResponseEntity<Usuario> postResponse = restTemplate.postForEntity("http://localhost:" + port + "/usuarios", usuario, Usuario.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Usuario creado = postResponse.getBody();
        assertThat(creado).isNotNull();
        assertThat(creado.getId()).isNotNull();

        ResponseEntity<Usuario> getResponse = restTemplate.getForEntity("http://localhost:" + port + "/usuarios/" + creado.getId(), Usuario.class);
        assertThat(getResponse.getBody().getNombre()).isEqualTo("TestUser");
    }
}
