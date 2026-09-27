package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.ports.in;

public interface DeleteUsuarioChequeraClaseChequeraUseCase {
    void deleteUsuarioChequeraClaseChequera(Long userId, Integer claseChequeraId);
}
