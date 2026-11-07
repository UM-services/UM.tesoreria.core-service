package um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in;

public interface DeletePagosByArchivoUseCase {

    void deletePagosByArchivo(String archivo);

}
