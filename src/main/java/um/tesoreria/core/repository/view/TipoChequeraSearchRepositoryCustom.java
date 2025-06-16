package um.tesoreria.core.repository.view;

import um.tesoreria.core.model.view.TipoChequeraSearch;

import java.util.List;

public interface TipoChequeraSearchRepositoryCustom {

    List<TipoChequeraSearch> findAllByStrings(List<String> conditions);

}
