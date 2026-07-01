package um.tesoreria.core.hexagonal.lectivo.domain.ports.in;

public interface DeleteLectivoUseCase {
    void deleteLectivo(Integer lectivoId);
}
