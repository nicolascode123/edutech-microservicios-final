package com.edutech.msusuarios.controller;

import com.edutech.msusuarios.assembler.UsuarioModelAssembler;
import com.edutech.msusuarios.dto.UsuarioRequest;
import com.edutech.msusuarios.entity.Usuario;
import com.edutech.msusuarios.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/usuarios")
@Validated
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioModelAssembler assembler;

    public UsuarioController(UsuarioService usuarioService, UsuarioModelAssembler assembler) {
        this.usuarioService = usuarioService;
        this.assembler = assembler;
    }

    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista de todos los usuarios almacenados con enlaces HATEOAS.")
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<Usuario>> obtenerUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();

        List<EntityModel<Usuario>> usuariosConLinks = usuarios.stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());

        return CollectionModel.of(usuariosConLinks,
                linkTo(methodOn(UsuarioController.class).obtenerUsuarios()).withSelfRel());
    }

    @Operation(summary = "Obtener un usuario por ID", description = "Devuelve los detalles de un usuario específico por su ID.")
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Usuario>> obtenerUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return ResponseEntity.ok(assembler.toModel(usuario));
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
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación o email duplicado", content = @Content)
        }
    )
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Usuario>> crearUsuario(@Valid @RequestBody UsuarioRequest usuarioRequest) {
        System.out.println("Crear usuario - nombre: " + usuarioRequest.getNombre());
        System.out.println("Crear usuario - email: " + usuarioRequest.getEmail());

        Usuario usuarioGuardado = usuarioService.guardar(usuarioRequest);

        EntityModel<Usuario> usuarioModel = assembler.toModel(usuarioGuardado);

        URI location = linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(usuarioGuardado.getId())).toUri();
        return ResponseEntity.created(location).body(usuarioModel);
    }

    // Manejo de errores
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest()
                .body("Error en datos de entrada: " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Error de integridad de datos";
        if (ex.getRootCause() != null) {
            message += ": " + ex.getRootCause().getMessage();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
}
