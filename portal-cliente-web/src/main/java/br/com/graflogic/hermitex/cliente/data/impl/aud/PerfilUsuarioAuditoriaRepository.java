package br.com.graflogic.hermitex.cliente.data.impl.aud;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.aud.PerfilUsuarioAuditoria;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class PerfilUsuarioAuditoriaRepository extends BaseRepository<PerfilUsuarioAuditoria> {

	public PerfilUsuarioAuditoriaRepository() {
		super(PerfilUsuarioAuditoria.class);
	}
}