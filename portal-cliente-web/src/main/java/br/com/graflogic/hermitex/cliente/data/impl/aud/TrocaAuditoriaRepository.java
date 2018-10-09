package br.com.graflogic.hermitex.cliente.data.impl.aud;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.aud.TrocaAuditoria;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class TrocaAuditoriaRepository extends BaseRepository<TrocaAuditoria> {

	public TrocaAuditoriaRepository() {
		super(TrocaAuditoria.class);
	}
}