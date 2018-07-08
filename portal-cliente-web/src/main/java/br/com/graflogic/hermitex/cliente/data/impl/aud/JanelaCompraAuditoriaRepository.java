package br.com.graflogic.hermitex.cliente.data.impl.aud;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.aud.JanelaCompraAuditoria;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class JanelaCompraAuditoriaRepository extends BaseRepository<JanelaCompraAuditoria> {

	public JanelaCompraAuditoriaRepository() {
		super(JanelaCompraAuditoria.class);
	}
}