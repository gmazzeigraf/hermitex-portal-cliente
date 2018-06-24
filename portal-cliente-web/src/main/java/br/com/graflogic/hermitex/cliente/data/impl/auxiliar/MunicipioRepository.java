package br.com.graflogic.hermitex.cliente.data.impl.auxiliar;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.auxiliar.Municipio;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class MunicipioRepository extends BaseRepository<Municipio> {

	public MunicipioRepository() {
		super(Municipio.class);
	}

	public List<Municipio> consulta(Municipio entity) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Municipio> query = builder.createQuery(Municipio.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<Municipio> table = query.from(Municipio.class);

		if (StringUtils.isNotEmpty(entity.getSiglaEstado())) {
			predicateList.add(builder.and(builder.equal(table.get("siglaEstado"), entity.getSiglaEstado())));
		}

		if (StringUtils.isNotEmpty(entity.getNome())) {
			predicateList.add(builder.and(builder.like(builder.upper(table.<String>get("nome")), "%" + entity.getNome().toUpperCase() + "%")));
		}

		query.orderBy(builder.asc(table.get("nome")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<Municipio> typedQuery = getEntityManager().createQuery(query);

		return (List<Municipio>) typedQuery.getResultList();
	}

	public List<Municipio> consulta(String siglaEstado) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Municipio> query = builder.createQuery(Municipio.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<Municipio> table = query.from(Municipio.class);

		predicateList.add(builder.and(builder.equal(table.get("siglaEstado"), siglaEstado)));

		query.orderBy(builder.asc(table.get("nome")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<Municipio> typedQuery = getEntityManager().createQuery(query);

		return (List<Municipio>) typedQuery.getResultList();
	}

	public Municipio consultaPorNomeEstado(String nome, String siglaEstado) {
		Query query = getEntityManager().createNativeQuery(
				"SELECT mun.* FROM tb_municipio mun INNER JOIN tb_estado est ON mun.sg_estado = est.sigla WHERE UPPER(mun.nome) = ? AND est.sigla = ? ORDER BY id ASC LIMIT 1",
				Municipio.class);

		query.setParameter(1, nome);
		query.setParameter(2, siglaEstado);

		return (Municipio) query.getSingleResult();
	}
}