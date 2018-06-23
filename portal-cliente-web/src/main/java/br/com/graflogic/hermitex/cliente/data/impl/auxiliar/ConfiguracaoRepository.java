package br.com.graflogic.hermitex.cliente.data.impl.auxiliar;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class ConfiguracaoRepository extends BaseRepository<Object> {

	public ConfiguracaoRepository() {
		super(Object.class);
	}

	public String consulta(String codigo) {
		Query query = getEntityManager().createNativeQuery("SELECT valor FROM tb_configuracao WHERE codigo = '" + codigo + "'");

		return (String) query.getSingleResult();
	}
}