package br.com.graflogic.hermitex.cliente.data.impl.pedido;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.pedido.Carrinho;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class CarrinhoRepository extends BaseRepository<Carrinho> {

	public CarrinhoRepository() {
		super(Carrinho.class);
	}
}