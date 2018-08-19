package br.com.graflogic.hermitex.cliente.data.impl.aud;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.aud.SolicitacaoEstoqueAuditoria;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class SolicitacaoEstoqueAuditoriaRepository extends BaseRepository<SolicitacaoEstoqueAuditoria> {

	public SolicitacaoEstoqueAuditoriaRepository() {
		super(SolicitacaoEstoqueAuditoria.class);
	}
}