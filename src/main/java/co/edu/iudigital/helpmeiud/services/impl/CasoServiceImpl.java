package co.edu.iudigital.helpmeiud.services.impl;

import co.edu.iudigital.helpmeiud.dtos.casos.CasoRequestDTO;
import co.edu.iudigital.helpmeiud.dtos.casos.CasoResponseDTO;
import co.edu.iudigital.helpmeiud.exceptions.ErrorDto;
import co.edu.iudigital.helpmeiud.exceptions.InternalServerErrorException;
import co.edu.iudigital.helpmeiud.exceptions.NotFoundException;
import co.edu.iudigital.helpmeiud.exceptions.RestException;
import co.edu.iudigital.helpmeiud.models.Caso;
import co.edu.iudigital.helpmeiud.models.Delito;
import co.edu.iudigital.helpmeiud.models.Usuario;
import co.edu.iudigital.helpmeiud.repositories.ICasoRepository;
import co.edu.iudigital.helpmeiud.repositories.IDelitoRepository;
import co.edu.iudigital.helpmeiud.repositories.IUsuarioRepository;
import co.edu.iudigital.helpmeiud.services.ifaces.ICasoService;
import co.edu.iudigital.helpmeiud.utils.CasoMapper;
import co.edu.iudigital.helpmeiud.utils.Messages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CasoServiceImpl implements ICasoService {

    @Autowired
    private CasoMapper casoMapper;

    @Autowired
    private ICasoRepository casoRepository;

    @Autowired
    private IDelitoRepository delitoRepository;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public List<CasoResponseDTO> consultarCasos() throws RestException {
        log.info("consultarCasos CasoServiceImpl");
        try{
            final List<Caso> casos = casoRepository.findAll();
            final List<CasoResponseDTO> casoResponseDTOList =
                    casoMapper.toCasoResponseDTOList(casos);
            return casoResponseDTOList;
        }catch (Exception e) {
            log.error("Error Consultando casos: {}",e.getMessage());
            throw new InternalServerErrorException(
                    ErrorDto.builder()
                            .error("Error General")
                            .status(500)
                            .message(e.getMessage())
                            .date(LocalDateTime.now())
                            .build()
            );
        }
    }

    @Override
    public List<CasoResponseDTO> consultarCasosVisibles() throws RestException {
        log.info("consultarCasosVisibles CasoServiceImpl");
        try{
            final List<Caso> casos = casoRepository.findAllByVisibleTrue();
            final List<CasoResponseDTO> casoResponseDTOList =
                    casoMapper.toCasoResponseDTOList(casos);
            return casoResponseDTOList;
        }catch (Exception e) {
            log.error("Error Consultando casos: {}",e.getMessage());
            throw new InternalServerErrorException(
                    ErrorDto.builder()
                            .error("Error General")
                            .status(500)
                            .message(e.getMessage())
                            .date(LocalDateTime.now())
                            .build()
            );
        }
    }

    @Override
    public List<CasoResponseDTO> consultarCasosPorUsuario(String username) throws RestException {
        return null;
    }

    @Override
    public CasoResponseDTO consultarCasoPorId(Long id) throws RestException {
        return null;
    }

    @Override
    public CasoResponseDTO guardarCaso(final CasoRequestDTO caso, Authentication authentication) throws RestException {
        log.info("consultarCasos CasoServiceImpl");
        String username = authentication.getName();
        final Delito delitoBD = delitoRepository.findById(caso.getDelitoId())
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorDto.builder()
                                        .error(Messages.NO_ENCONTRADO)
                                        .message(Messages.DELITO_NO_EXISTE)
                                        .status(404)
                                        .date(LocalDateTime.now())
                                        .build())
                );
        Usuario usuarioDB = usuarioRepository.findByUsername(username);
        if(usuarioDB == null ) {
            throw new NotFoundException(
                    ErrorDto.builder()
                            .error("Usuario No encontrado")
                            .message("Usuario No existe")
                            .status(404)
                            .date(LocalDateTime.now())
                            .build());
        }
        try{
            Caso casoEntity = new Caso();
            casoEntity.setFechaHora(caso.getFechaHora());
            casoEntity.setLatitud(caso.getLatitud());
            casoEntity.setLongitud(caso.getLongitud());
            casoEntity.setAltitud(caso.getAltitud());
            casoEntity.setVisible(true);
            casoEntity.setDescripcion(caso.getDescripcion());
            casoEntity.setUrlMapa(caso.getUrlMapa());
            casoEntity.setRmiUrl(caso.getRmiUrl());
            casoEntity.setUsuario(usuarioDB);
            casoEntity.setDelito(delitoBD);

            casoEntity = casoRepository.save(casoEntity);
            return casoMapper.toCasoResponseDTO(casoEntity);
        } catch (Exception e) {
            log.error("Error Consultando casos: {}",e.getMessage());
            throw new InternalServerErrorException(
                    ErrorDto.builder()
                            .error("Error General")
                            .status(500)
                            .message(e.getMessage())
                            .date(LocalDateTime.now())
                            .build()
            );
        }
    }

    @Override
    public Boolean visibilizar(final Boolean visible, final Long id) throws RestException {
        log.info("visibilizar CasoServiceImpl");
        Caso casoBD = casoRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorDto.builder()
                                        .error(Messages.NO_ENCONTRADO)
                                        .message("Caso No existe")
                                        .status(HttpStatus.NOT_FOUND.value())
                                        .date(LocalDateTime.now())
                                        .build())
                );
        try{
            casoBD.setVisible(visible);
            casoBD = casoRepository.saveAndFlush(casoBD);
            if(casoBD != null) {
                return true;
            }
            return false;
        }catch (Exception e) {
            log.error("Error Actualizado caso: {}",e.getMessage());
            throw new InternalServerErrorException(
                    ErrorDto.builder()
                            .error("Error General")
                            .status(500)
                            .message("Error al intentar actualizar")
                            .date(LocalDateTime.now())
                            .build()
            );
        }
    }
}
