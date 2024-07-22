package co.edu.iudigital.helpmeiud.utils;

import co.edu.iudigital.helpmeiud.dtos.casos.CasoResponseDTO;
import co.edu.iudigital.helpmeiud.models.Caso;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CasoMapper {


    public List<CasoResponseDTO> toCasoResponseDTOList(List<Caso> casos) {
        return casos.stream()
                .map(this::toCasoResponseDTO)
                .collect(Collectors.toList());
    }

    public CasoResponseDTO toCasoResponseDTO(Caso caso) {
        return CasoResponseDTO.builder()
                .id(caso.getId())
                .fechaHora(caso.getFechaHora())
                .latitud(caso.getLatitud())
                .longitud(caso.getLongitud())
                .altitud(caso.getAltitud())
                .visible(caso.getVisible())
                .descripcion(caso.getDescripcion())
                .urlMapa(caso.getUrlMapa())
                .rmiUrl(caso.getRmiUrl())
                .username(caso.getUsuario().getUsername())
                .delitoId(caso.getDelito().getId())
                .build();
    }
}
