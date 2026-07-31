/**
 * 
 */
package um.tesoreria.core.service.view;

import lombok.RequiredArgsConstructor;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.CuotaDeudaPayPerTic;
import um.tesoreria.core.repository.view.CuotaDeudaPayPerTicRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class CuotaDeudaPayPerTicService {

	private final CuotaDeudaPayPerTicRepository repository;

	public List<CuotaDeudaPayPerTic> findAllByVencimiento1Between(OffsetDateTime desde, OffsetDateTime hasta) {
		return repository.findAllByVencimiento1Between(desde, hasta);
	}

}
