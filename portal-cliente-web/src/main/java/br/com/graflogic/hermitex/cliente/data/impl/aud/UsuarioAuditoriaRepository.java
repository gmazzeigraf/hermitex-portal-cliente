package br.com.graflogic.hermitex.cliente.data.impl.aud;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.aud.UsuarioAuditoria;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class UsuarioAuditoriaRepository extends BaseRepository<UsuarioAuditoria> {

	public UsuarioAuditoriaRepository() {
		super(UsuarioAuditoria.class);
	}
}