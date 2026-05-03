/**
 * 
 */
package um.tesoreria.core.hexagonal.articulo.application.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ArticuloException;
import um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.entity.ArticuloEntity;
import um.tesoreria.core.model.view.ArticuloKey;
import um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.repository.JpaArticuloRepository;
import um.tesoreria.core.service.view.ArticuloKeyService;

/**
 * @author daniel
 *
 */
@Service
public class ArticuloService {

	@Autowired
	private JpaArticuloRepository repository;

	@Autowired
	private ArticuloKeyService articuloKeyService;

	public List<ArticuloEntity> findAll() {
		return repository.findAll();
	}

	public List<ArticuloKey> findByStrings(List<String> conditions) {
		return articuloKeyService.findAllByStrings(conditions);
	}

	public ArticuloEntity articuloNew() {
		ArticuloEntity articulo = repository.findTopByOrderByArticuloIdDesc()
				.orElse(new ArticuloEntity(0L, "", "", "", BigDecimal.ZERO, (byte) 0, 0L, null, "", (byte) 0, (byte) 0, null));
		return new ArticuloEntity(1L + articulo.getArticuloId(), "", "", "", BigDecimal.ZERO, (byte) 0, 0L, null, "",
				(byte) 0, (byte) 0, null);
	}

	public ArticuloEntity findByArticuloId(Long articuloId) {
		return repository.findByArticuloId(articuloId).orElseThrow(() -> new ArticuloException(articuloId));
	}

	public ArticuloEntity add(ArticuloEntity articulo) {
		return repository.save(articulo);
	}

	public ArticuloEntity update(ArticuloEntity newArticulo, Long articuloId) {
		return repository.findByArticuloId(articuloId).map(articulo -> {
			articulo = new ArticuloEntity(articuloId, newArticulo.getNombre(), newArticulo.getDescripcion(),
					newArticulo.getUnidad(), newArticulo.getPrecio(), newArticulo.getInventariable(),
					newArticulo.getStockMinimo(), newArticulo.getNumeroCuenta(), newArticulo.getTipo(),
					newArticulo.getDirecto(), newArticulo.getHabilitado(), null);
			return repository.save(articulo);
		}).orElseThrow(() -> new ArticuloException(articuloId));
	}

}
