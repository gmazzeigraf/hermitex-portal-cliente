package br.com.graflogic.hermitex.cliente.data.impl.cadastro;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.cadastro.FilialContato;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class FilialContatoRepository extends BaseRepository<FilialContato> {

	public FilialContatoRepository() {
		super(FilialContato.class);
	}

	public List<FilialContato> consultaPorFilial(Integer id) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<FilialContato> query = builder.createQuery(FilialContato.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<FilialContato> table = query.from(FilialContato.class);

		predicateList.add(builder.and(builder.equal(table.get("idFilial"), id)));

		query.orderBy(builder.asc(table.get("nome")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<FilialContato> typedQuery = getEntityManager().createQuery(query);

		return (List<FilialContato>) typedQuery.getResultList();
	}
}