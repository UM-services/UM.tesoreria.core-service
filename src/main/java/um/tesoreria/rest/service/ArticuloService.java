/**
 * 
 */
package um.tesoreria.rest.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.exception.ArticuloException;
import um.tesoreria.rest.kotlin.model.Articulo;
import um.tesoreria.rest.model.view.ArticuloKey;
import um.tesoreria.rest.repository.IArticuloRepository;
import um.tesoreria.rest.service.view.ArticuloKeyService;

/**
 * @author daniel
 *
 */
@Service
public class ArticuloService {

	@Autowired
	private IArticuloRepository repository;

	@Autowired
	private ArticuloKeyService articuloKeyService;

	public List<Articulo> findAll() {
		return repository.findAll();
	}

	public List<ArticuloKey> findByStrings(List<String> conditions) {
		return articuloKeyService.findAllByStrings(conditions);
	}

	public Articulo articuloNew() {
		Articulo articulo = repository.findTopByOrderByArticuloIdDesc()
				.orElse(new Articulo(0L, "", "", "", BigDecimal.ZERO, (byte) 0, 0L, null, "", (byte) 0, (byte) 0, null));
		return new Articulo(1L + articulo.getArticuloId(), "", "", "", BigDecimal.ZERO, (byte) 0, 0L, null, "",
				(byte) 0, (byte) 0, null);
	}

	public Articulo findByArticuloId(Long articuloId) {
		return repository.findByArticuloId(articuloId).orElseThrow(() -> new ArticuloException(articuloId));
	}

	public Articulo add(Articulo articulo) {
		return repository.save(articulo);
	}

	public Articulo update(Articulo newArticulo, Long articuloId) {
		return repository.findByArticuloId(articuloId).map(articulo -> {
			articulo = new Articulo(articuloId, newArticulo.getNombre(), newArticulo.getDescripcion(),
					newArticulo.getUnidad(), newArticulo.getPrecio(), newArticulo.getInventariable(),
					newArticulo.getStockMinimo(), newArticulo.getNumeroCuenta(), newArticulo.getTipo(),
					newArticulo.getDirecto(), newArticulo.getHabilitado(), null);
			return repository.save(articulo);
		}).orElseThrow(() -> new ArticuloException(articuloId));
	}

}
