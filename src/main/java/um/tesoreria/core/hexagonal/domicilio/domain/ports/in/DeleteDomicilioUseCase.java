package um.tesoreria.core.hexagonal.domicilio.domain.ports.in;

public interface DeleteDomicilioUseCase {
    boolean deleteDomicilio(Long id);
}
