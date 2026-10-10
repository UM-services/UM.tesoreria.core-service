package um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in;

public interface DeleteChequeraPagoUseCase {
    void deleteChequeraPago(Long chequeraPagoId);
}