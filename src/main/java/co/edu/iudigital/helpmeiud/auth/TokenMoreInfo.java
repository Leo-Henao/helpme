package co.edu.iudigital.helpmeiud.auth;

import co.edu.iudigital.helpmeiud.models.Usuario;
import co.edu.iudigital.helpmeiud.services.ifaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class TokenMoreInfo implements TokenEnhancer {

	@Autowired
	private IUsuarioService usuarioService;

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		String username = authentication.getName();
		Usuario usuario = usuarioService.findByUsername(username);

		if (usuario != null) {
			Map<String, Object> info = new HashMap<>();
			info.put("id_usuario", usuario.getId().toString());
			info.put("nombre", usuario.getNombre());
			info.put("image", usuario.getImage());
			info.put("fecha_nacimiento", usuario.getFechaNacimiento().toString());
			((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
		} else {
			throw new UsernameNotFoundException("Usuario no encontrado: " + username);
		}

		return accessToken;
	}
}
