package br.com.graflogic.hermitex.cliente.data.impl.cadastro;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.cadastro.RepresentanteContato;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class RepresentanteContatoRepository extends BaseRepository<RepresentanteContato> {

	public RepresentanteContatoRepository() {
		super(RepresentanteContato.class);
	}

	public List<RepresentanteContato> consultaPorRepresentante(Integer idRepresentante) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<RepresentanteContato> query = builder.createQuery(RepresentanteContato.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<RepresentanteContato> table = query.from(RepresentanteContato.class);

		predicateList.add(builder.and(builder.equal(table.get("idRepresentante"), idRepresentante)));

		query.orderBy(builder.asc(table.get("nome")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<RepresentanteContato> typedQuery = getEntityManager().createQuery(query);

		return (List<RepresentanteContato>) typedQuery.getResultList();
	}
}