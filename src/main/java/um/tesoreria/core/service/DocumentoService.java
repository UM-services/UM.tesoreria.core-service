/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.DocumentoException;
import um.tesoreria.core.kotlin.model.Documento;
import um.tesoreria.core.repository.DocumentoRepository;

/**
 * @author daniel
 *
 */
@Service
public class DocumentoService {

	private final DocumentoRepository repository;

	public DocumentoService(DocumentoRepository repository) {
		this.repository = repository;
	}

	public List<Documento> findAll() {
		return repository.findAll();
	}

	public Documento findByDocumentoId(Integer documentoId) {
		return repository.findByDocumentoId(documentoId).orElseThrow(() -> new DocumentoException(documentoId));
	}

}
