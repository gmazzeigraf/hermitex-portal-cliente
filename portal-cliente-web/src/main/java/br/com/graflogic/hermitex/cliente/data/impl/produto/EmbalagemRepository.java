package br.com.graflogic.hermitex.cliente.data.impl.produto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.produto.Embalagem;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class EmbalagemRepository extends BaseRepository<Embalagem> {

	public EmbalagemRepository() {
		super(Embalagem.class);
	}

	public List<Embalagem> consulta(Embalagem entity) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Embalagem> query = builder.createQuery(Embalagem.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<Embalagem> table = query.from(Embalagem.class);

		if (StringUtils.isNotEmpty(entity.getNome())) {
			predicateList.add(builder.and(builder.like(builder.upper(table.<String>get("nome")), "%" + entity.getNome().toUpperCase() + "%")));
		}

		if (StringUtils.isNotEmpty(entity.getTipoProduto())) {
			predicateList.add(builder.and(builder.equal(table.get("tipoProduto"), entity.getTipoProduto())));
		}

		if (StringUtils.isNotEmpty(entity.getStatus())) {
			predicateList.add(builder.and(builder.equal(table.get("status"), entity.getStatus())));
		}

		query.orderBy(builder.asc(table.get("pesoMaximo")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<Embalagem> typedQuery = getEntityManager().createQuery(query);

		return (List<Embalagem>) typedQuery.getResultList();
	}

	public Embalagem consultaPorTipoProdutoPeso(String tipoProduto, BigDecimal peso) {
		Query query = getEntityManager().createNativeQuery(
				"SELECT * FROM tb_embalagem WHERE tp_produto = ? AND peso_maximo >= ? ORDER BY peso_maximo ASC LIMIT 1", Embalagem.class);

		query.setParameter(1, tipoProduto);
		query.setParameter(2, peso);

		return (Embalagem) query.getSingleResult();
	}

	public Embalagem consultaMaiorPesoPorTipoProduto(String tipoProduto) {
		Query query = getEntityManager().createNativeQuery("SELECT * FROM tb_embalagem WHERE tp_produto = ? ORDER BY peso_maximo DESC LIMIT 1",
				Embalagem.class);

		query.setParameter(1, tipoProduto);

		return (Embalagem) query.getSingleResult();
	}
	
	public Embalagem consultaPorTipoProdutoQuantidade(String tipoProduto, Integer quantidade) {
		Query query = getEntityManager().createNativeQuery(
				"SELECT * FROM tb_embalagem WHERE tp_produto = ? AND qt_maxima >= ? ORDER BY qt_maxima ASC LIMIT 1", Embalagem.class);

		query.setParameter(1, tipoProduto);
		query.setParameter(2, quantidade);

		return (Embalagem) query.getSingleResult();
	}

	public Embalagem consultaMaiorQuantidadePorTipoProduto(String tipoProduto) {
		Query query = getEntityManager().createNativeQuery("SELECT * FROM tb_embalagem WHERE tp_produto = ? ORDER BY qt_maxima DESC LIMIT 1",
				Embalagem.class);

		query.setParameter(1, tipoProduto);

		return (Embalagem) query.getSingleResult();
	}
}