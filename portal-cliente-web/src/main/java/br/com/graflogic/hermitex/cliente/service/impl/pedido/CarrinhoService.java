package br.com.graflogic.hermitex.cliente.service.impl.pedido;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.graflogic.hermitex.cliente.data.entity.pedido.Carrinho;
import br.com.graflogic.hermitex.cliente.data.impl.pedido.CarrinhoRepository;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;
import br.com.graflogic.hermitex.cliente.web.util.SessionUtil;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class CarrinhoService {

	@Autowired
	private CarrinhoRepository repository;

	public String gera() {
		Carrinho entity = new Carrinho();

		entity.setId(UUID.randomUUID().toString());
		entity.setDataCriacao(new Date());
		entity.setIdUsuario(SessionUtil.getAuthenticatedUsuario().getId());

		repository.store(entity);

		return entity.getId();
	}

	public void atualizaPedido(String id, Long idPedido) {
		Carrinho entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException("Carrinho nao encontrado");
		}

		entity.setIdPedido(idPedido);

		repository.update(entity);
	}
}