package br.com.graflogic.hermitex.cliente.data.impl.acesso;

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

import br.com.graflogic.hermitex.cliente.data.entity.acesso.Usuario;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.UsuarioCliente;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.UsuarioFilial;
import br.com.graflogic.hermitex.cliente.data.entity.acesso.UsuarioRepresentante;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class UsuarioRepository extends BaseRepository<Usuario> {

	public UsuarioRepository() {
		super(Usuario.class);
	}

	@SuppressWarnings("unchecked")
	public List<Usuario> consulta(Usuario entity) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<? extends Usuario> query = builder.createQuery(entity.getClass());
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<? extends Usuario> table = query.from(entity.getClass());

		if (StringUtils.isNotEmpty(entity.getNome())) {
			predicateList.add(builder.and(builder.like(builder.upper(table.<String>get("nome")), "%" + entity.getNome().toUpperCase() + "%")));
		}

		if (null != entity.getIdPerfil() && 0 != entity.getIdPerfil()) {
			predicateList.add(builder.and(builder.equal(table.get("idPerfil"), entity.getIdPerfil())));
		}

		if (StringUtils.isNotEmpty(entity.getStatus())) {
			predicateList.add(builder.and(builder.equal(table.get("status"), entity.getStatus())));
		}

		if (entity instanceof UsuarioCliente) {
			Integer idCliente = ((UsuarioCliente) entity).getIdCliente();

			if (null != idCliente && 0 != idCliente) {
				predicateList.add(builder.and(builder.equal(table.get("idCliente"), idCliente)));
			}

		} else if (entity instanceof UsuarioFilial) {
			Integer idFilial = ((UsuarioFilial) entity).getIdFilial();

			if (null != idFilial && 0 != idFilial) {
				predicateList.add(builder.and(builder.equal(table.get("idFilial"), idFilial)));
			}
	
		} else if (entity instanceof UsuarioRepresentante) {
			Integer idRepresentante = ((UsuarioRepresentante) entity).getIdRepresentante();

			if (null != idRepresentante && 0 != idRepresentante) {
				predicateList.add(builder.and(builder.equal(table.get("idRepresentante"), idRepresentante)));
			}
		}

		query.orderBy(builder.asc(table.get("status")), builder.asc(table.get("nome")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<? extends Usuario> typedQuery = getEntityManager().createQuery(query);

		return (List<Usuario>) typedQuery.getResultList();
	}

	public Usuario consultaPorEmail(String email) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<? extends Usuario> query = builder.createQuery(Usuario.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<? extends Usuario> table = query.from(Usuario.class);

		predicateList.add(builder.and(builder.equal(table.get("email"), email)));

		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<? extends Usuario> typedQuery = getEntityManager().createQuery(query);

		return typedQuery.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<Usuario> consultaPorPermissao(String permissao) {
		Query query = getEntityManager().createNativeQuery(
				"SELECT usu.*, 0 AS clazz_ FROM tb_usuario usu INNER JOIN tb_perfil_usuario_permissao_acesso perm ON usu.id_perfil = perm.id_perfil"
						+ " WHERE perm.cd_permissao = ?",
				Usuario.class);

		query.setParameter(1, permissao);

		return query.getResultList();
	}
}