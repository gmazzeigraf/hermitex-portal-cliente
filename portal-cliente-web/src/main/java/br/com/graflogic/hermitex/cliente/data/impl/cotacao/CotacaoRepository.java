package br.com.graflogic.hermitex.cliente.data.impl.cotacao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaCotacao;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.Cotacao;
import br.com.graflogic.hermitex.cliente.data.entity.cotacao.CotacaoSimple;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class CotacaoRepository extends BaseRepository<Cotacao> {

	public CotacaoRepository() {
		super(Cotacao.class);
	}

	@SuppressWarnings("unchecked")
	public List<CotacaoSimple> consulta(CotacaoSimple filter) {
		String queryStr = "SELECT cot.id, cot.id_filial, cot.vl_total, cot.qt_total_itens, cot.status,"
				+ " (SELECT COUNT(id) FROM tb_cotacao_item WHERE id_cotacao = cot.id) AS quantidade_itens, aud.data, aud.id_responsavel"
				+ " FROM tb_cotacao cot INNER JOIN tb_cotacao_aud aud ON cot.id = aud.id_relacionado AND aud.cd_evento = ?";
		String where = "";
		List<Object> params = new ArrayList<>();

		params.add(DomEventoAuditoriaCotacao.CADASTRO);

		if (null != filter.getIdCliente() && 0 != filter.getIdCliente()) {
			where = generateWhere(where, "cot.id_cliente = ?");
			params.add(filter.getIdCliente());
		}

		if (null != filter.getIdFilial() && 0 != filter.getIdFilial()) {
			where = generateWhere(where, "cot.id_filial = ?");
			params.add(filter.getIdFilial());
		}

		if (StringUtils.isNotEmpty(filter.getStatus())) {
			where = generateWhere(where, "cot.status = ?");
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

		queryStr += " ORDER BY cot.id DESC";

		Query query = getEntityManager().createNativeQuery(queryStr);

		for (int i = 0; i < params.size(); i++) {
			query.setParameter((i + 1), params.get(i));
		}

		List<Object[]> rows = query.getResultList();

		List<CotacaoSimple> entities = new ArrayList<>();

		for (Object[] row : rows) {
			CotacaoSimple entity = new CotacaoSimple();
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