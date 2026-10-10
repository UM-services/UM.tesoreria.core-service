package um.tesoreria.core.hexagonal.auth.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;
import um.tesoreria.core.hexagonal.auth.domain.ports.in.ChangePasswordUseCase;
import um.tesoreria.core.hexagonal.auth.domain.ports.out.UsuarioAuthRepository;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChangePasswordUseCaseImpl implements ChangePasswordUseCase {

    private final UsuarioAuthRepository usuarioAuthRepository;

    @Override
    public UsuarioAuth changePassword(Long userId, String login, String currentPassword, String newPassword, String reClaveNueva, String nombre) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: Falta CLAVE . . .");
        }

        if (reClaveNueva != null && !newPassword.trim().equals(reClaveNueva.trim())) {
            throw new IllegalArgumentException("ERROR: Claves NO Coinciden");
        }

        UsuarioAuth usuario = null;
        if (userId != null) {
            usuario = usuarioAuthRepository.findById(userId).orElse(null);
        }
        if (usuario == null && login != null && !login.trim().isEmpty()) {
            usuario = usuarioAuthRepository.findByLogin(login.trim()).orElse(null);
        }

        if (usuario == null) {
            log.warn("Intento de cambio de clave para usuario inexistente. userId: {}, login: {}", userId, login);
            throw new IllegalArgumentException("ERROR: Usuario NO Encontrado");
        }

        // 1. Verifica la clave anterior
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: Falta Clave Anterior");
        }
        String currentHashed = DigestUtils.sha256Hex(currentPassword.trim());
        String dbPassword = usuario.getPassword() != null ? usuario.getPassword().trim() : "";
        if (!currentHashed.equals(dbPassword)) {
            log.warn("Clave anterior incorrecta para usuario: {}", usuario.getLogin());
            throw new IllegalArgumentException("ERROR: Usuario NO Autenticado");
        }

        // 2. Verifica si otro usuario ya tiene la nueva clave
        String newHashed = DigestUtils.sha256Hex(newPassword.trim());
        Optional<UsuarioAuth> existingWithPassword = usuarioAuthRepository.findByPassword(newHashed);
        if (existingWithPassword.isPresent() && !existingWithPassword.get().getUserId().equals(usuario.getUserId())) {
            log.warn("Clave nueva ya existe en otro usuario. userId solicitante: {}", usuario.getUserId());
            throw new IllegalArgumentException("ERROR: Clave NO Válida");
        }

        // 3. Actualiza contraseña y nombre si aplica
        String newNombre = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        usuario.setPassword(newHashed);
        if (newNombre != null) {
            usuario.setNombre(newNombre);
        }
        // La clave ya la eligio el propio usuario: se limpia el cambio forzado.
        usuario.setDebeCambiarClave((byte) 0);

        log.info("Cambio de clave exitoso para usuario: {}", usuario.getLogin());
        usuarioAuthRepository.updateCredentials(usuario.getUserId(), newHashed, newNombre);
        return usuario;
    }
}
