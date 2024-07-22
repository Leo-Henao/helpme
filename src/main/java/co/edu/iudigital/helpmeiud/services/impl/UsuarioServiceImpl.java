package co.edu.iudigital.helpmeiud.services.impl;

import co.edu.iudigital.helpmeiud.dtos.usuarios.UsuarioRequestDTO;
import co.edu.iudigital.helpmeiud.dtos.usuarios.UsuarioRequestUpdateDTO;
import co.edu.iudigital.helpmeiud.dtos.usuarios.UsuarioResponseDTO;
import co.edu.iudigital.helpmeiud.exceptions.*;
import co.edu.iudigital.helpmeiud.models.Role;
import co.edu.iudigital.helpmeiud.models.Usuario;
import co.edu.iudigital.helpmeiud.repositories.IRoleRepository;
import co.edu.iudigital.helpmeiud.repositories.IUsuarioRepository;
import co.edu.iudigital.helpmeiud.services.ifaces.IEmailService;
import co.edu.iudigital.helpmeiud.services.ifaces.IUsuarioService;
import co.edu.iudigital.helpmeiud.utils.Messages;
import co.edu.iudigital.helpmeiud.utils.UsuarioMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UsuarioServiceImpl implements IUsuarioService, UserDetailsService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private IEmailService emailService;

    @Override
    public List<UsuarioResponseDTO> consultarTodos() throws RestException {
        return null;
    }

    @Override
    public UsuarioResponseDTO registrar(UsuarioRequestDTO usuarioRequestDTO)  throws RestException{

        Usuario usuario = usuarioRepository.findByUsername(usuarioRequestDTO.getUsername());

        if(usuario != null) {
            throw new BadRequestException(
                    ErrorDto.builder()
                            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                            .message("Usuario ya existe")
                            .status(HttpStatus.BAD_REQUEST.value())
                            .date(LocalDateTime.now())
                            .build());
        }

        // TODO: COLOCAR EL TRY-CATCH
        usuario = usuarioMapper.toUsuario(usuarioRequestDTO);
        usuario.setRoles(Collections.singletonList(new Role(2L)));
        usuario.setEnabled(true);

        usuario = usuarioRepository.save(usuario);

        if(usuario != null) {
           if(emailService.sendMail(
                    "Hola " + usuario.getNombre() + " Te has dado de alta en HelpMe IUD con el usuario " + usuario.getUsername(),
                    usuario.getUsername(),
                    "Registro exitoso en Helpe IUD"
            ))
           {
               log.info("Mensaje envido");
           }else{
               log.warn("Mensaje no enviado");
           }
        }

        return usuarioMapper.toUsuarioResponseDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO consultarPorID(Long id)  throws RestException{
        return null;
    }

    @Override
    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public UsuarioResponseDTO consultarPorUsername(Authentication authentication) throws RestException {
        if (!authentication.isAuthenticated()) {
            throw new UnauthorizedException(
                    ErrorDto.builder()
                            .error(Messages.ERROR_GENERAL)
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message(Messages.NO_ENCONTRADO)
                            .date(LocalDateTime.now())
                            .build()
            );
        }

        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByUsername(username);

        if (usuario == null) {
            throw new NotFoundException(
                    ErrorDto.builder()
                            .error(Messages.NO_ENCONTRADO)
                            .message(Messages.USUARIO_NO_EXISTE)
                            .status(HttpStatus.NOT_FOUND.value())
                            .date(LocalDateTime.now())
                            .build()
            );
        }

        return usuarioMapper.toUsuarioResponseDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO actualizar(UsuarioRequestUpdateDTO usuarioRequestUpdateDTO, Authentication authentication) throws RestException {

        Usuario usuario = usuarioRepository.findByUsername(authentication.getName());

        if(usuario == null) {
            throw new BadRequestException(
                    ErrorDto.builder()
                            .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                            .message(Messages.USUARIO_NO_EXISTE)
                            .status(HttpStatus.NOT_FOUND.value())
                            .date(LocalDateTime.now())
                            .build());
        }
        usuario.setNombre(usuarioRequestUpdateDTO.getNombre()!=null ? usuarioRequestUpdateDTO.getNombre() : usuario.getNombre());
        usuario.setApellido(usuarioRequestUpdateDTO.getApellido()!=null ? usuarioRequestUpdateDTO.getApellido() : usuario.getApellido());
        usuario.setPassword(usuarioRequestUpdateDTO.getPassword()!=null ? usuarioRequestUpdateDTO.getPassword() : usuario.getPassword());
        usuario.setFechaNacimiento(usuarioRequestUpdateDTO.getFechaNacimiento()!=null ? usuarioRequestUpdateDTO.getFechaNacimiento() : usuario.getFechaNacimiento());

        usuario = usuarioRepository.save(usuario);

        return usuarioMapper.toUsuarioResponseDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO subirImagen(MultipartFile image, Authentication authentication) throws RestException {
        Usuario usuario = usuarioRepository.findByUsername(authentication.getName());
        if(!image.isEmpty()) {
            String nombreImage = UUID
                    .randomUUID()
                    .toString()
                    .concat("_")
                    .concat(image.getOriginalFilename())
                    .replace(" ", "");
            Path path = Paths.get("uploads").resolve(nombreImage).toAbsolutePath();
            try {
                Files.copy(image.getInputStream(), path);
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new BadRequestException(
                        ErrorDto.builder()
                                .message(e.getMessage())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Error De imagen")
                        .build()
                );
            }



            String imageAnterior = usuario.getImage();

            if(imageAnterior != null && imageAnterior.length() > 0 && !imageAnterior.startsWith("http")){
                Path pathAnterior = Paths.get("uploads").resolve(imageAnterior).toAbsolutePath();
                File fileAnterior = pathAnterior.toFile();
                if(fileAnterior.exists() && fileAnterior.canRead()) {
                    fileAnterior.delete();
                }
            }

            usuario.setImage(nombreImage);

            usuarioRepository.save(usuario);

        }
        return usuarioMapper.toUsuarioResponseDTO(usuario);
    }

    @Override
    public Resource obtenerImagen(String name) throws RestException {
        Path path = Paths.get("uploads").resolve(name).toAbsolutePath();
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
            if(!resource.exists()) {
                path = Paths.get("uploads").resolve("default.jpg").toAbsolutePath();
                resource = new UrlResource(path.toUri());
            }
        } catch (MalformedURLException e) {
            log.error(e.getMessage());
        }
        return resource;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username);
        if(usuario == null) {
            log.error("Usuario no existe: " + username);
            throw new UsernameNotFoundException("Usuario no existe: " + username);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        for(Role role : usuario.getRoles()) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getNombre());
            authorities.add(grantedAuthority);
        }

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getEnabled(),
                true,
                true,
                true,
                authorities);
    }
}
