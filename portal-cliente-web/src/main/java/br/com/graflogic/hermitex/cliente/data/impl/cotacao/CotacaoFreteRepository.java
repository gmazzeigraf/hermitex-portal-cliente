package br.com.graflogic.hermitex.cliente.data.impl.cotacao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.cotacao.CotacaoFrete;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class CotacaoFreteRepository extends BaseRepository<CotacaoFrete> {

	public CotacaoFreteRepository() {
		super(CotacaoFrete.class);
	}

	@SuppressWarnings("unchecked")
	public List<CotacaoFrete> consultaPorCotacao(Long idCotacao) {
		String queryStr = "SELECT fre.id, fre.id_cotacao, fre.id_embalagem, fre.peso_itens, fre.qt_itens, fre.valor, fre.prazo_dias, fre.cd_servico, emb.nome AS nome_embalagem"
				+ " FROM tb_cotacao_frete fre LEFT JOIN tb_embalagem emb ON fre.id_embalagem = emb.id";

		String where = "";

		List<Object> params = new ArrayList<>();

		where = generateWhere(where, "fre.id_cotacao = ?");
		params.add(idCotacao);

		queryStr += where;

		queryStr += " ORDER BY fre.id";

		Query query = getEntityManager().createNativeQuery(queryStr);

		for (int i = 0; i < params.size(); i++) {
			query.setParameter((i + 1), params.get(i));
		}

		List<Object[]> rows = query.getResultList();

		List<CotacaoFrete> entities = new ArrayList<>();

		for (Object[] row : rows) {
			CotacaoFrete entity = new CotacaoFrete();
			entity.setId(((BigInteger) row[0]).longValue());
			entity.setIdCotacao(((BigInteger) row[1]).longValue());
			entity.setIdEmbalagem((Integer) row[2]);
			entity.setPesoItens((BigDecimal) row[3]);
			entity.setQuantidadeItens(((Short) row[4]).intValue());
			entity.setValor((BigDecimal) row[5]);
			entity.setPrazoDias(((Short) row[6]).intValue());
			entity.setCodigoServico((String) row[7]);
			entity.setNomeEmbalagem((String) row[8]);

			entities.add(entity);
		}

		return entities;
	}
}