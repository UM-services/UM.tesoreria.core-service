package um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in;

public interface DeleteBancariaUseCase {
    void deleteBancaria(Long bancariaId);
}
