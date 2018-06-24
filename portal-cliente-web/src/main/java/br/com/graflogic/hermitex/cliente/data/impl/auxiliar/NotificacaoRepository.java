package br.com.graflogic.hermitex.cliente.data.impl.auxiliar;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Notificacao;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class NotificacaoRepository extends BaseRepository<Notificacao> {

	public NotificacaoRepository() {
		super(Notificacao.class);
	}

	public List<Notificacao> consulta(Notificacao filter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Notificacao> query = builder.createQuery(Notificacao.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<Notificacao> table = query.from(Notificacao.class);

		if (StringUtils.isNotEmpty(filter.getTipo())) {
			predicateList.add(builder.and(builder.equal(table.get("tipo"), filter.getTipo())));
		}

		if (null != filter.getDataSolicitacaoDe()) {
			predicateList.add(builder.and(builder.greaterThanOrEqualTo(table.get("dataSolicitacao"), filter.getDataSolicitacaoDe())));
		}

		if (null != filter.getDataSolicitacaoAte()) {
			predicateList.add(builder.and(builder.lessThanOrEqualTo(table.get("dataSolicitacao"), filter.getDataSolicitacaoAte())));
		}

		if (StringUtils.isNotEmpty(filter.getStatus())) {
			predicateList.add(builder.and(builder.equal(table.get("status"), filter.getStatus())));
		}

		query.orderBy(builder.desc(table.get("dataSolicitacao")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<Notificacao> typedQuery = getEntityManager().createQuery(query);

		return (List<Notificacao>) typedQuery.getResultList();
	}
}