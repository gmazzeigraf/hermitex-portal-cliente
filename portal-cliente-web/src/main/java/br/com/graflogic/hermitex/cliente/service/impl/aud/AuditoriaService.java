package br.com.graflogic.hermitex.cliente.service.impl.aud;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.graflogic.hermitex.cliente.data.entity.aud.UsuarioLogin;
import br.com.graflogic.hermitex.cliente.data.impl.aud.UsuarioLoginRepository;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class AuditoriaService {

	@Autowired
	private UsuarioLoginRepository loginRepository;

	public String registraLogin(Integer idUsuario, String ipOrigem) {
		UsuarioLogin login = new UsuarioLogin();
		login.setId(UUID.randomUUID().toString());
		login.setData(new Date());
		login.setIdUsuario(idUsuario);
		login.setIpOrigem(ipOrigem);

		loginRepository.store(login);

		return login.getId();
	}

	public List<UsuarioLogin> consultaLogin(UsuarioLogin filter) {
		return loginRepository.consulta(filter);
	}
}