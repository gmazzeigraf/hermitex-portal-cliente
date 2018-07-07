package br.com.graflogic.hermitex.cliente.data.impl.acesso;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.acesso.PerfilUsuario;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.PerfilUsuarioCliente;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.PerfilUsuarioFilial;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.PerfilUsuarioRepresentante;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class PerfilUsuarioRepository extends BaseRepository<PerfilUsuario> {

	public PerfilUsuarioRepository() {
		super(PerfilUsuario.class);
	}

	@SuppressWarnings("unchecked")
	public List<PerfilUsuario> consulta(PerfilUsuario entity) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<? extends PerfilUsuario> query = builder.createQuery(entity.getClass());
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<? extends PerfilUsuario> table = query.from(entity.getClass());

		if (StringUtils.isNotEmpty(entity.getNome())) {
			predicateList.add(builder.and(builder.like(builder.upper(table.<String>get("nome")), "%" + entity.getNome().toUpperCase() + "%")));
		}

		if (StringUtils.isNotEmpty(entity.getTipoUsuario())) {
			predicateList.add(builder.and(builder.equal(table.get("tipoUsuario"), entity.getTipoUsuario())));
		}

		if (entity instanceof PerfilUsuarioCliente) {
			Integer idCliente = ((PerfilUsuarioCliente) entity).getIdCliente();

			if (null != idCliente && 0 != idCliente) {
				predicateList.add(builder.and(builder.equal(table.get("idCliente"), idCliente)));
			}

		} else if (entity instanceof PerfilUsuarioFilial) {
			Integer idFilial = ((PerfilUsuarioFilial) entity).getIdFilial();

			if (null != idFilial && 0 != idFilial) {
				predicateList.add(builder.and(builder.equal(table.get("idFilial"), idFilial)));
			}

		} else if (entity instanceof PerfilUsuarioRepresentante) {
			Integer idRepresentante = ((PerfilUsuarioRepresentante) entity).getIdRepresentante();

			if (null != idRepresentante && 0 != idRepresentante) {
				predicateList.add(builder.and(builder.equal(table.get("idRepresentante"), idRepresentante)));
			}
		}

		query.orderBy(builder.asc(table.get("nome")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<? extends PerfilUsuario> typedQuery = getEntityManager().createQuery(query);

		return (List<PerfilUsuario>) typedQuery.getResultList();
	}
}