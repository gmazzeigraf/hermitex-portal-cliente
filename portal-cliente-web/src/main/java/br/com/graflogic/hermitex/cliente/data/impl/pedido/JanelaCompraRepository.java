package br.com.graflogic.hermitex.cliente.data.impl.pedido;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.dom.DomPedido.DomStatusJanelaCompra;
import br.com.graflogic.hermitex.cliente.data.entity.pedido.JanelaCompra;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class JanelaCompraRepository extends BaseRepository<JanelaCompra> {

	public JanelaCompraRepository() {
		super(JanelaCompra.class);
	}

	public List<JanelaCompra> consulta(JanelaCompra entity) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<JanelaCompra> query = builder.createQuery(JanelaCompra.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<JanelaCompra> table = query.from(JanelaCompra.class);

		if (null != entity.getIdCliente() && 0 != entity.getIdCliente()) {
			predicateList.add(builder.and(builder.equal(table.get("idCliente"), entity.getIdCliente())));
		}

		if (StringUtils.isNotEmpty(entity.getStatus())) {
			predicateList.add(builder.and(builder.equal(table.get("status"), entity.getStatus())));
		}

		query.orderBy(builder.desc(table.get("id")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<JanelaCompra> typedQuery = getEntityManager().createQuery(query);

		return (List<JanelaCompra>) typedQuery.getResultList();
	}

	public boolean isJanelaComprasDisponivel(Integer idCliente, Date dataAbertura, Date dataFechamento) {
		Query query = getEntityManager().createNativeQuery("SELECT (COUNT(id) = 0) FROM tb_janela_compra"
				+ " WHERE id_cliente = ? AND (CAST(? AS DATE) BETWEEN dt_abertura AND dt_fechamento OR CAST(? AS DATE) BETWEEN dt_abertura AND dt_fechamento)"
				+ " AND status IN (?, ?)");

		query.setParameter(1, idCliente);
		query.setParameter(2, dataAbertura);
		query.setParameter(3, dataFechamento);
		query.setParameter(4, DomStatusJanelaCompra.CADASTRADA);
		query.setParameter(5, DomStatusJanelaCompra.FECHADA);

		boolean disponivel = (boolean) query.getSingleResult();

		return disponivel;
	}

	public JanelaCompra consultaPorClienteData(Integer idCliente, Date data) {
		Query query = getEntityManager().createNativeQuery(
				"SELECT * FROM tb_janela_compra WHERE id_cliente = ? AND CAST(? AS DATE) BETWEEN dt_abertura AND dt_fechamento", JanelaCompra.class);

		query.setParameter(1, idCliente);
		query.setParameter(2, data);

		return (JanelaCompra) query.getSingleResult();
	}
}