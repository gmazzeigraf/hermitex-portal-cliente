package br.com.graflogic.hermitex.cliente.data.impl.produto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoCor;
import br.com.graflogic.hermitex.cliente.data.entity.produto.ProdutoCorPK;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class ProdutoCorRepository extends BaseRepository<ProdutoCor> {

	public ProdutoCorRepository() {
		super(ProdutoCor.class);
	}

	@SuppressWarnings("unchecked")
	public List<ProdutoCor> consultaPorProduto(Integer idProduto) {
		String queryStr = "SELECT pro_cor.id_produto, pro_cor.cd_cor, cor.nome"
				+ " FROM tb_produto_cor pro_cor INNER JOIN tb_cor_produto cor ON pro_cor.cd_cor = cor.codigo";

		String where = "";

		List<Object> params = new ArrayList<>();

		where = generateWhere(where, "pro_cor.id_produto = ?");
		params.add(idProduto);

		queryStr += where;

		queryStr += " ORDER BY cor.ordem";

		Query query = getEntityManager().createNativeQuery(queryStr);

		for (int i = 0; i < params.size(); i++) {
			query.setParameter((i + 1), params.get(i));
		}

		List<Object[]> rows = query.getResultList();

		List<ProdutoCor> entities = new ArrayList<>();

		for (Object[] row : rows) {
			ProdutoCor entity = new ProdutoCor();
			entity.setId(new ProdutoCorPK());
			entity.getId().setIdProduto((Integer) row[0]);
			entity.getId().setCodigoCor((String) row[1]);
			entity.setNomeCor((String) row[2]);

			entities.add(entity);
		}

		return entities;
	}
}