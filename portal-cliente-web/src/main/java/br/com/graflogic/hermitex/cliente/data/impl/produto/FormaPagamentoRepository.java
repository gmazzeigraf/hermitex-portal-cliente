package br.com.graflogic.hermitex.cliente.data.impl.produto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.produto.FormaPagamento;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class FormaPagamentoRepository extends BaseRepository<FormaPagamento> {

	public FormaPagamentoRepository() {
		super(FormaPagamento.class);
	}

	public List<FormaPagamento> consulta(FormaPagamento entity) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<FormaPagamento> query = builder.createQuery(FormaPagamento.class);
		List<Predicate> predicateList = new ArrayList<>();

		Root<FormaPagamento> table = query.from(FormaPagamento.class);

		if (StringUtils.isNotEmpty(entity.getTipo())) {
			predicateList.add(builder.and(builder.equal(table.get("tipo"), entity.getTipo())));
		}

		if (null != entity.getIdCliente() && 0 != entity.getIdCliente()) {
			predicateList.add(builder.and(builder.equal(table.get("idCliente"), entity.getIdCliente())));
		}

		if (StringUtils.isNotEmpty(entity.getStatus())) {
			predicateList.add(builder.and(builder.equal(table.get("status"), entity.getStatus())));
		}

		if (StringUtils.isNotEmpty(entity.getPedidoFaturado())) {
			predicateList.add(builder.and(builder.equal(table.get("pedidoFaturado"), entity.getPedidoFaturado())));
		}

		query.orderBy(builder.asc(table.get("tipo")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<FormaPagamento> typedQuery = getEntityManager().createQuery(query);

		return typedQuery.getResultList();
	}
}