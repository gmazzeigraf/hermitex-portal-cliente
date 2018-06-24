package br.com.graflogic.hermitex.cliente.data.impl.cadastro;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.cadastro.ClienteEndereco;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class ClienteEnderecoRepository extends BaseRepository<ClienteEndereco> {

	public ClienteEnderecoRepository() {
		super(ClienteEndereco.class);
	}
}