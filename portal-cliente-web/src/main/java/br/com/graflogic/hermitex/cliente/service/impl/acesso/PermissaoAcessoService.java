package br.com.graflogic.hermitex.cliente.service.impl.acesso;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.graflogic.base.service.util.CacheUtil;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.PermissaoAcesso;
import br.com.graflogic.hermitex.cliente.data.impl.acesso.PermissaoAcessoRepository;
import br.com.graflogic.utilities.datautil.copy.ObjectCopier;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class PermissaoAcessoService {

	private static final String CACHE_NAME = "permissoesAcesso";

	@Autowired
	private PermissaoAcessoRepository repository;

	@Autowired
	private CacheUtil cacheUtil;

	@SuppressWarnings("unchecked")
	public List<PermissaoAcesso> consulta(String tipoUsuario) {
		Object cacheObj = cacheUtil.findOnCache(CACHE_NAME, tipoUsuario);

		if (null == cacheObj) {
			List<PermissaoAcesso> objetos = repository.consulta(tipoUsuario);
			cacheUtil.putOnCache(CACHE_NAME, tipoUsuario, ObjectCopier.copy(objetos));

			cacheObj = cacheUtil.findOnCache(CACHE_NAME, tipoUsuario);
		}

		return (List<PermissaoAcesso>) ObjectCopier.copy(cacheObj);
	}

	public List<PermissaoAcesso> consultaPorPerfilUsuario(Integer idPerfilUsuario) {
		return repository.consultaPorPerfilUsuario(idPerfilUsuario);
	}
}