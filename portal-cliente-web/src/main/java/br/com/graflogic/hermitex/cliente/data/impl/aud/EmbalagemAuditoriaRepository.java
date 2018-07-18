package br.com.graflogic.hermitex.cliente.data.impl.aud;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.aud.EmbalagemAuditoria;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class EmbalagemAuditoriaRepository extends BaseRepository<EmbalagemAuditoria> {

	public EmbalagemAuditoriaRepository() {
		super(EmbalagemAuditoria.class);
	}
}