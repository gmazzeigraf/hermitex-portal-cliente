package br.com.graflogic.hermitex.cliente.data.impl.cotacao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.cotacao.CotacaoEndereco;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class CotacaoEnderecoRepository extends BaseRepository<CotacaoEndereco> {

	public CotacaoEnderecoRepository() {
		super(CotacaoEndereco.class);
	}

	public List<CotacaoEndereco> consultaPorCotacao(Long idCotacao) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CotacaoEndereco> query = builder.createQuery(CotacaoEndereco.class);
		List<Predicate> predicateList = new ArrayList<>();

		Root<CotacaoEndereco> table = query.from(CotacaoEndereco.class);

		predicateList.add(builder.and(builder.equal(table.get("id").get("idCotacao"), idCotacao)));

		query.orderBy(builder.asc(table.get("id").get("tipo")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<CotacaoEndereco> typedQuery = getEntityManager().createQuery(query);

		return typedQuery.getResultList();
	}
}