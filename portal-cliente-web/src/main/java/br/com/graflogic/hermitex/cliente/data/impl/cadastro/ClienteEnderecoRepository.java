package br.com.graflogic.hermitex.cliente.data.impl.cadastro;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.cadastro.ClienteEndereco;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class ClienteEnderecoRepository extends BaseRepository<ClienteEndereco> {

	public ClienteEnderecoRepository() {
		super(ClienteEndereco.class);
	}

	public List<ClienteEndereco> consultaPorCliente(Integer id) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<ClienteEndereco> query = builder.createQuery(ClienteEndereco.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<ClienteEndereco> table = query.from(ClienteEndereco.class);

		predicateList.add(builder.and(builder.equal(table.get("id").get("idCliente"), id)));

		query.orderBy(builder.asc(table.get("id").get("tipo")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<ClienteEndereco> typedQuery = getEntityManager().createQuery(query);

		return (List<ClienteEndereco>) typedQuery.getResultList();
	}
}