package com.edutech.msusuarios.assembler;

import com.edutech.msusuarios.controller.UsuarioController;
import com.edutech.msusuarios.entity.Usuario;

import org.springframework.hateoas.EntityModel; // 
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<Usuario, EntityModel<Usuario>> {

    @Override
    public EntityModel<Usuario> toModel(Usuario usuario) {
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(usuario.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).obtenerUsuarios()).withRel("usuarios"));
    }
}
