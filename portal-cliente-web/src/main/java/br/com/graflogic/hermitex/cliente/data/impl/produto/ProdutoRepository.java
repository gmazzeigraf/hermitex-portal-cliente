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

import br.com.graflogic.hermitex.cliente.data.entity.produto.Produto;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class ProdutoRepository extends BaseRepository<Produto> {

	public ProdutoRepository() {
		super(Produto.class);
	}

	public List<Produto> consulta(Produto entity) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Produto> query = builder.createQuery(Produto.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<Produto> table = query.from(Produto.class);

		if (StringUtils.isNotEmpty(entity.getTitulo())) {
			predicateList.add(builder.and(builder.like(builder.upper(table.<String>get("titulo")), "%" + entity.getTitulo().toUpperCase() + "%")));
		}

		if (null != entity.getIdCliente() && 0 != entity.getIdCliente()) {
			predicateList.add(builder.and(builder.equal(table.get("idCliente"), entity.getIdCliente())));
		}

		if (null != entity.getIdCategoria() && 0 != entity.getIdCategoria()) {
			predicateList.add(builder.and(builder.equal(table.get("idCategoria"), entity.getIdCategoria())));
		}

		if (null != entity.getIdSetor() && 0 != entity.getIdSetor()) {
			predicateList.add(builder.and(builder.equal(table.get("idSetor"), entity.getIdSetor())));
		}

		if (StringUtils.isNotEmpty(entity.getGenero())) {
			predicateList.add(builder.and(builder.equal(table.get("genero"), entity.getGenero())));
		}

		if (StringUtils.isNotEmpty(entity.getStatus())) {
			predicateList.add(builder.and(builder.equal(table.get("status"), entity.getStatus())));
		}

		query.orderBy(builder.asc(table.get("titulo")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<Produto> typedQuery = getEntityManager().createQuery(query);

		return (List<Produto>) typedQuery.getResultList();
	}
}