package co.edu.iudigital.helpmeiud.services.ifaces;

import co.edu.iudigital.helpmeiud.dtos.usuarios.UsuarioRequestDTO;
import co.edu.iudigital.helpmeiud.dtos.usuarios.UsuarioRequestUpdateDTO;
import co.edu.iudigital.helpmeiud.dtos.usuarios.UsuarioResponseDTO;
import co.edu.iudigital.helpmeiud.exceptions.RestException;
import co.edu.iudigital.helpmeiud.models.Usuario;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IUsuarioService {

    List<UsuarioResponseDTO> consultarTodos() throws RestException;

    UsuarioResponseDTO registrar(UsuarioRequestDTO usuarioRequestDTO)  throws RestException;

    UsuarioResponseDTO consultarPorID(Long id)  throws RestException;

    Usuario findByUsername(String username);

    UsuarioResponseDTO consultarPorUsername(Authentication authentication) throws RestException;

    UsuarioResponseDTO actualizar(UsuarioRequestUpdateDTO usuarioRequestUpdateDTO, Authentication authentication) throws RestException;

    UsuarioResponseDTO subirImagen(MultipartFile image, Authentication authentication) throws RestException;

    Resource obtenerImagen(String name) throws RestException;
}
