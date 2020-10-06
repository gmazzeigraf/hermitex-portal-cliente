package br.com.graflogic.hermitex.cliente.data.impl.aud;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.aud.CotacaoAuditoria;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class CotacaoAuditoriaRepository extends BaseRepository<CotacaoAuditoria> {

	public CotacaoAuditoriaRepository() {
		super(CotacaoAuditoria.class);
	}
}