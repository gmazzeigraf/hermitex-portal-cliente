package br.com.graflogic.hermitex.cliente.data.impl.aud;

import org.springframework.stereotype.Repository;

import br.com.graflogic.hermitex.cliente.data.entity.aud.FilialAuditoria;
import br.com.graflogic.utilities.datautil.repository.BaseRepository;

/**
 * 
 * @author gmazz
 *
 */
@Repository
public class FilialAuditoriaRepository extends BaseRepository<FilialAuditoria> {

	public FilialAuditoriaRepository() {
		super(FilialAuditoria.class);
	}
}