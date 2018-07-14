package br.com.graflogic.hermitex.cliente.data.impl.pedido;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.dom.DomAuditoria.DomEventoAuditoriaSolicitacaoTroca;
import br.com.graflogic.hermitex.cliente.data.dom.DomGeral.DomBoolean;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.SolicitacaoTroca;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class SolicitacaoTrocaRepository extends BaseRepository<SolicitacaoTroca> {

	public SolicitacaoTrocaRepository() {
		super(SolicitacaoTroca.class);
	}

	@SuppressWarnings("unchecked")
	public List<SolicitacaoTroca> consulta(SolicitacaoTroca filter) {
		String queryStr = getQuery();
		String where = "";
		List<Object> params = new ArrayList<>();

		params.add(DomEventoAuditoriaSolicitacaoTroca.CADASTRO);
		params.add(DomBoolean.SIM);

		if (null != filter.getIdPedido() && 0 != filter.getIdPedido()) {
			where = generateWhere(where, "ped.id = ?");
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

		if (StringUtils.isNotEmpty(filter.getStatus())) {
			where = generateWhere(where, "soc.status = ?");
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

		queryStr += " ORDER BY soc.id DESC";

		Query query = getEntityManager().createNativeQuery(queryStr);

		for (int i = 0; i < params.size(); i++) {
			query.setParameter((i + 1), params.get(i));
		}

		List<Object[]> rows = query.getResultList();

		List<SolicitacaoTroca> entities = new ArrayList<>();

		for (Object[] row : rows) {
			entities.add(parse(row));
		}

		return entities;
	}

	public SolicitacaoTroca consultaPorId(Long id) {
		String queryStr = getQuery();
		String where = "";
		List<Object> params = new ArrayList<>();

		params.add(DomEventoAuditoriaSolicitacaoTroca.CADASTRO);
		params.add(DomBoolean.SIM);

		where = generateWhere(where, "soc.id = ?");
		params.add(id);

		queryStr += where;

		Query query = getEntityManager().createNativeQuery(queryStr);

		for (int i = 0; i < params.size(); i++) {
			query.setParameter((i + 1), params.get(i));
		}

		return parse((Object[]) query.getSingleResult());
	}

	private String getQuery() {
		return "SELECT soc.id, soc.id_pedido_item, soc.quantidade, soc.status, soc.versao, ite.id_pedido, aud.data,"
				+ " pro.codigo, pro.titulo, img.id AS id_imagem, ite.cd_tamanho"
				+ " FROM tb_solicitacao_troca soc INNER JOIN tb_solicitacao_troca_aud aud ON soc.id = aud.id_relacionado AND aud.cd_evento = ?"
				+ " INNER JOIN tb_pedido_item ite ON ite.id = soc.id_pedido_item INNER JOIN tb_produto pro ON ite.id_produto = pro.id"
				+ " INNER JOIN tb_produto_imagem img ON pro.id = img.id_produto AND img.in_capa = ? INNER JOIN tb_pedido ped ON ite.id_pedido = ped.id";
	}

	private SolicitacaoTroca parse(Object[] row) {
		SolicitacaoTroca entity = new SolicitacaoTroca();
		entity.setId(((BigInteger) row[0]).longValue());
		entity.setIdPedidoItem(((BigInteger) row[1]).longValue());
		entity.setQuantidade(((Short) row[2]).intValue());
		entity.setStatus(((Character) row[3]).toString());
		entity.setVersao(((BigInteger) row[4]).longValue());
		entity.setIdPedido(((BigInteger) row[5]).longValue());
		entity.setDataCadastro((Date) row[6]);
		entity.setCodigoProduto((String) row[7]);
		entity.setTituloProduto((String) row[8]);
		entity.setIdImagemCapaProduto((String) row[9]);
		entity.setCodigoTamanhoProduto((String) row[10]);

		return entity;
	}
}