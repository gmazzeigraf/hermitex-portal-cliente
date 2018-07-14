package br.com.graflogic.hermitex.cliente.data.impl.aud;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.aud.SolicitacaoTrocaAuditoria;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class SolicitacaoTrocaAuditoriaRepository extends BaseRepository<SolicitacaoTrocaAuditoria> {

	public SolicitacaoTrocaAuditoriaRepository() {
		super(SolicitacaoTrocaAuditoria.class);
	}
}