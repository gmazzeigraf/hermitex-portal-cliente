package br.com.graflogic.hermitex.cliente.data.impl.produto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoImagem;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class ProdutoImagemRepository extends BaseRepository<ProdutoImagem> {

	public ProdutoImagemRepository() {
		super(ProdutoImagem.class);
	}

	public List<ProdutoImagem> consultaPorProduto(Integer idProduto) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ProdutoImagem> query = builder.createQuery(ProdutoImagem.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<ProdutoImagem> table = query.from(ProdutoImagem.class);

		predicateList.add(builder.and(builder.equal(table.get("idProduto"), idProduto)));

		query.orderBy(builder.desc(table.get("capa")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<ProdutoImagem> typedQuery = getEntityManager().createQuery(query);

		return (List<ProdutoImagem>) typedQuery.getResultList();
	}
}