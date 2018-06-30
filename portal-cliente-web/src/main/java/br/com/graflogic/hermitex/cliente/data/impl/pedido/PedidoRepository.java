package br.com.graflogic.hermitex.cliente.data.impl.pedido;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.pedido.Pedido;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class PedidoRepository extends BaseRepository<Pedido> {

	public PedidoRepository() {
		super(Pedido.class);
	}

	public List<Pedido> consulta(Pedido entity) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Pedido> query = builder.createQuery(Pedido.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<Pedido> table = query.from(Pedido.class);

		if (null != entity.getIdCliente() && 0 != entity.getIdCliente()) {
			predicateList.add(builder.and(builder.equal(table.get("idCliente"), entity.getIdCliente())));
		}

		if (null != entity.getIdFilial() && 0 != entity.getIdFilial()) {
			predicateList.add(builder.and(builder.equal(table.get("idFilial"), entity.getIdFilial())));
		}

		if (StringUtils.isNotEmpty(entity.getStatus())) {
			predicateList.add(builder.and(builder.equal(table.get("status"), entity.getStatus())));
		}

		if (StringUtils.isNotEmpty(entity.getStatusPagamento())) {
			predicateList.add(builder.and(builder.equal(table.get("pagamento"), entity.getStatusPagamento())));
		}

		query.orderBy(builder.desc(table.get("id")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<Pedido> typedQuery = getEntityManager().createQuery(query);

		return (List<Pedido>) typedQuery.getResultList();
	}
}