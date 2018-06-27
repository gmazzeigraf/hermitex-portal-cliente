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

import br.com.graflogic.hermitex.cliente.data.entity.cadastro.Filial;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class FilialRepository extends BaseRepository<Filial> {

	public FilialRepository() {
		super(Filial.class);
	}

	public List<Filial> consulta(Filial entity) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Filial> query = builder.createQuery(Filial.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<Filial> table = query.from(Filial.class);

		if (StringUtils.isNotEmpty(entity.getRazaoSocial())) {
			predicateList.add(
					builder.and(builder.like(builder.upper(table.<String>get("razaoSocial")), "%" + entity.getRazaoSocial().toUpperCase() + "%")));
		}

		if (StringUtils.isNotEmpty(entity.getStatus())) {
			predicateList.add(builder.and(builder.equal(table.get("status"), entity.getStatus())));
		}

		if (null != entity.getIdCliente() && 0 != entity.getIdCliente()) {
			predicateList.add(builder.and(builder.equal(table.get("idCliente"), entity.getIdCliente())));
		}

		query.orderBy(builder.asc(table.get("razaoSocial")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<Filial> typedQuery = getEntityManager().createQuery(query);

		return (List<Filial>) typedQuery.getResultList();
	}

	public Filial consultaPorCnpj(String cnpj) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Filial> query = builder.createQuery(Filial.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<Filial> table = query.from(Filial.class);

		predicateList.add(builder.and(builder.equal(table.get("cnpj"), cnpj)));

		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<Filial> typedQuery = getEntityManager().createQuery(query);

		return typedQuery.getSingleResult();
	}
}