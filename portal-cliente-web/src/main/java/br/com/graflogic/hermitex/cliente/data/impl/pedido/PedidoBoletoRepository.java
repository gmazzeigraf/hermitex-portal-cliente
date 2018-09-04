package br.com.graflogic.hermitex.cliente.data.impl.pedido;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.pedido.Pedido;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoBoleto;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class PedidoBoletoRepository extends BaseRepository<PedidoBoleto> {

	public PedidoBoletoRepository() {
		super(PedidoBoleto.class);
	}

	public List<PedidoBoleto> consulta(PedidoBoleto filter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<PedidoBoleto> query = builder.createQuery(PedidoBoleto.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<PedidoBoleto> table = query.from(PedidoBoleto.class);

		if (null != filter.getIdPedido() && 0 != filter.getIdPedido()) {
			predicateList.add(builder.and(builder.equal(table.get("idPedido"), filter.getIdPedido())));
		}

		if (null != filter.getIdCliente() && 0 != filter.getIdCliente()) {
			Join<PedidoBoleto, Pedido> join = table.join("pedido");

			predicateList.add(builder.and(builder.equal(join.get("idCliente"), filter.getIdCliente())));
		}

		if (null != filter.getIdFilial() && 0 != filter.getIdFilial()) {
			Join<PedidoBoleto, Pedido> join = table.join("pedido");

			predicateList.add(builder.and(builder.equal(join.get("idFilial"), filter.getIdFilial())));
		}

		if (null != filter.getDataVencimentoDe()) {
			predicateList.add(builder.and(builder.greaterThanOrEqualTo(table.<Date>get("dataVencimento"), filter.getDataVencimentoDe())));
		}

		if (null != filter.getDataVencimentoAte()) {
			predicateList.add(builder.and(builder.lessThanOrEqualTo(table.<Date>get("dataVencimento"), filter.getDataVencimentoAte())));
		}

		if (StringUtils.isNotEmpty(filter.getStatus())) {
			predicateList.add(builder.and(builder.equal(table.get("status"), filter.getStatus())));
		}

		query.orderBy(builder.asc(table.get("dataVencimento")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<PedidoBoleto> typedQuery = getEntityManager().createQuery(query);

		return (List<PedidoBoleto>) typedQuery.getResultList();
	}

	public List<PedidoBoleto> consultaPorPedido(Long idPedido) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<PedidoBoleto> query = builder.createQuery(PedidoBoleto.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<PedidoBoleto> table = query.from(PedidoBoleto.class);

		predicateList.add(builder.and(builder.equal(table.get("idPedido"), idPedido)));

		query.orderBy(builder.asc(table.get("dataVencimento")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<PedidoBoleto> typedQuery = getEntityManager().createQuery(query);

		return (List<PedidoBoleto>) typedQuery.getResultList();
	}
}