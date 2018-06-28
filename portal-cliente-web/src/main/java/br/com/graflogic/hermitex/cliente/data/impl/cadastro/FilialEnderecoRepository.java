package br.com.graflogic.hermitex.cliente.data.impl.cadastro;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.cadastro.FilialEndereco;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class FilialEnderecoRepository extends BaseRepository<FilialEndereco> {

	public FilialEnderecoRepository() {
		super(FilialEndereco.class);
	}

	public List<FilialEndereco> consultaPorFilial(Integer idFilial) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<FilialEndereco> query = builder.createQuery(FilialEndereco.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<FilialEndereco> table = query.from(FilialEndereco.class);

		predicateList.add(builder.and(builder.equal(table.get("id").get("idFilial"), idFilial)));

		query.orderBy(builder.asc(table.get("id").get("tipo")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<FilialEndereco> typedQuery = getEntityManager().createQuery(query);

		return (List<FilialEndereco>) typedQuery.getResultList();
	}
}