package br.com.graflogic.hermitex.cliente.data.impl.aud;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.aud.FormaPagamentoAuditoria;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class FormaPagamentoAuditoriaRepository extends BaseRepository<FormaPagamentoAuditoria> {

	public FormaPagamentoAuditoriaRepository() {
		super(FormaPagamentoAuditoria.class);
	}
}