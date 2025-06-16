package um.tesoreria.core.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;
import um.tesoreria.core.model.view.TipoChequeraSearch;

public interface TipoChequeraSearchRepository extends JpaRepository<TipoChequeraSearch, Integer>, TipoChequeraSearchRepositoryCustom {
}
