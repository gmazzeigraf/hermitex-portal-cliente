package br.com.graflogic.hermitex.cliente.data.impl.aud;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.aud.RepresentanteAuditoria;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class RepresentanteAuditoriaRepository extends BaseRepository<RepresentanteAuditoria> {

	public RepresentanteAuditoriaRepository() {
		super(RepresentanteAuditoria.class);
	}
}