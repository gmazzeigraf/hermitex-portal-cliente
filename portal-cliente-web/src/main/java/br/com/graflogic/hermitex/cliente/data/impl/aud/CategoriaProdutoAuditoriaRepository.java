package br.com.graflogic.hermitex.cliente.data.impl.aud;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.aud.CategoriaProdutoAuditoria;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class CategoriaProdutoAuditoriaRepository extends BaseRepository<CategoriaProdutoAuditoria> {

	public CategoriaProdutoAuditoriaRepository() {
		super(CategoriaProdutoAuditoria.class);
	}
}