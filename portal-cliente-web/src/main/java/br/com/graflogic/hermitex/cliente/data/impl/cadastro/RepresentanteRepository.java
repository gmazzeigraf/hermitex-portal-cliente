package br.com.graflogic.hermitex.cliente.data.impl.cadastro;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Representante;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class RepresentanteRepository extends BaseRepository<Representante> {

	public RepresentanteRepository() {
		super(Representante.class);
	}

	public List<Representante> consulta(Representante entity) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Representante> query = builder.createQuery(Representante.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<Representante> table = query.from(Representante.class);

		if (StringUtils.isNotEmpty(entity.getRazaoSocial())) {
			predicateList.add(
					builder.and(builder.like(builder.upper(table.<String>get("razaoSocial")), "%" + entity.getRazaoSocial().toUpperCase() + "%")));
		}

		if (StringUtils.isNotEmpty(entity.getStatus())) {
			predicateList.add(builder.and(builder.equal(table.get("status"), entity.getStatus())));
		}

		query.orderBy(builder.asc(table.get("razaoSocial")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<Representante> typedQuery = getEntityManager().createQuery(query);

		return (List<Representante>) typedQuery.getResultList();
	}

	public Representante consultaPorCnpj(String cnpj) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Representante> query = builder.createQuery(Representante.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<Representante> table = query.from(Representante.class);

		predicateList.add(builder.and(builder.equal(table.get("cnpj"), cnpj)));

		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<Representante> typedQuery = getEntityManager().createQuery(query);

		return typedQuery.getSingleResult();
	}
}