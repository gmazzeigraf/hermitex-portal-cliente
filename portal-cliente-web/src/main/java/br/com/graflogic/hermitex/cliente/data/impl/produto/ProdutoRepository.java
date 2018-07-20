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

import br.com.graflogic.hermitex.cliente.data.dom.DomGeral.DomBoolean;
import br.com.graflogic.hermitex.cliente.data.dom.DomProduto.DomStatus;
import br.com.graflogic.hermitex.cliente.data.entity.produto.Produto;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoApresentacaoLista;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class ProdutoRepository extends BaseRepository<Produto> {

	public ProdutoRepository() {
		super(Produto.class);
	}

	public List<Produto> consulta(Produto entity) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Produto> query = builder.createQuery(Produto.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<Produto> table = query.from(Produto.class);

		if (StringUtils.isNotEmpty(entity.getTitulo())) {
			predicateList.add(builder.and(builder.like(builder.upper(table.<String>get("titulo")), "%" + entity.getTitulo().toUpperCase() + "%")));
		}

		if (null != entity.getIdCliente() && 0 != entity.getIdCliente()) {
			predicateList.add(builder.and(builder.equal(table.get("idCliente"), entity.getIdCliente())));
		}

		if (StringUtils.isNotEmpty(entity.getTipo())) {
			predicateList.add(builder.and(builder.equal(table.get("tipo"), entity.getTipo())));
		}

		if (null != entity.getIdCategoria() && 0 != entity.getIdCategoria()) {
			predicateList.add(builder.and(builder.equal(table.get("idCategoria"), entity.getIdCategoria())));
		}

		if (null != entity.getIdSetor() && 0 != entity.getIdSetor()) {
			predicateList.add(builder.and(builder.equal(table.get("idSetor"), entity.getIdSetor())));
		}

		if (StringUtils.isNotEmpty(entity.getGenero())) {
			predicateList.add(builder.and(builder.equal(table.get("genero"), entity.getGenero())));
		}

		if (StringUtils.isNotEmpty(entity.getStatus())) {
			predicateList.add(builder.and(builder.equal(table.get("status"), entity.getStatus())));
		}

		query.orderBy(builder.asc(table.get("titulo")));
		query.where(predicateList.toArray(new Predicate[predicateList.size()]));
		TypedQuery<Produto> typedQuery = getEntityManager().createQuery(query);

		return (List<Produto>) typedQuery.getResultList();
	}

	public Produto consultaPorClienteCodigo(Integer idCliente, String codigo) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Produto> query = builder.createQuery(Produto.class);
		List<Predicate> predicateList = new ArrayList<Predicate>();

		Root<Produto> table = query.from(Produto.class);

		predicateList.add(builder.and(builder.equal(table.get("idCliente"), idCliente)));
		predicateList.add(builder.and(builder.equal(table.get("codigo"), codigo)));

		query.where(predicateList.toArray(new Predicate[predicateList.size()]));

		TypedQuery<Produto> typedQuery = getEntityManager().createQuery(query);

		return typedQuery.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<ProdutoApresentacaoLista> consultaApresentacaoLista(ProdutoApresentacaoLista filter) {
		String queryStr = "SELECT pro.id, pro.codigo, pro.titulo, pro.valor, img.id AS id_imagem"
				+ " FROM tb_produto pro INNER JOIN tb_produto_imagem img ON pro.id = img.id_produto";

		String where = "";

		List<Object> params = new ArrayList<>();

		where = generateWhere(where, "img.in_capa = ?");
		params.add(DomBoolean.SIM);

		where = generateWhere(where, "pro.status = ?");
		params.add(DomStatus.ATIVO);

		where = generateWhere(where, "pro.id_cliente = ?");
		params.add(filter.getIdCliente());

		if (null != filter.getIdCategoria() && 0 != filter.getIdCategoria()) {
			where = generateWhere(where, "pro.id_categoria = ?");
			params.add(filter.getIdCategoria());
		}

		if (null != filter.getIdSetor() && 0 != filter.getIdSetor()) {
			where = generateWhere(where, "pro.id_setor = ?");
			params.add(filter.getIdSetor());
		}

		if (StringUtils.isNotEmpty(filter.getTitulo())) {
			where = generateWhere(where, "UPPER(pro.titulo) LIKE ?");
			params.add("%" + filter.getTitulo().toUpperCase() + "%");
		}

		if (StringUtils.isNotEmpty(filter.getGenero())) {
			where = generateWhere(where, "pro.genero = ?");
			params.add(filter.getGenero());
		}

		if (StringUtils.isNotEmpty(filter.getCodigoTamanho())) {
			where = generateWhere(where, "pro.id IN (SELECT id_produto FROM tb_produto_tamanho WHERE id_produto = pro.id AND cd_tamanho = ?)");
			params.add(filter.getCodigoTamanho());
		}

		queryStr += where;

		queryStr += " ORDER BY pro.genero, pro.id_categoria";

		Query query = getEntityManager().createNativeQuery(queryStr);

		for (int i = 0; i < params.size(); i++) {
			query.setParameter((i + 1), params.get(i));
		}

		List<Object[]> rows = query.getResultList();

		List<ProdutoApresentacaoLista> entities = new ArrayList<>();

		for (Object[] row : rows) {
			ProdutoApresentacaoLista entity = new ProdutoApresentacaoLista();
			entity.setId((Integer) row[0]);
			entity.setCodigo((String) row[1]);
			entity.setTitulo((String) row[2]);
			entity.setValor((BigDecimal) row[3]);
			entity.setIdImagemCapa((String) row[4]);

			entities.add(entity);
		}

		return entities;
	}
}