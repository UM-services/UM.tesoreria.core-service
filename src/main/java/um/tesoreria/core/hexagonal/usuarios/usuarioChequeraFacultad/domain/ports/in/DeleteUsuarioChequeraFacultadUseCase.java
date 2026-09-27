package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.ports.in;

public interface DeleteUsuarioChequeraFacultadUseCase {
    void deleteUsuarioChequeraFacultad(Long userId, Integer facultadId);
}
