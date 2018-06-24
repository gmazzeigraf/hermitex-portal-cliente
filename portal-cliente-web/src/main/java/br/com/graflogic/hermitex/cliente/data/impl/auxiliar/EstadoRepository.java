package br.com.graflogic.hermitex.cliente.data.impl.auxiliar;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Estado;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class EstadoRepository extends BaseRepository<Estado> {

	public EstadoRepository() {
		super(Estado.class);
	}

	public List<Estado> consulta() {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Estado> query = builder.createQuery(Estado.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<Estado> table = query.from(Estado.class);

		query.orderBy(builder.asc(table.get("nome")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<Estado> typedQuery = getEntityManager().createQuery(query);

		return (List<Estado>) typedQuery.getResultList();
	}
}