package br.com.graflogic.hermitex.cliente.data.impl.produto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoTamanho;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class ProdutoTamanhoRepository extends BaseRepository<ProdutoTamanho> {

	public ProdutoTamanhoRepository() {
		super(ProdutoTamanho.class);
	}

	public List<ProdutoTamanho> consultaPorProduto(Integer idProduto) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ProdutoTamanho> query = builder.createQuery(ProdutoTamanho.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<ProdutoTamanho> table = query.from(ProdutoTamanho.class);

		predicateList.add(builder.and(builder.equal(table.get("id").get("idProduto"), idProduto)));

		query.orderBy(builder.asc(table.get("id").get("codigoTamanho")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<ProdutoTamanho> typedQuery = getEntityManager().createQuery(query);

		return (List<ProdutoTamanho>) typedQuery.getResultList();
	}
}