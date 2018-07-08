package br.com.graflogic.hermitex.cliente.data.impl.pedido;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.pedido.Pedido;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoSimple;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class PedidoRepository extends BaseRepository<Pedido> {

	public PedidoRepository() {
		super(Pedido.class);
	}

	@SuppressWarnings("unchecked")
	public List<PedidoSimple> consulta(PedidoSimple filter) {
		String queryStr = "SELECT ped.id, ped.vl_total, ped.cd_forma_pagamento, ped.status,"
				+ " (SELECT COUNT(id) FROM tb_pedido_item WHERE id_pedido = ped.id) AS quantidade_itens, aud.data"
				+ " FROM tb_pedido ped INNER JOIN tb_pedido_aud aud ON ped.id = aud.id_relacionado";
		String where = "";
		List<Object> params = new ArrayList<>();

		if (null != filter.getIdCliente() && 0 != filter.getIdCliente()) {
			where = generateWhere(where, "ped.id_cliente = ?");
			params.add(filter.getIdCliente());
		}

		if (null != filter.getIdFilial() && 0 != filter.getIdFilial()) {
			where = generateWhere(where, "ped.id_filial = ?");
			params.add(filter.getIdFilial());
		}

		if (StringUtils.isNotEmpty(filter.getStatus())) {
			where = generateWhere(where, "ped.status = ?");
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

		queryStr += " ORDER BY ped.id DESC";

		Query query = getEntityManager().createNativeQuery(queryStr);

		for (int i = 0; i < params.size(); i++) {
			query.setParameter((i + 1), params.get(i));
		}

		List<Object[]> rows = query.getResultList();

		List<PedidoSimple> entities = new ArrayList<>();

		for (Object[] row : rows) {
			PedidoSimple entity = new PedidoSimple();
			entity.setId(((BigInteger) row[0]).longValue());
			entity.setValorTotal((BigDecimal) row[1]);
			entity.setCodigoFormaPagamento(((Character) row[2]).toString());
			entity.setStatus(((Character) row[3]).toString());
			entity.setQuantidadeItens(((BigInteger) row[4]).intValue());
			entity.setDataCadastro((Date) row[5]);

			entities.add(entity);
		}

		return entities;
	}
}