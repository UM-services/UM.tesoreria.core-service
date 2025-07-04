/**
 * 
 */
package um.tesoreria.core.service;

import java.time.OffsetDateTime;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.PayPerTicException;
import um.tesoreria.core.model.PayPerTic;
import um.tesoreria.core.repository.PayPerTicRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class PayPerTicService {
	@Autowired
	private PayPerTicRepository repository;

	public List<PayPerTic> findAllByPeriodo(OffsetDateTime desde, OffsetDateTime hasta) {
		return repository.findAllByPaymentdateBetween(desde, hasta);
	}

	public PayPerTic findByPayperticId(String payperticId) {
		return repository.findByPayperticId(payperticId).orElseThrow(() -> new PayPerTicException(payperticId));
	}

	public PayPerTic update(PayPerTic newpaypertic, String payperticId) {
		return repository.findByPayperticId(payperticId).map(paypertic -> {
			paypertic.setExternaltransactionId(newpaypertic.getExternaltransactionId());
			paypertic.setConceptId(newpaypertic.getConceptId());
			paypertic.setConceptdescription(newpaypertic.getConceptdescription());
			paypertic.setPayername(newpaypertic.getPayername());
			paypertic.setPayeremail(newpaypertic.getPayeremail());
			paypertic.setPayernumberId(newpaypertic.getPayernumberId());
			paypertic.setUploaddate(newpaypertic.getUploaddate());
			paypertic.setDuedate(newpaypertic.getDuedate());
			paypertic.setPaymentdate(newpaypertic.getPaymentdate());
			paypertic.setAccreditationdate(newpaypertic.getAccreditationdate());
			paypertic.setAmount(newpaypertic.getAmount());
			paypertic.setArancelrecaudacion(newpaypertic.getArancelrecaudacion());
			paypertic.setIvaservicio(newpaypertic.getIvaservicio());
			paypertic.setCostomediopago(newpaypertic.getCostomediopago());
			paypertic.setCostofinanciacion(newpaypertic.getCostofinanciacion());
			paypertic.setOtroscostos(newpaypertic.getOtroscostos());
			paypertic.setImpuestosrefacturacion(newpaypertic.getImpuestosrefacturacion());
			paypertic.setTotalfactura(newpaypertic.getTotalfactura());
			paypertic.setSheet(newpaypertic.getSheet());
			paypertic.setImportado(newpaypertic.getImportado());
			repository.save(paypertic);
			log.debug("PayperTIC -> " + paypertic);
			return paypertic;
		}).orElseThrow(() -> new PayPerTicException(payperticId));
	}

	@Transactional
	public List<PayPerTic> saveAll(List<PayPerTic> pagos) {
		return repository.saveAll(pagos);
	}

}
