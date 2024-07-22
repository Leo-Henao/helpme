package co.edu.iudigital.helpmeiud.services.ifaces;

import co.edu.iudigital.helpmeiud.exceptions.RestException;
import co.edu.iudigital.helpmeiud.models.Delito;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IDelitoService {

    Delito crearDelito(Delito delito, Authentication authentication) throws RestException;

    Delito actualizarDelitoPorID(Long id, Delito delito) throws RestException;

    void eliminarDelitoPorID(Long id) throws RestException;

    Delito consultarDelitoPorID(Long id) throws RestException;

    List<Delito> consultarDelitos() throws RestException;
}
