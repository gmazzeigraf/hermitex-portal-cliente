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

import br.com.graflogic.hermitex.cliente.data.entity.produto.TamanhoProduto;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class TamanhoProdutoRepository extends BaseRepository<TamanhoProduto> {

	public TamanhoProdutoRepository() {
		super(TamanhoProduto.class);
	}

	public List<TamanhoProduto> consulta(TamanhoProduto entity) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<TamanhoProduto> query = builder.createQuery(TamanhoProduto.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<TamanhoProduto> table = query.from(TamanhoProduto.class);

		if (StringUtils.isNotEmpty(entity.getNome())) {
			predicateList.add(builder.and(builder.like(builder.upper(table.<String>get("nome")), "%" + entity.getNome().toUpperCase() + "%")));
		}

		if (StringUtils.isNotEmpty(entity.getStatus())) {
			predicateList.add(builder.and(builder.equal(table.get("status"), entity.getStatus())));
		}

		query.orderBy(builder.asc(table.get("ordem")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<TamanhoProduto> typedQuery = getEntityManager().createQuery(query);

		return (List<TamanhoProduto>) typedQuery.getResultList();
	}
}