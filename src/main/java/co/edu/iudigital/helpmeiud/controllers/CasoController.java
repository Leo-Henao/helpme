package co.edu.iudigital.helpmeiud.controllers;

import co.edu.iudigital.helpmeiud.dtos.casos.CasoRequestDTO;
import co.edu.iudigital.helpmeiud.dtos.casos.CasoRequestVisibleDTO;
import co.edu.iudigital.helpmeiud.dtos.casos.CasoResponseDTO;
import co.edu.iudigital.helpmeiud.exceptions.RestException;
import co.edu.iudigital.helpmeiud.services.ifaces.ICasoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Casos Controller", description = "Controlador para gestión de casos")
@RestController
@RequestMapping("/casos")
@Slf4j
public class CasoController {

    @Autowired
    private ICasoService casoService;

    @Operation(
            summary = "Consultar todos los casos",
            description = "Endpoint para consultar todos los casos"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta exitosa"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<CasoResponseDTO>> getAllCases() throws RestException {
        log.info("Consultando todos los casos");
        List<CasoResponseDTO> casos = casoService.consultarCasos();
        return ResponseEntity.ok(casos);
    }

    @Operation(
            summary = "Consultar todos los casos visibles",
            description = "Endpoint para consultar todos los casos visibles"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta exitosa"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/visibles")
    public ResponseEntity<List<CasoResponseDTO>> getVisibleCases() throws RestException {
        log.info("Consultando todos los casos visibles");
        List<CasoResponseDTO> casosVisibles = casoService.consultarCasosVisibles();
        return ResponseEntity.ok(casosVisibles);
    }

    @Secured({"rol_admin", "rol_user"})
    @SecurityRequirement(name = "Authorization")
    @Operation(
            summary = "Crear un caso",
            description = "Endpoint para crear un caso"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Caso creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Prohibido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<CasoResponseDTO> createCase(
            @RequestBody CasoRequestDTO caso,
            Authentication authentication
    ) throws RestException {
        log.info("Creando un nuevo caso");
        CasoResponseDTO casoCreado = casoService.guardarCaso(caso, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(casoCreado);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Authorization")
    @Operation(
            summary = "Actualizar visibilidad de un caso",
            description = "Endpoint para visualizar u ocultar un caso"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Visibilidad actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Prohibido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<Boolean> updateCaseVisibility(
            @PathVariable Long id,
            @RequestBody CasoRequestVisibleDTO request
    ) throws RestException {
        log.info("Actualizando visibilidad del caso con ID: {}", id);
        Boolean visibilidadActualizada = casoService.visibilizar(request.getVisible(), id);
        return ResponseEntity.ok(visibilidadActualizada);
    }
}
