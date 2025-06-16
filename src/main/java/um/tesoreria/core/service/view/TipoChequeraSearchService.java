package um.tesoreria.core.service.view;

import org.springframework.stereotype.Service;
import um.tesoreria.core.model.view.TipoChequeraSearch;
import um.tesoreria.core.repository.view.TipoChequeraSearchRepository;

import java.util.List;

@Service
public class TipoChequeraSearchService {

    private final TipoChequeraSearchRepository repository;

    public TipoChequeraSearchService(TipoChequeraSearchRepository repository) {
        this.repository = repository;
    }

    public List<TipoChequeraSearch> findAllByStrings(List<String> conditions) {
        return repository.findAllByStrings(conditions);
    }

}
