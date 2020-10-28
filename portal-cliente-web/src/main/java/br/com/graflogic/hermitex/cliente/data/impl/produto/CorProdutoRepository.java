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

import br.com.graflogic.hermitex.cliente.data.entity.produto.CorProduto;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class CorProdutoRepository extends BaseRepository<CorProduto> {

	public CorProdutoRepository() {
		super(CorProduto.class);
	}

	public List<CorProduto> consulta(CorProduto entity) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CorProduto> query = builder.createQuery(CorProduto.class);
		List<Predicate> predicateList = new ArrayList<>();

		Root<CorProduto> table = query.from(CorProduto.class);

		if (StringUtils.isNotEmpty(entity.getStatus())) {
			predicateList.add(builder.and(builder.equal(table.get("status"), entity.getStatus())));
		}

		query.orderBy(builder.asc(table.get("ordem")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<CorProduto> typedQuery = getEntityManager().createQuery(query);

		return typedQuery.getResultList();
	}
}