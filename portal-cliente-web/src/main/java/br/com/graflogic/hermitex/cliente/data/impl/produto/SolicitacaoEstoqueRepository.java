package br.com.graflogic.hermitex.cliente.data.impl.produto;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.produto.SolicitacaoEstoque;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class SolicitacaoEstoqueRepository extends BaseRepository<SolicitacaoEstoque> {

	public SolicitacaoEstoqueRepository() {
		super(SolicitacaoEstoque.class);
	}
}