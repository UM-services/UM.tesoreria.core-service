/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.CursoCargoContratadoException;
import um.tesoreria.core.model.CursoCargoContratado;
import um.tesoreria.core.repository.ICursoCargoContratadoRepository;

/**
 * @author daniel
 *
 */
@Service
public class CursoCargoContratadoService {

	@Autowired
	private ICursoCargoContratadoRepository repository;

	public List<CursoCargoContratado> findAllByContratado(Long contratadoId, Integer anho, Integer mes,
			Long contratoId) {
		return repository.findAllByContratadoIdAndAnhoAndMesAndContratoId(contratadoId, anho, mes, contratoId);
	}

	public List<CursoCargoContratado> findAllByCurso(Long cursoId, Integer anho, Integer mes) {
		return repository.findAllByCursoIdAndAnhoAndMes(cursoId, anho, mes);
	}

	public List<CursoCargoContratado> findAllByCursoIdAndContratoId(Long cursoId, Long contratoId) {
		return repository.findAllByCursoIdAndContratoId(cursoId, contratoId);
	}

	public List<CursoCargoContratado> findAllByPeriodo(Integer anho, Integer mes) {
		return repository.findAllByAnhoAndMes(anho, mes);
	}

	public CursoCargoContratado findByCursoCargo(Long cursoCargoContratadoId) {
		return repository.findByCursoCargoContratadoId(cursoCargoContratadoId)
				.orElseThrow(() -> new CursoCargoContratadoException(cursoCargoContratadoId));
	}

	public CursoCargoContratado findByContratado(Long cursoId, Integer anho, Integer mes, Long contratadoId) {
		return repository.findByCursoIdAndAnhoAndMesAndContratadoId(cursoId, anho, mes, contratadoId)
				.orElseThrow(() -> new CursoCargoContratadoException(cursoId, anho, mes, contratadoId));
	}

	public CursoCargoContratado add(CursoCargoContratado cursoCargoContratado) {
		cursoCargoContratado = repository.save(cursoCargoContratado);
		return cursoCargoContratado;
	}

	public CursoCargoContratado update(CursoCargoContratado newCursoCargoContratado, Long cursoCargoContratadoId) {
		return repository.findByCursoCargoContratadoId(cursoCargoContratadoId).map(cursoCargoContratado -> {
			cursoCargoContratado = new CursoCargoContratado(cursoCargoContratadoId,
					newCursoCargoContratado.getCursoId(), newCursoCargoContratado.getAnho(),
					newCursoCargoContratado.getMes(), newCursoCargoContratado.getContratadoId(),
					newCursoCargoContratado.getContratoId(), newCursoCargoContratado.getCargoTipoId(),
					newCursoCargoContratado.getHorasSemanales(), newCursoCargoContratado.getHorasTotales(),
					newCursoCargoContratado.getDesignacionTipoId(), newCursoCargoContratado.getCategoriaId(),
					newCursoCargoContratado.getCursoCargoNovedadId(), newCursoCargoContratado.getAcreditado(), null);
			cursoCargoContratado = repository.save(cursoCargoContratado);
			return cursoCargoContratado;
		}).orElseThrow(() -> new CursoCargoContratadoException(cursoCargoContratadoId));
	}

	@Transactional
	public void delete(Long cursoCargoContratadoId) {
		repository.deleteByCursoCargoContratadoId(cursoCargoContratadoId);
	}

	@Transactional
	public List<CursoCargoContratado> saveAll(List<CursoCargoContratado> cargos) {
		return repository.saveAll(cargos);
	}

	@Transactional
	public void deleteAllByCursoIdAndContratoIdAndContratadoId(Long cursoId, Long contratoId, Long contratadoId) {
		repository.deleteAllByCursoIdAndContratoIdAndContratadoId(cursoId, contratoId, contratadoId);
	}

}
