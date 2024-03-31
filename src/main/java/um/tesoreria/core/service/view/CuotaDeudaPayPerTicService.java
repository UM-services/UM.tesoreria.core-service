/**
 * 
 */
package um.tesoreria.core.service.view;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.CuotaDeudaPayPerTic;
import um.tesoreria.core.repository.view.ICuotaDeudaPayPerTicRepository;

/**
 * @author daniel
 *
 */
@Service
public class CuotaDeudaPayPerTicService {

	@Autowired
	private ICuotaDeudaPayPerTicRepository repository;

	public List<CuotaDeudaPayPerTic> findAllByVencimiento1Between(OffsetDateTime desde, OffsetDateTime hasta) {
		return repository.findAllByVencimiento1Between(desde, hasta);
	}

}
