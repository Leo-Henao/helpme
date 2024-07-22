package co.edu.iudigital.helpmeiud.services.impl;

import co.edu.iudigital.helpmeiud.exceptions.*;
import co.edu.iudigital.helpmeiud.models.Delito;
import co.edu.iudigital.helpmeiud.models.Usuario;
import co.edu.iudigital.helpmeiud.repositories.IDelitoRepository;
import co.edu.iudigital.helpmeiud.repositories.IUsuarioRepository;
import co.edu.iudigital.helpmeiud.services.ifaces.IDelitoService;
import co.edu.iudigital.helpmeiud.utils.Messages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class DelitoServiceImpl implements IDelitoService {

    @Autowired
    private IDelitoRepository delitoRepository;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public Delito crearDelito(Delito delito, Authentication authentication) throws RestException {
        String username = authentication.getName();

        try{
            Usuario usuario = usuarioRepository.findByUsername(username);
            delito.setUsuario(usuario);
            return delitoRepository.save(delito);
        }catch (Exception e) {
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

    @Transactional
    @Override
    public Delito actualizarDelitoPorID(Long id, Delito delito) throws RestException {
        Delito delitoBD = delitoRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorDto.builder()
                                        .error(Messages.NO_ENCONTRADO)
                                        .message(Messages.DELITO_NO_EXISTE)
                                        .status(404)
                                        .date(LocalDateTime.now())
                                        .build())
                );
        delitoBD.setNombre(delito.getNombre());
        delitoBD.setDescripcion(delito.getDescripcion());
        try{
            return delitoRepository.save(delitoBD);
        }catch (Exception e) {
            throw new InternalServerErrorException(
                    ErrorDto.builder()
                            .error(Messages.ERROR_GENERAL)
                            .status(500)
                            .message("Error consule con el administrador")
                            .date(LocalDateTime.now())
                            .build()
            );
        }

    }

    @Transactional
    @Override
    public void eliminarDelitoPorID(Long id) throws RestException {
        delitoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Delito consultarDelitoPorID(Long id) throws RestException {
        return delitoRepository.findById(id)
                .orElseThrow(() ->
                    new NotFoundException(
                        ErrorDto.builder()
                            .error(Messages.NO_ENCONTRADO)
                            .message(Messages.DELITO_NO_EXISTE)
                            .status(400)
                            .date(LocalDateTime.now())
                            .build())
                );
    }

    @Override
    public List<Delito> consultarDelitos() throws RestException{
        try{
            return delitoRepository.findAll();
        }catch (Exception e) {
            throw new InternalServerErrorException(
                    ErrorDto.builder()
                            .error(Messages.ERROR_GENERAL)
                            .status(500)
                            .message(e.getMessage())
                            .date(LocalDateTime.now())
                            .build()
            );
        }
    }
}
