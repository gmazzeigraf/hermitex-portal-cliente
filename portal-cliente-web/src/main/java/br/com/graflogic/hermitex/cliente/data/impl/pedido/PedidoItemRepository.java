package br.com.graflogic.hermitex.cliente.data.impl.pedido;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoItem;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class PedidoItemRepository extends BaseRepository<PedidoItem> {

	public PedidoItemRepository() {
		super(PedidoItem.class);
	}

	public List<PedidoItem> consultaPorPedido(Long idPedido) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<PedidoItem> query = builder.createQuery(PedidoItem.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<PedidoItem> table = query.from(PedidoItem.class);

		predicateList.add(builder.and(builder.equal(table.get("idPedido"), idPedido)));

		query.orderBy(builder.asc(table.get("desc")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<PedidoItem> typedQuery = getEntityManager().createQuery(query);

		return (List<PedidoItem>) typedQuery.getResultList();
	}
}