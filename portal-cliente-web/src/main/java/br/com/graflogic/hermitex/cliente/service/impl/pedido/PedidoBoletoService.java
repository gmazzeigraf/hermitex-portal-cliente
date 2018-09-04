package br.com.graflogic.hermitex.cliente.service.impl.pedido;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoBoleto;
import br.com.graflogic.hermitex.cliente.data.impl.pedido.PedidoBoletoRepository;
import br.com.graflogic.hermitex.cliente.service.exception.ResultadoNaoEncontradoException;

/**
 * 
 * @author gmazz
 *
 */
@Service
public class PedidoBoletoService {

	@Autowired
	private PedidoBoletoRepository repository;

	// Consulta
	public List<PedidoBoleto> consulta(PedidoBoleto filter) {
		return repository.consulta(filter);
	}

	public PedidoBoleto consultaPorId(Long id) {
		PedidoBoleto entity = repository.findById(id);

		if (null == entity) {
			throw new ResultadoNaoEncontradoException();
		}

		return entity;
	}
}