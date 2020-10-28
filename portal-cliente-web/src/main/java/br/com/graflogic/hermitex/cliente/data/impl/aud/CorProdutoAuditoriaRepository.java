package br.com.graflogic.hermitex.cliente.data.impl.aud;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.aud.CorProdutoAuditoria;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class CorProdutoAuditoriaRepository extends BaseRepository<CorProdutoAuditoria> {

	public CorProdutoAuditoriaRepository() {
		super(CorProdutoAuditoria.class);
	}
}