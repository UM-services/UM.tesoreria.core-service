package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.ports.in;

public interface DeleteUsuarioChequeraGeograficaUseCase {
    void deleteUsuarioChequeraGeografica(Long userId, Integer geograficaId);
}
