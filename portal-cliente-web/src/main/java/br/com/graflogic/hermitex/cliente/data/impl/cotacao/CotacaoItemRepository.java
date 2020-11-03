package br.com.graflogic.hermitex.cliente.data.impl.cotacao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.dom.DomGeral.DomBoolean;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.CotacaoItem;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class CotacaoItemRepository extends BaseRepository<CotacaoItem> {

	public CotacaoItemRepository() {
		super(CotacaoItem.class);
	}

	@SuppressWarnings("unchecked")
	public List<CotacaoItem> consultaPorCotacao(Long idCotacao) {
		String queryStr = "SELECT ite.id, ite.id_cotacao, ite.id_produto, ite.cd_tamanho, ite.cd_cor, ite.quantidade, ite.vl_unitario, ite.vl_corrigido_tamanho, ite.vl_total, peso_total,"
				+ " pro.codigo, pro.sku, pro.titulo, img.id AS id_imagem, cor.nome AS nm_cor"
				+ " FROM tb_cotacao_item ite INNER JOIN tb_produto pro ON ite.id_produto = pro.id"
				+ " INNER JOIN tb_tamanho_produto tam ON tam.codigo = ite.cd_tamanho LEFT JOIN tb_cor_produto cor ON cor.codigo = ite.cd_cor"
				+ " INNER JOIN tb_produto_imagem img ON pro.id = img.id_produto AND img.in_capa = ? AND (CASE WHEN cor.codigo IS NOT NULL THEN img.cd_cor = cor.codigo ELSE true END)";

		String where = "";

		List<Object> params = new ArrayList<>();

		params.add(DomBoolean.SIM);

		where = generateWhere(where, "ite.id_cotacao = ?");
		params.add(idCotacao);

		queryStr += where;

		queryStr += " ORDER BY pro.codigo, tam.ordem";

		Query query = getEntityManager().createNativeQuery(queryStr);

		for (int i = 0; i < params.size(); i++) {
			query.setParameter((i + 1), params.get(i));
		}

		List<Object[]> rows = query.getResultList();

		List<CotacaoItem> entities = new ArrayList<>();

		for (Object[] row : rows) {
			CotacaoItem entity = new CotacaoItem();
			entity.setId(((BigInteger) row[0]).longValue());
			entity.setIdCotacao(((BigInteger) row[1]).longValue());
			entity.setIdProduto((Integer) row[2]);
			entity.setCodigoTamanho((String) row[3]);
			entity.setCodigoCor((String) row[4]);
			entity.setQuantidade(((Short) row[5]).intValue());
			entity.setValorUnitario((BigDecimal) row[6]);
			entity.setValorCorrigidoTamanho((BigDecimal) row[7]);
			entity.setValorTotal((BigDecimal) row[8]);
			entity.setPesoTotal((BigDecimal) row[9]);
			entity.setCodigoProduto((String) row[10]);
			entity.setSkuProduto((String) row[11]);
			entity.setTituloProduto((String) row[12]);
			entity.setIdImagemCapaProduto((String) row[13]);
			entity.setNomeCor((String) row[14]);

			entities.add(entity);
		}

		return entities;
	}
}