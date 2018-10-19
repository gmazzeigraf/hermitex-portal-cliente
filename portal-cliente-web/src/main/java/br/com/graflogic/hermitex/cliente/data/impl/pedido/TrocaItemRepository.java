package br.com.graflogic.hermitex.cliente.data.impl.pedido;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.dom.DomGeral.DomBoolean;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.TrocaItem;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class TrocaItemRepository extends BaseRepository<TrocaItem> {

	public TrocaItemRepository() {
		super(TrocaItem.class);
	}

	public List<TrocaItem> consultaPorTroca(Long idTroca) {
		String queryStr = getQuery();

		String where = "";

		List<Object> params = new ArrayList<>();

		params.add(DomBoolean.SIM);

		where = generateWhere(where, "ite.id_troca = ?");
		params.add(idTroca);

		queryStr += where;

		queryStr += " ORDER BY pro.codigo, tam.ordem";

		return query(queryStr, params);
	}

	public List<TrocaItem> consultaPorPedidoItem(Long idPedidoItem) {
		String queryStr = getQuery();

		String where = "";

		List<Object> params = new ArrayList<>();

		params.add(DomBoolean.SIM);

		where = generateWhere(where, "ite.id_pedido_item = ?");
		params.add(idPedidoItem);

		queryStr += where;

		return query(queryStr, params);
	}

	@SuppressWarnings("unchecked")
	private List<TrocaItem> query(String queryStr, List<Object> params) {
		Query query = getEntityManager().createNativeQuery(queryStr);

		for (int i = 0; i < params.size(); i++) {
			query.setParameter((i + 1), params.get(i));
		}

		List<Object[]> rows = query.getResultList();

		List<TrocaItem> entities = new ArrayList<>();

		for (Object[] row : rows) {
			entities.add(parseEntity(row));
		}

		return entities;
	}

	private String getQuery() {
		return "SELECT ite.id, ite.id_troca, ite.id_pedido_item, ite.id_produto, ite.quantidade, ite.cd_tamanho, ite.motivo, pei.quantidade AS quantidade_pedido, pei.cd_tamanho AS cd_tamanho_pedido,"
				+ " pro.codigo, pro.titulo, img.id AS id_imagem"
				+ " FROM tb_troca_item ite INNER JOIN tb_produto pro ON ite.id_produto = pro.id INNER JOIN tb_pedido_item pei ON pei.id = ite.id_pedido_item"
				+ " INNER JOIN tb_produto_imagem img ON pro.id = img.id_produto AND img.in_capa = ?"
				+ " LEFT JOIN tb_tamanho_produto tam ON tam.codigo = ite.cd_tamanho";
	}

	private TrocaItem parseEntity(Object[] row) {
		TrocaItem entity = new TrocaItem();
		entity.setId(((BigInteger) row[0]).longValue());
		entity.setIdTroca(((BigInteger) row[1]).longValue());
		entity.setIdPedidoItem(((BigInteger) row[2]).longValue());
		entity.setIdProduto((Integer) row[3]);
		entity.setQuantidade(((Short) row[4]).intValue());
		entity.setCodigoTamanho((String) row[5]);
		entity.setMotivo((String) row[6]);
		entity.setQuantidadePedido(((Short) row[7]).intValue());
		entity.setCodigoTamanhoPedido((String) row[8]);
		entity.setCodigoProduto((String) row[9]);
		entity.setTituloProduto((String) row[10]);
		entity.setIdImagemCapaProduto((String) row[11]);

		return entity;
	}
}