/**
 * 
 */
package um.tesoreria.core.service;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.CursoCargoContratadoException;
import um.tesoreria.core.kotlin.model.CursoCargoContratado;
import um.tesoreria.core.repository.CursoCargoContratadoRepository;
import um.tesoreria.core.util.Jsonifier;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CursoCargoContratadoService {

	private final CursoCargoContratadoRepository repository;

	public List<CursoCargoContratado> findAllByContrato(Long contratoId,
                                                          Integer anho,
                                                          Integer mes,
                                                          BigDecimal personaId,
                                                          Integer documentoId
    ) {
        log.debug("Processing CursoCargoContratadoService.findAllByContrato");
		var cursoCargoContratados = repository.findAllByContratoIdAndAnhoAndMesAndPersonaIdAndDocumentoId(
                contratoId,
                anho,
                mes,
                personaId,
                documentoId
        );
        log.debug("Cursos -> {}", Jsonifier.builder(cursoCargoContratados).build());
        return cursoCargoContratados;
	}

    public List<CursoCargoContratado> findAllByPersona(BigDecimal personaId, Integer documentoId, Integer anho, Integer mes) {
        log.debug("Processing CursoCargoContratadoService.findAllByPersona");
        var cursoCargoContratados = repository.findAllByPersonaIdAndDocumentoIdAndAnhoAndMes(personaId, documentoId, anho, mes);
        log.debug("Cursos -> {}", Jsonifier.builder(cursoCargoContratados).build());
        return cursoCargoContratados;
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

	public CursoCargoContratado add(CursoCargoContratado cursoCargoContratado) {
		cursoCargoContratado = repository.save(cursoCargoContratado);
		return cursoCargoContratado;
	}

	public CursoCargoContratado update(CursoCargoContratado newCursoCargoContratado, Long cursoCargoContratadoId) {
		return repository.findByCursoCargoContratadoId(cursoCargoContratadoId).map(cursoCargoContratado -> {
            cursoCargoContratado.setCursoId(newCursoCargoContratado.getCursoId());
            cursoCargoContratado.setAnho(newCursoCargoContratado.getAnho());
            cursoCargoContratado.setMes(newCursoCargoContratado.getMes());
            cursoCargoContratado.setContratoId(newCursoCargoContratado.getContratoId());
            cursoCargoContratado.setPersonaId(newCursoCargoContratado.getPersonaId());
            cursoCargoContratado.setDocumentoId(newCursoCargoContratado.getDocumentoId());
            cursoCargoContratado.setCargoTipoId(newCursoCargoContratado.getCargoTipoId());
            cursoCargoContratado.setHorasSemanales(newCursoCargoContratado.getHorasSemanales());
            cursoCargoContratado.setHorasTotales(newCursoCargoContratado.getHorasTotales());
            cursoCargoContratado.setDesignacionTipoId(newCursoCargoContratado.getDesignacionTipoId());
            cursoCargoContratado.setCategoriaId(newCursoCargoContratado.getCategoriaId());
            cursoCargoContratado.setCursoCargoNovedadId(newCursoCargoContratado.getCursoCargoNovedadId());
            cursoCargoContratado.setAcreditado(newCursoCargoContratado.getAcreditado());
			cursoCargoContratado = repository.save(cursoCargoContratado);
            log.debug("CursoCargoContratado -> {}", cursoCargoContratado.jsonify());
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
	public void deleteAllByCursoIdAndContratoId(Long cursoId, Long contratoId) {
		repository.deleteAllByCursoIdAndContratoId(cursoId, contratoId);
	}

    @Transactional
    public void deleteByContratoAndPeriodo(Long contratoId, Integer anho, Integer mes) {
        repository.deleteAllByContratoIdAndAnhoAndMes(contratoId, anho, mes);
    }

}
