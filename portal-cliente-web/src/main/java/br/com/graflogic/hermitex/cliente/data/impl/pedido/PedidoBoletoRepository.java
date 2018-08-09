package br.com.graflogic.hermitex.cliente.data.impl.pedido;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

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