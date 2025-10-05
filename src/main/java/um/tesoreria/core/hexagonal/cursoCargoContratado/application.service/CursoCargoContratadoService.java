/**
 * 
 */
package um.tesoreria.core.hexagonal.cursoCargoContratado.application.service;

import java.math.BigDecimal;
import java.util.List;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.CursoCargoContratadoException;
import um.tesoreria.core.hexagonal.cursoCargoContratado.domain.model.CursoCargoContratado;
import um.tesoreria.core.hexagonal.cursoCargoContratado.domain.ports.in.GetAllCargosByPersonaUseCase;
import um.tesoreria.core.hexagonal.cursoCargoContratado.domain.ports.out.CursoCargoContratadoRepository;
import um.tesoreria.core.hexagonal.cursoCargoContratado.infrastructure.persistence.entity.CursoCargoContratadoEntity;
import um.tesoreria.core.hexagonal.cursoCargoContratado.infrastructure.persistence.repository.JpaCursoCargoContratadoRepository;
import um.tesoreria.core.util.Jsonifier;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CursoCargoContratadoService implements GetAllCargosByPersonaUseCase {

	private final JpaCursoCargoContratadoRepository repository;

    private final CursoCargoContratadoRepository cursoCargoContratadoRepository;

    public List<CursoCargoContratadoEntity> findAllByPersona(BigDecimal personaId, Integer documentoId, Integer anho, Integer mes) {
        log.debug("Processing CursoCargoContratadoService.findAllByPersona");
        var cursoCargoContratados = repository.findAllByPersonaIdAndDocumentoIdAndAnhoAndMes(personaId, documentoId, anho, mes);
        log.debug("Cursos -> {}", Jsonifier.builder(cursoCargoContratados).build());
        return cursoCargoContratados;
    }

    public List<CursoCargoContratadoEntity> findAllByCurso(Long cursoId, Integer anho, Integer mes) {
        log.debug("Processing CursoCargoContratadoService.findAllByCurso");
        var cargos = repository.findAllByCursoIdAndAnhoAndMes(cursoId, anho, mes);
        log.debug("Cargos -> {}", Jsonifier.builder(cargos).build());
		return cargos;
	}

	public List<CursoCargoContratadoEntity> findAllByCursoIdAndContratoId(Long cursoId, Long contratoId) {
		return repository.findAllByCursoIdAndContratoId(cursoId, contratoId);
	}

	public List<CursoCargoContratadoEntity> findAllByPeriodo(Integer anho, Integer mes) {
		return repository.findAllByAnhoAndMes(anho, mes);
	}

	public CursoCargoContratadoEntity findByCursoCargo(Long cursoCargoContratadoId) {
		return repository.findByCursoCargoContratadoId(cursoCargoContratadoId)
				.orElseThrow(() -> new CursoCargoContratadoException(cursoCargoContratadoId));
	}

	public CursoCargoContratadoEntity add(CursoCargoContratadoEntity cursoCargoContratadoEntity) {
		cursoCargoContratadoEntity = repository.save(cursoCargoContratadoEntity);
		return cursoCargoContratadoEntity;
	}

	public CursoCargoContratadoEntity update(CursoCargoContratadoEntity newCursoCargoContratadoEntity, Long cursoCargoContratadoId) {
		return repository.findByCursoCargoContratadoId(cursoCargoContratadoId).map(cursoCargoContratadoEntity -> {
            cursoCargoContratadoEntity.setCursoId(newCursoCargoContratadoEntity.getCursoId());
            cursoCargoContratadoEntity.setAnho(newCursoCargoContratadoEntity.getAnho());
            cursoCargoContratadoEntity.setMes(newCursoCargoContratadoEntity.getMes());
            cursoCargoContratadoEntity.setContratoId(newCursoCargoContratadoEntity.getContratoId());
            cursoCargoContratadoEntity.setPersonaId(newCursoCargoContratadoEntity.getPersonaId());
            cursoCargoContratadoEntity.setDocumentoId(newCursoCargoContratadoEntity.getDocumentoId());
            cursoCargoContratadoEntity.setCargoTipoId(newCursoCargoContratadoEntity.getCargoTipoId());
            cursoCargoContratadoEntity.setHorasSemanales(newCursoCargoContratadoEntity.getHorasSemanales());
            cursoCargoContratadoEntity.setHorasTotales(newCursoCargoContratadoEntity.getHorasTotales());
            cursoCargoContratadoEntity.setDesignacionTipoId(newCursoCargoContratadoEntity.getDesignacionTipoId());
            cursoCargoContratadoEntity.setCategoriaId(newCursoCargoContratadoEntity.getCategoriaId());
            cursoCargoContratadoEntity.setCursoCargoNovedadId(newCursoCargoContratadoEntity.getCursoCargoNovedadId());
            cursoCargoContratadoEntity.setAcreditado(newCursoCargoContratadoEntity.getAcreditado());
			cursoCargoContratadoEntity = repository.save(cursoCargoContratadoEntity);
            log.debug("CursoCargoContratado -> {}", cursoCargoContratadoEntity.jsonify());
			return cursoCargoContratadoEntity;
		}).orElseThrow(() -> new CursoCargoContratadoException(cursoCargoContratadoId));
	}

	@Transactional
	public void delete(Long cursoCargoContratadoId) {
		repository.deleteByCursoCargoContratadoId(cursoCargoContratadoId);
	}

	@Transactional
	public List<CursoCargoContratadoEntity> saveAll(List<CursoCargoContratadoEntity> cargos) {
		return repository.saveAll(cargos);
	}

	@Transactional
	public void deleteAllByCursoIdAndContratoId(Long cursoId, Long contratoId) {
		repository.deleteAllByCursoIdAndContratoId(cursoId, contratoId);
	}

    @Transactional
    public void deleteByContratoAndPeriodo(Long contratoId, Integer anho, Integer mes) {
        repository.deleteAllByContratoIdAndAnhoAndMes(contratoId, anho, mes);
    }

    // Comienza hexagonal
    @Override
    public List<CursoCargoContratado> getAllCargosByPersona(Long contratoId, Integer anho, Integer mes, BigDecimal personaId, Integer documentoId) {
        return cursoCargoContratadoRepository.findAllByContrato(contratoId, anho, mes, personaId, documentoId);
    }

}
