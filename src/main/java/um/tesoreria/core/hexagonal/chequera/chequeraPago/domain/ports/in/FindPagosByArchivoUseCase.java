package um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;

import java.util.List;

public interface FindPagosByArchivoUseCase {

    List<ChequeraPago> findPagosByArchivo(String archivo);

}
