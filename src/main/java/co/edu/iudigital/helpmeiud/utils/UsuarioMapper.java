package co.edu.iudigital.helpmeiud.utils;

import co.edu.iudigital.helpmeiud.dtos.usuarios.UsuarioRequestDTO;
import co.edu.iudigital.helpmeiud.dtos.usuarios.UsuarioResponseDTO;
import co.edu.iudigital.helpmeiud.models.Role;
import co.edu.iudigital.helpmeiud.models.Usuario;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class UsuarioMapper {

    public Usuario toUsuario(UsuarioRequestDTO usuarioRequestDTO){
        Usuario usuario = new Usuario();
        usuario.setUsername(usuarioRequestDTO.getUsername());
        usuario.setNombre(usuarioRequestDTO.getNombre());
        usuario.setApellido(usuarioRequestDTO.getApellido());
        usuario.setPassword(usuarioRequestDTO.getPassword());
        usuario.setFechaNacimiento(usuarioRequestDTO.getFechaNacimiento());
        usuario.setEnabled(usuarioRequestDTO.getEnabled());
        usuario.setRedSocial(usuarioRequestDTO.getRedSocial());
        usuario.setImage(usuarioRequestDTO.getImage());
        return usuario;
    }

    public UsuarioResponseDTO toUsuarioResponseDTO(Usuario usuario){
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .password(usuario.getPassword())
                .fechaNacimiento(usuario.getFechaNacimiento())
                .enabled(usuario.getEnabled())
                .redSocial(usuario.getRedSocial())
                .image(usuario.getImage())
                .roles(Collections.singletonList(usuario.getRoles().get(0).getNombre()))
                .build();
    }
}
