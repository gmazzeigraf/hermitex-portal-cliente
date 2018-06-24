package br.com.graflogic.hermitex.cliente.data.impl.aud;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.aud.ClienteAuditoria;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class ClienteAuditoriaRepository extends BaseRepository<ClienteAuditoria> {

	public ClienteAuditoriaRepository() {
		super(ClienteAuditoria.class);
	}
}