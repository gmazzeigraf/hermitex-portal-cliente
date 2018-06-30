package br.com.graflogic.hermitex.cliente.data.impl.aud;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.aud.PedidoAuditoria;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class PedidoAuditoriaRepository extends BaseRepository<PedidoAuditoria> {

	public PedidoAuditoriaRepository() {
		super(PedidoAuditoria.class);
	}
}