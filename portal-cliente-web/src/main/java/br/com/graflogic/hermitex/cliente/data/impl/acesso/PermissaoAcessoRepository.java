package br.com.graflogic.hermitex.cliente.data.impl.acesso;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.acesso.PermissaoAcesso;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class PermissaoAcessoRepository extends BaseRepository<PermissaoAcesso> {

	public PermissaoAcessoRepository() {
		super(PermissaoAcesso.class);
	}

	public List<PermissaoAcesso> consulta(String tipoUsuario) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<PermissaoAcesso> query = builder.createQuery(PermissaoAcesso.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<PermissaoAcesso> table = query.from(PermissaoAcesso.class);

		predicateList.add(builder.and(builder.like(table.<String>get("tipoUsuario"), "%" + tipoUsuario + "%")));

		query.orderBy(builder.asc(table.get("descricao")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<PermissaoAcesso> typedQuery = getEntityManager().createQuery(query);

		return (List<PermissaoAcesso>) typedQuery.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<PermissaoAcesso> consultaPorPerfilUsuario(Integer idPerfilUsuario) {
		Query query = getEntityManager().createNativeQuery(
				"SELECT per.codigo, per.descricao, per.tp_usuario FROM tb_permissao_acesso per INNER JOIN tb_perfil_usuario_permissao_acesso xref ON per.codigo = xref.cd_permissao"
						+ " WHERE xref.id_perfil = ?",
				PermissaoAcesso.class);

		query.setParameter(1, idPerfilUsuario);

		return query.getResultList();
	}
}