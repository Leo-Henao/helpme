package co.edu.iudigital.helpmeiud.services.ifaces;

import co.edu.iudigital.helpmeiud.dtos.casos.CasoRequestDTO;
import co.edu.iudigital.helpmeiud.dtos.casos.CasoResponseDTO;
import co.edu.iudigital.helpmeiud.exceptions.RestException;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ICasoService {

    List<CasoResponseDTO> consultarCasos() throws RestException;

    List<CasoResponseDTO> consultarCasosVisibles() throws RestException;

    List<CasoResponseDTO> consultarCasosPorUsuario(String username) throws RestException;

    CasoResponseDTO consultarCasoPorId(Long id) throws RestException;

    CasoResponseDTO guardarCaso(CasoRequestDTO casoString, Authentication authentication) throws RestException;

    Boolean visibilizar(Boolean visible, Long id) throws RestException;
}
