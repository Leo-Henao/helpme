package co.edu.iudigital.helpmeiud.controllers;

import co.edu.iudigital.helpmeiud.dtos.usuarios.UsuarioRequestDTO;
import co.edu.iudigital.helpmeiud.dtos.usuarios.UsuarioRequestUpdateDTO;
import co.edu.iudigital.helpmeiud.dtos.usuarios.UsuarioResponseDTO;
import co.edu.iudigital.helpmeiud.exceptions.RestException;
import co.edu.iudigital.helpmeiud.services.ifaces.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Slf4j
@Tag(name = "Usuarios Controller", description = "Controlador para gestión de usuarios")
@RestController
@RequestMapping("/usuarios")
@Validated
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Operation(
            summary = "Registro de nuevo usuario",
            description = "Endpoint para registrar un nuevo usuario en el sistema"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UsuarioResponseDTO> register(
            @Valid @RequestBody UsuarioRequestDTO request
    ) throws RestException {
        log.info("Registrando nuevo usuario");
        String passwordEncoded = passwordEncoder.encode(request.getPassword());
        request.setPassword(passwordEncoded);
        UsuarioResponseDTO usuario = usuarioService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Authorization")
    @Operation(
            summary = "Consultar perfil del usuario",
            description = "Endpoint para consultar el perfil del usuario autenticado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil consultado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UsuarioResponseDTO> profile(Authentication authentication) throws RestException {
        log.info("Consultando perfil del usuario");
        UsuarioResponseDTO usuario = usuarioService.consultarPorUsername(authentication);
        return ResponseEntity.ok(usuario);
    }

    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Authorization")
    @Operation(
            summary = "Actualizar perfil del usuario",
            description = "Endpoint para actualizar la información del usuario autenticado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UsuarioResponseDTO> updateProfile(
            @Valid @RequestBody UsuarioRequestUpdateDTO request,
            Authentication authentication
    ) throws RestException {
        log.info("Actualizando perfil del usuario");
        if (request.getPassword() != null) {
            String passwordEncoded = passwordEncoder.encode(request.getPassword());
            request.setPassword(passwordEncoded);
        }
        UsuarioResponseDTO usuario = usuarioService.actualizar(request, authentication);
        return ResponseEntity.ok(usuario);
    }

    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Authorization")
    @Operation(
            summary = "Subir foto de perfil",
            description = "Endpoint para subir la foto de perfil del usuario autenticado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Foto subida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UsuarioResponseDTO> uploadImage(
            @RequestParam("image") MultipartFile image, Authentication authentication
    ) throws RestException {
        log.info("Subiendo foto de perfil");
        UsuarioResponseDTO usuario = usuarioService.subirImagen(image, authentication);
        return ResponseEntity.ok(usuario);
    }

    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Authorization")
    @Operation(
            summary = "Obtener foto de perfil",
            description = "Endpoint para obtener la foto de perfil del usuario autenticado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Foto de perfil obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Foto no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/upload/img/{name:.+}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> getImage(@PathVariable String name) throws RestException {
        log.info("Obteniendo foto de perfil con nombre: {}", name);
        Resource resource = usuarioService.obtenerImagen(name);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
