package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.model.UsuarioChequeraGeografica;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.ports.in.CreateUsuarioChequeraGeograficaUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.ports.in.DeleteUsuarioChequeraGeograficaUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.ports.in.GetUsuarioChequeraGeograficasByUserIdUseCase;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioChequeraGeograficaService {

    private final GetUsuarioChequeraGeograficasByUserIdUseCase getUsuarioChequeraGeograficasByUserIdUseCase;
    private final CreateUsuarioChequeraGeograficaUseCase createUsuarioChequeraGeograficaUseCase;
    private final DeleteUsuarioChequeraGeograficaUseCase deleteUsuarioChequeraGeograficaUseCase;

    public List<UsuarioChequeraGeografica> findAllByUserId(Long userId) {
        return getUsuarioChequeraGeograficasByUserIdUseCase.getByUserId(userId);
    }

    public UsuarioChequeraGeografica add(UsuarioChequeraGeografica usuarioChequeraGeografica) {
        return createUsuarioChequeraGeograficaUseCase.createUsuarioChequeraGeografica(usuarioChequeraGeografica);
    }

    public void delete(Long userId, Integer geograficaId) {
        deleteUsuarioChequeraGeograficaUseCase.deleteUsuarioChequeraGeografica(userId, geograficaId);
    }

}
