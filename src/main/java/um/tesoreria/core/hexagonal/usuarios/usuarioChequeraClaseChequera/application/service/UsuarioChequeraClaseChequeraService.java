package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model.UsuarioChequeraClaseChequera;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.ports.in.CreateUsuarioChequeraClaseChequeraUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.ports.in.DeleteUsuarioChequeraClaseChequeraUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.ports.in.GetUsuarioChequeraClaseChequerasByUserIdUseCase;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioChequeraClaseChequeraService {

    private final GetUsuarioChequeraClaseChequerasByUserIdUseCase getUsuarioChequeraClaseChequerasByUserIdUseCase;
    private final CreateUsuarioChequeraClaseChequeraUseCase createUsuarioChequeraClaseChequeraUseCase;
    private final DeleteUsuarioChequeraClaseChequeraUseCase deleteUsuarioChequeraClaseChequeraUseCase;

    public List<UsuarioChequeraClaseChequera> findAllByUserId(Long userId) {
        return getUsuarioChequeraClaseChequerasByUserIdUseCase.getByUserId(userId);
    }

    public UsuarioChequeraClaseChequera add(UsuarioChequeraClaseChequera usuarioChequeraClaseChequera) {
        return createUsuarioChequeraClaseChequeraUseCase.createUsuarioChequeraClaseChequera(usuarioChequeraClaseChequera);
    }

    public void delete(Long userId, Integer claseChequeraId) {
        deleteUsuarioChequeraClaseChequeraUseCase.deleteUsuarioChequeraClaseChequera(userId, claseChequeraId);
    }

}
