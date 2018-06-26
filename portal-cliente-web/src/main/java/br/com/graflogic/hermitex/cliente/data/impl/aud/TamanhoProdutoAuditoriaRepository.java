package br.com.graflogic.hermitex.cliente.data.impl.aud;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.aud.TamanhoProdutoAuditoria;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class TamanhoProdutoAuditoriaRepository extends BaseRepository<TamanhoProdutoAuditoria> {

	public TamanhoProdutoAuditoriaRepository() {
		super(TamanhoProdutoAuditoria.class);
	}
}