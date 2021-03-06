package br.com.graflogic.hermitex.cliente.data.impl.pedido;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaPedido;
import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomStatus;
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
		String queryStr = "SELECT ped.id, ped.id_filial, ped.vl_total, ped.qt_total_itens, ped.status,"
				+ " (SELECT COUNT(id) FROM tb_pedido_item WHERE id_pedido = ped.id) AS quantidade_itens, aud.data, aud.id_responsavel"
				+ " FROM tb_pedido ped INNER JOIN tb_pedido_aud aud ON ped.id = aud.id_relacionado AND aud.cd_evento = ?";
		String where = "";
		List<Object> params = new ArrayList<>();

		params.add(DomEventoAuditoriaPedido.CADASTRO);

		if (null != filter.getIdCliente() && 0 != filter.getIdCliente()) {
			where = generateWhere(where, "ped.id_cliente = ?");
			params.add(filter.getIdCliente());
		}

		if (null != filter.getIdFilial() && 0 != filter.getIdFilial()) {
			where = generateWhere(where, "ped.id_filial = ?");
			params.add(filter.getIdFilial());
		}

		if (null != filter.getIdJanelaCompra() && 0 != filter.getIdJanelaCompra()) {
			where = generateWhere(where, "ped.id_janela_compra = ?");
			params.add(filter.getIdJanelaCompra());
		}

		if (StringUtils.isNotEmpty(filter.getStatus())) {
			where = generateWhere(where, "ped.status = ?");
			params.add(filter.getStatus());
		} else {
			where = generateWhere(where, "ped.status != ?");
			params.add(DomStatus.CANCELADO);
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
			entity.setIdFilial((Integer) row[1]);
			entity.setValorTotal((BigDecimal) row[2]);
			entity.setQuantidadeTotalItens(((Short) row[3]).intValue());
			entity.setStatus(((Character) row[4]).toString());
			entity.setQuantidadeItens(((BigInteger) row[5]).intValue());
			entity.setDataCadastro((Date) row[6]);
			entity.setIdUsuarioCadastro((Integer) row[7]);

			entities.add(entity);
		}

		return entities;
	}
}