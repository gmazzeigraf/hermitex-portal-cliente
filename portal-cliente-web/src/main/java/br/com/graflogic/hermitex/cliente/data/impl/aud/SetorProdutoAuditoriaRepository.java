package br.com.graflogic.hermitex.cliente.data.impl.aud;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.aud.SetorProdutoAuditoria;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class SetorProdutoAuditoriaRepository extends BaseRepository<SetorProdutoAuditoria> {

	public SetorProdutoAuditoriaRepository() {
		super(SetorProdutoAuditoria.class);
	}
}