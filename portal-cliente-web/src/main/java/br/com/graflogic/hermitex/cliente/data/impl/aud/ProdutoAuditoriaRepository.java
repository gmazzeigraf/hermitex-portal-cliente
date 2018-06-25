package br.com.graflogic.hermitex.cliente.data.impl.aud;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.aud.ProdutoAuditoria;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class ProdutoAuditoriaRepository extends BaseRepository<ProdutoAuditoria> {

	public ProdutoAuditoriaRepository() {
		super(ProdutoAuditoria.class);
	}
}