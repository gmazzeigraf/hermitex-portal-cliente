package br.com.graflogic.hermitex.cliente.data.impl.cadastro;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.cadastro.ClienteParametro;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class ClienteParametroRepository extends BaseRepository<ClienteParametro> {

	public ClienteParametroRepository() {
		super(ClienteParametro.class);
	}
}