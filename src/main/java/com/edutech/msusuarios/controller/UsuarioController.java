package com.edutech.msusuarios.controller;

import com.edutech.msusuarios.dto.UsuarioRequest;
import com.edutech.msusuarios.entity.Usuario;
import com.edutech.msusuarios.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/usuarios")
@Validated
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista de todos los usuarios almacenados con enlaces HATEOAS.")
    @GetMapping
    public CollectionModel<EntityModel<Usuario>> obtenerUsuarios() {
        List<EntityModel<Usuario>> usuarios = usuarioRepository.findAll().stream()
                .map(usuario -> EntityModel.of(usuario,
                        linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(usuario.getId())).withSelfRel(),
                        linkTo(methodOn(UsuarioController.class).obtenerUsuarios()).withRel("usuarios")))
                .collect(Collectors.toList());

        return CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioController.class).obtenerUsuarios()).withSelfRel());
    }

    @Operation(summary = "Obtener un usuario por ID", description = "Devuelve los detalles de un usuario específico por su ID.")
    @GetMapping("/{id}")
    public EntityModel<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(id)).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).obtenerUsuarios()).withRel("usuarios"));
    }

    @Operation(
        summary = "Crear un nuevo usuario",
        description = "Registra un nuevo usuario con nombre y email.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Datos del nuevo usuario",
            content = @Content(
                schema = @Schema(implementation = UsuarioRequest.class),
                examples = {
                    @ExampleObject(
                        name = "Ejemplo de Usuario",
                        value = "{\"nombre\": \"Carlos Pérez\", \"email\": \"carlos@example.com\"}"
                    )
                }
            )
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación o email duplicado", content = @Content)
        }
    )
    @PostMapping
    public EntityModel<Usuario> crearUsuario(@Valid @RequestBody UsuarioRequest usuarioRequest) {
        System.out.println("Crear usuario - nombre: " + usuarioRequest.getNombre());
        System.out.println("Crear usuario - email: " + usuarioRequest.getEmail());

        if (usuarioRepository.existsByEmail(usuarioRequest.getEmail())) {
            throw new IllegalArgumentException("Email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioRequest.getNombre());
        usuario.setEmail(usuarioRequest.getEmail());

        Usuario guardado = usuarioRepository.save(usuario);

        return EntityModel.of(guardado,
                linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(guardado.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).obtenerUsuarios()).withRel("usuarios"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body("Error en datos de entrada: " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Error de integridad de datos: " + ex.getRootCause().getMessage());
    }
}
