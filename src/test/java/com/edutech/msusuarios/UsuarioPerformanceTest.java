package com.edutech.msusuarios;
import com.edutech.msusuarios.entity.Usuario;
import com.edutech.msusuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UsuarioPerformanceTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    public void crearMuchosUsuarios_rapidamente() {
        long inicio = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            Usuario usuario = new Usuario();
            usuario.setNombre("Usuario" + i);
            usuario.setEmail("usuario" + i + "@test.com");
            usuarioRepository.save(usuario);
        }

        long fin = System.currentTimeMillis();
        long duracion = fin - inicio;
        System.out.println("Tiempo para guardar 1000 usuarios: " + duracion + " ms");

        // Tiempo estimado menor a 5 segundos 
        assert duracion < 5000;
    }
}

