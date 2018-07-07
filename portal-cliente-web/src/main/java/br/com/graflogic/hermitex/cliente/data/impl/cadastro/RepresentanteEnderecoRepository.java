package br.com.graflogic.hermitex.cliente.data.impl.cadastro;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.cadastro.RepresentanteEndereco;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class RepresentanteEnderecoRepository extends BaseRepository<RepresentanteEndereco> {

	public RepresentanteEnderecoRepository() {
		super(RepresentanteEndereco.class);
	}

	public List<RepresentanteEndereco> consultaPorRepresentante(Integer idRepresentante) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<RepresentanteEndereco> query = builder.createQuery(RepresentanteEndereco.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<RepresentanteEndereco> table = query.from(RepresentanteEndereco.class);

		predicateList.add(builder.and(builder.equal(table.get("id").get("idRepresentante"), idRepresentante)));

		query.orderBy(builder.asc(table.get("id").get("tipo")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<RepresentanteEndereco> typedQuery = getEntityManager().createQuery(query);

		return (List<RepresentanteEndereco>) typedQuery.getResultList();
	}
}