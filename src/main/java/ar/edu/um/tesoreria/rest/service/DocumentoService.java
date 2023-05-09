/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.Documento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.DocumentoException;
import ar.edu.um.tesoreria.rest.repository.IDocumentoRepository;

/**
 * @author daniel
 *
 */
@Service
public class DocumentoService {

	@Autowired
	private IDocumentoRepository repository;

	public List<Documento> findAll() {
		return repository.findAll();
	}

	public Documento findByDocumentoId(Integer documentoId) {
		return repository.findByDocumentoId(documentoId).orElseThrow(() -> new DocumentoException(documentoId));
	}

}
