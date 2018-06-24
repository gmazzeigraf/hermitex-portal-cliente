package br.com.graflogic.hermitex.cliente.data.impl.aud;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.aud.UsuarioLogin;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class UsuarioLoginRepository extends BaseRepository<UsuarioLogin> {

	public UsuarioLoginRepository() {
		super(UsuarioLogin.class);
	}

	@SuppressWarnings("unchecked")
	public List<UsuarioLogin> consulta(UsuarioLogin filter) {
		String queryStr = "SELECT log.id, log.id_usuario, log.data, log.ip_origem, usu.cpf, usu.nome, usu.tipo"
				+ " FROM tb_usuario_login log INNER JOIN tb_usuario usu ON log.id_usuario = usu.id ";
		String where = "";
		List<Object> params = new ArrayList<Object>();

		if (null != filter.getDataDe()) {
			where = generateWhere(where, "log.data >= ?");
			params.add(filter.getDataDe());
		}

		if (null != filter.getDataAte()) {
			where = generateWhere(where, "log.data <= ?");
			params.add(filter.getDataAte());
		}

		if (StringUtils.isNotEmpty(filter.getCpfUsuario())) {
			where = generateWhere(where, "usu.cpf = ?");
			params.add(filter.getCpfUsuario());
		}

		queryStr += where;
		queryStr += " ORDER BY log.data DESC";

		Query query = getEntityManager().createNativeQuery(queryStr);

		for (int i = 0; i < params.size(); i++) {
			query.setParameter((i + 1), params.get(i));
		}

		List<UsuarioLogin> entities = new ArrayList<>();

		List<Object[]> rows = query.getResultList();
		for (Object[] row : rows) {
			UsuarioLogin entity = new UsuarioLogin();
			entity.setId((String) row[0]);
			entity.setIdUsuario((Integer) row[1]);
			entity.setData((Date) row[2]);
			entity.setIpOrigem((String) row[3]);
			entity.setCpfUsuario((String) row[4]);
			entity.setNomeUsuario((String) row[5]);
			entity.setTipoUsuario((String) row[6]);

			entities.add(entity);
		}

		return entities;
	}
}