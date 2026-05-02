/**
 * 
 */
package um.tesoreria.core.hexagonal.cuenta.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.CuentaException;
import um.tesoreria.core.hexagonal.cuenta.infrastructure.persistence.entity.CuentaEntity;
import um.tesoreria.core.model.view.CuentaSearch;
import um.tesoreria.core.hexagonal.cuenta.infrastructure.persistence.repository.CuentaRepository;
import um.tesoreria.core.service.view.CuentaSearchService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class CuentaService {

	@Autowired
	private CuentaRepository repository;

	@Autowired
	private CuentaSearchService cuentaSearchService;

	public List<CuentaEntity> findAll() {
		return repository.findAll();
	}

	public List<CuentaEntity> findAllByNumeroCuentaIn(List<BigDecimal> cuentas) {
		return repository.findAllByNumeroCuentaIn(cuentas);
	}

	public List<CuentaEntity> findAllGrado5() {
		return repository.findAllByGradoAndNumeroCuentaGreaterThan(5, BigDecimal.ZERO,
				Sort.by("numeroCuenta").ascending());
	}

	public List<CuentaEntity> findAllByCierreResultado() {
		return repository.findAllByGradoAndNumeroCuentaBetween(5, new BigDecimal(30000000000L),
				new BigDecimal(49999999999L));
	}

	public List<CuentaEntity> findAllByCierreActivoPasivo() {
		return repository.findAllByGradoAndNumeroCuentaBetween(5, new BigDecimal(10000000000L),
				new BigDecimal(29999999999L));
	}

	public List<CuentaSearch> findByStrings(List<String> conditions, Boolean visible) {
		return cuentaSearchService.findAllByStrings(conditions, visible);
	}

	public CuentaEntity findByNumeroCuenta(BigDecimal numeroCuenta) {
		var cuenta = repository.findByNumeroCuenta(numeroCuenta).orElseThrow(() -> new CuentaException(numeroCuenta));
        try {
            log.debug("Cuenta -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter()
                    .writeValueAsString(cuenta));
        } catch (JsonProcessingException e) {
            log.debug("Cuenta -> error {}", e.getMessage());
        }
        return cuenta;
	}

	public CuentaEntity findByCuentaContableId(Long cuentaContableId) {
		return repository.findByCuentaContableId(cuentaContableId)
				.orElseThrow(() -> new CuentaException(cuentaContableId));
	}

	public CuentaEntity add(CuentaEntity cuenta) {
		cuenta = repository.save(cuenta);
		return cuenta;
	}

	public CuentaEntity update(CuentaEntity newCuenta, BigDecimal numeroCuenta) {
		log.debug("New -> {}", newCuenta);
		return repository.findByNumeroCuenta(numeroCuenta).map(cuenta -> {
			log.debug("Cuenta -> {}", cuenta);
			cuenta = new CuentaEntity(numeroCuenta, newCuenta.getNombre(), newCuenta.getIntegradora(), newCuenta.getGrado(),
					newCuenta.getGrado1(), newCuenta.getGrado2(), newCuenta.getGrado3(), newCuenta.getGrado4(),
					newCuenta.getGeograficaId(), newCuenta.getFechaBloqueo(), newCuenta.getVisible(),
					newCuenta.getCuentaContableId(), null);
			cuenta = repository.save(cuenta);
			return cuenta;
		}).orElseThrow(() -> new CuentaException(numeroCuenta));
	}

	@Transactional
	public List<CuentaEntity> saveAll(List<CuentaEntity> cuentas) {
		return repository.saveAll(cuentas);
	}

	@Transactional
	public void delete(BigDecimal numeroCuenta) {
		repository.deleteByNumeroCuenta(numeroCuenta);
	}

	@Transactional
	public String recalculaGrados() {
		List<CuentaEntity> cuentas = findAll();
		for (CuentaEntity cuenta : cuentas) {
			cuenta.setGrado(5);
			BigDecimal factor = new BigDecimal(10000);
			cuenta.setGrado4(cuenta.getNumeroCuenta().divide(factor).setScale(0, RoundingMode.DOWN).multiply(factor)
					.setScale(0, RoundingMode.DOWN));
			factor = new BigDecimal(1000000);
			cuenta.setGrado3(cuenta.getNumeroCuenta().divide(factor).setScale(0, RoundingMode.DOWN).multiply(factor)
					.setScale(0, RoundingMode.DOWN));
			factor = new BigDecimal(100000000);
			cuenta.setGrado2(cuenta.getNumeroCuenta().divide(factor).setScale(0, RoundingMode.DOWN).multiply(factor)
					.setScale(0, RoundingMode.DOWN));
			factor = new BigDecimal(10000000000L);
			cuenta.setGrado1(cuenta.getNumeroCuenta().divide(factor).setScale(0, RoundingMode.DOWN).multiply(factor)
					.setScale(0, RoundingMode.DOWN));
			if (cuenta.getNumeroCuenta().compareTo(cuenta.getGrado4()) == 0) {
				cuenta.setGrado(4);
			}
			if (cuenta.getNumeroCuenta().compareTo(cuenta.getGrado3()) == 0) {
				cuenta.setGrado(3);
			}
			if (cuenta.getNumeroCuenta().compareTo(cuenta.getGrado2()) == 0) {
				cuenta.setGrado(2);
			}
			if (cuenta.getNumeroCuenta().compareTo(cuenta.getGrado1()) == 0) {
				cuenta.setGrado(1);
			}
		}
		cuentas = saveAll(cuentas);
		return "Ok";
	}

}
