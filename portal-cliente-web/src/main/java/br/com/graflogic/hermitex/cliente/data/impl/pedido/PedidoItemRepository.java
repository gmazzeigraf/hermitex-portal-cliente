package br.com.graflogic.hermitex.cliente.data.impl.pedido;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.dom.DomGeral.DomBoolean;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.PedidoItem;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class PedidoItemRepository extends BaseRepository<PedidoItem> {

	public PedidoItemRepository() {
		super(PedidoItem.class);
	}

	@SuppressWarnings("unchecked")
	public List<PedidoItem> consultaPorPedido(Long idPedido) {
		String queryStr = "SELECT ite.id, ite.id_pedido, ite.id_produto, ite.cd_tamanho, ite.quantidade, ite.vl_unitario, ite.vl_corrigido_tamanho, ite.vl_total, peso_total,"
				+ " pro.codigo, pro.titulo, img.id AS id_imagem"
				+ " FROM tb_pedido_item ite INNER JOIN tb_produto pro ON ite.id_produto = pro.id INNER JOIN tb_produto_imagem img ON pro.id = img.id_produto AND img.in_capa = ?"
				+ " INNER JOIN tb_tamanho_produto tam ON tam.codigo = ite.cd_tamanho";

		String where = "";

		List<Object> params = new ArrayList<>();

		params.add(DomBoolean.SIM);

		where = generateWhere(where, "ite.id_pedido = ?");
		params.add(idPedido);

		queryStr += where;

		queryStr += " ORDER BY pro.codigo, tam.ordem";

		Query query = getEntityManager().createNativeQuery(queryStr);

		for (int i = 0; i < params.size(); i++) {
			query.setParameter((i + 1), params.get(i));
		}

		List<Object[]> rows = query.getResultList();

		List<PedidoItem> entities = new ArrayList<>();

		for (Object[] row : rows) {
			PedidoItem entity = new PedidoItem();
			entity.setId(((BigInteger) row[0]).longValue());
			entity.setIdPedido(((BigInteger) row[1]).longValue());
			entity.setIdProduto((Integer) row[2]);
			entity.setCodigoTamanho((String) row[3]);
			entity.setQuantidade(((Short) row[4]).intValue());
			entity.setValorUnitario((BigDecimal) row[5]);
			entity.setValorCorrigidoTamanho((BigDecimal) row[6]);
			entity.setValorTotal((BigDecimal) row[7]);
			entity.setPesoTotal((BigDecimal) row[8]);
			entity.setCodigoProduto((String) row[9]);
			entity.setTituloProduto((String) row[10]);
			entity.setIdImagemCapaProduto((String) row[11]);

			entities.add(entity);
		}

		return entities;
	}
}