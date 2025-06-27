package com.edutech.msusuarios.integration;

import com.edutech.msusuarios.dto.UsuarioRequest;
import com.edutech.msusuarios.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void limpiarBaseDatos() {
        usuarioRepository.deleteAll();
    }

    @Test
    public void crearYObtenerUsuario_exito() {
        System.out.println("🔍 INICIANDO TEST DE DIAGNÓSTICO");
        System.out.println("Puerto del servidor: " + port);
        
        UsuarioRequest usuarioRequest = new UsuarioRequest();
        usuarioRequest.setNombre("TestUser");
        usuarioRequest.setEmail("testuser_" + System.currentTimeMillis() + "@example.com");
        
        System.out.println("📤 Datos a enviar: " + usuarioRequest.getNombre() + " - " + usuarioRequest.getEmail());

        try {
            // 1. Verificar que el repositorio funciona
            System.out.println("📊 Usuarios en BD antes: " + usuarioRepository.count());
            
            // 2. Preparar la petición
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<UsuarioRequest> request = new HttpEntity<>(usuarioRequest, headers);

            String url = "http://localhost:" + port + "/usuarios";
            System.out.println("🌐 URL objetivo: " + url);

            // 3. Hacer la petición y capturar TODO
            ResponseEntity<String> postResponse = restTemplate.postForEntity(url, request, String.class);

            System.out.println("📥 RESPUESTA COMPLETA:");
            System.out.println("   Status: " + postResponse.getStatusCode());
            System.out.println("   Headers: " + postResponse.getHeaders());
            System.out.println("   Body: " + postResponse.getBody());

            // 4. Si es error 500, mostrar detalles
            if (postResponse.getStatusCode().is5xxServerError()) {
                System.err.println("❌ ERROR 500 DETECTADO!");
                System.err.println("   Cuerpo de la respuesta de error: " + postResponse.getBody());
                
                // Verificar si la aplicación está funcionando con un GET básico
                try {
                    ResponseEntity<String> healthCheck = restTemplate.getForEntity(url, String.class);
                    System.out.println("🔍 Health check GET /usuarios: " + healthCheck.getStatusCode());
                } catch (Exception e) {
                    System.err.println("❌ GET también falla: " + e.getMessage());
                }
                
                // Verificar el estado de la base de datos después del error
                System.out.println("📊 Usuarios en BD después del error: " + usuarioRepository.count());
            }

            // Forzar fallo para ver todos los logs
            assertThat(postResponse.getStatusCode()).as("Respuesta del servidor").isEqualTo(HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("❌ EXCEPCIÓN EN EL TEST:");
            System.err.println("   Tipo: " + e.getClass().getSimpleName());
            System.err.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
            
            // Información adicional de debugging
            System.err.println("🔍 INFORMACIÓN DE DEBUGGING:");
            System.err.println("   Puerto: " + port);
            System.err.println("   Usuarios en BD: " + usuarioRepository.count());
            
            throw new AssertionError("Test falló con excepción: " + e.getMessage(), e);
        }
    }

    @Test
    public void verificarConectividadBasica() {
        System.out.println("🔍 VERIFICANDO CONECTIVIDAD BÁSICA");
        
        try {
            String url = "http://localhost:" + port + "/usuarios";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            System.out.println("✅ GET /usuarios funciona: " + response.getStatusCode());
            System.out.println("   Respuesta: " + response.getBody());
            
            assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NOT_FOUND);
            
        } catch (Exception e) {
            System.err.println("❌ Conectividad básica falló: " + e.getMessage());
            throw e;
        }
    }
}