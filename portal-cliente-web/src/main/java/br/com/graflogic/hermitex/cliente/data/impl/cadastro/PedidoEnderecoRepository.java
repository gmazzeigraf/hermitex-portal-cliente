package br.com.graflogic.hermitex.cliente.data.impl.cadastro;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoEndereco;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class PedidoEnderecoRepository extends BaseRepository<PedidoEndereco> {

	public PedidoEnderecoRepository() {
		super(PedidoEndereco.class);
	}

	public List<PedidoEndereco> consultaPorPedido(Long idPedido) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<PedidoEndereco> query = builder.createQuery(PedidoEndereco.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<PedidoEndereco> table = query.from(PedidoEndereco.class);

		predicateList.add(builder.and(builder.equal(table.get("id").get("idPedido"), idPedido)));

		query.orderBy(builder.asc(table.get("id").get("tipo")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<PedidoEndereco> typedQuery = getEntityManager().createQuery(query);

		return (List<PedidoEndereco>) typedQuery.getResultList();
	}
}