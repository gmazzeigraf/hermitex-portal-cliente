package br.com.graflogic.hermitex.cliente.data.impl.pedido;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaTroca;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.Troca;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class TrocaRepository extends BaseRepository<Troca> {

	public TrocaRepository() {
		super(Troca.class);
	}

	@SuppressWarnings("unchecked")
	public List<Troca> consulta(Troca filter) {
		String queryStr = getQuery();
		String where = "";
		List<Object> params = new ArrayList<>();

		params.add(DomEventoAuditoriaTroca.CADASTRO);

		if (null != filter.getIdPedido() && 0 != filter.getIdPedido()) {
			where = generateWhere(where, "tro.id_pedido = ?");
			params.add(filter.getIdPedido());
		}

		if (null != filter.getIdCliente() && 0 != filter.getIdCliente()) {
			where = generateWhere(where, "ped.id_cliente = ?");
			params.add(filter.getIdCliente());
		}

		if (null != filter.getIdFilial() && 0 != filter.getIdFilial()) {
			where = generateWhere(where, "ped.id_filial = ?");
			params.add(filter.getIdFilial());
		}

		if (StringUtils.isNotEmpty(filter.getTipo())) {
			where = generateWhere(where, "tro.tipo = ?");
			params.add(filter.getTipo());
		}

		if (StringUtils.isNotEmpty(filter.getStatus())) {
			where = generateWhere(where, "tro.status = ?");
			params.add(filter.getStatus());
		}

		if (null != filter.getDataCadastroDe()) {
			where = generateWhere(where, "aud.data >= ?");
			params.add(filter.getDataCadastroDe());
		}

		if (null != filter.getDataCadastroAte()) {
			where = generateWhere(where, "aud.data <= ?");
			params.add(filter.getDataCadastroAte());
		}

		queryStr += where;

		queryStr += " ORDER BY tro.id DESC";

		Query query = getEntityManager().createNativeQuery(queryStr);

		for (int i = 0; i < params.size(); i++) {
			query.setParameter((i + 1), params.get(i));
		}

		List<Object[]> rows = query.getResultList();

		List<Troca> entities = new ArrayList<>();

		for (Object[] row : rows) {
			entities.add(parse(row));
		}

		return entities;
	}

	public Troca consultaPorId(Long id) {
		String queryStr = getQuery();
		String where = "";
		List<Object> params = new ArrayList<>();

		params.add(DomEventoAuditoriaTroca.CADASTRO);

		where = generateWhere(where, "tro.id = ?");
		params.add(id);

		queryStr += where;

		Query query = getEntityManager().createNativeQuery(queryStr);

		for (int i = 0; i < params.size(); i++) {
			query.setParameter((i + 1), params.get(i));
		}

		return parse((Object[]) query.getSingleResult());
	}

	private String getQuery() {
		return "SELECT tro.id, tro.id_pedido, ped.id_filial, tro.tipo, tro.motivo, tro.status, tro.versao, aud.data, (SELECT COUNT(id) FROM tb_troca_item WHERE id_troca = tro.id)"
				+ " FROM tb_troca tro INNER JOIN tb_troca_aud aud ON tro.id = aud.id_relacionado AND aud.cd_evento = ?"
				+ " INNER JOIN tb_pedido ped ON ped.id = tro.id_pedido";
	}

	private Troca parse(Object[] row) {
		Troca entity = new Troca();
		entity.setId(((BigInteger) row[0]).longValue());
		entity.setIdPedido(((BigInteger) row[1]).longValue());
		entity.setIdFilial((Integer) row[2]);
		entity.setTipo(((Character) row[3]).toString());
		entity.setMotivo((String) row[4]);
		entity.setStatus(((Character) row[5]).toString());
		entity.setVersao(((BigInteger) row[6]).longValue());
		entity.setDataCadastro((Date) row[7]);
		entity.setQuantidadeItens(((BigInteger) row[8]).intValue());

		return entity;
	}
}