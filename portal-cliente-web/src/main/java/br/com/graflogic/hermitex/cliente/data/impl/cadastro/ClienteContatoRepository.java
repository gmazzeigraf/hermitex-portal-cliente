package br.com.graflogic.hermitex.cliente.data.impl.cadastro;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.cadastro.ClienteContato;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class ClienteContatoRepository extends BaseRepository<ClienteContato> {

	public ClienteContatoRepository() {
		super(ClienteContato.class);
	}

	public List<ClienteContato> consultaContatos(Integer id) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ClienteContato> query = builder.createQuery(ClienteContato.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<ClienteContato> table = query.from(ClienteContato.class);

		predicateList.add(builder.and(builder.equal(table.get("idCliente"), id)));

		query.orderBy(builder.asc(table.get("nome")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<ClienteContato> typedQuery = getEntityManager().createQuery(query);

		return (List<ClienteContato>) typedQuery.getResultList();
	}
}