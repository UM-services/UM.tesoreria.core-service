package um.tesoreria.core.hexagonal.auth.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;
import um.tesoreria.core.hexagonal.auth.domain.ports.in.LoginUseCase;
import um.tesoreria.core.hexagonal.auth.domain.ports.out.UsuarioAuthRepository;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginUseCaseImpl implements LoginUseCase {

    private final UsuarioAuthRepository usuarioAuthRepository;

    @Override
    public UsuarioAuth login(String login, String password) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: Falta USUARIO . . .");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: Falta CLAVE . . .");
        }

        String safeLogin = login.trim();
        String safePassword = password.trim();
        // El sistema original (UsuarioService) hashea las claves con SHA-256
        String hashedPassword = DigestUtils.sha256Hex(safePassword);

        // 1. Intentamos buscar por Login
        UsuarioAuth usuario = usuarioAuthRepository.findByLogin(safeLogin).orElse(null);

        if (usuario != null) {
            // Validamos clave hasheada
            String dbPassword = usuario.getPassword() != null ? usuario.getPassword().trim() : "";
            if (!hashedPassword.equals(dbPassword)) {
                log.warn("Fallo de login (Clave incorrecta) para usuario: {}", safeLogin);
                throw new IllegalArgumentException("ERROR: Usuario NO Válido");
            }
        } else {
            // 2. Si no se encontró por login (Case Sensitivity), buscamos por clave hasheada
            usuario = usuarioAuthRepository.findByPassword(hashedPassword).orElse(null);
            
            if (usuario == null) {
                log.warn("Fallo de login (Usuario no encontrado) para: {}", safeLogin);
                throw new IllegalArgumentException("ERROR: Usuario NO Válido");
            }
            
            // Comparamos el login ignorando mayúsculas/minúsculas
            String dbLogin = usuario.getLogin() != null ? usuario.getLogin().trim() : "";
            if (!safeLogin.equalsIgnoreCase(dbLogin)) {
                log.warn("Fallo de login (Login no coincide con clave) para: {}. DB tenia: {}", safeLogin, dbLogin);
                throw new IllegalArgumentException("ERROR: Usuario NO Válido");
            }
        }

        if (!usuario.isActivo()) {
             log.warn("Fallo de login (Usuario inactivo) para: {}", safeLogin);
             throw new IllegalArgumentException("ERROR: Usuario inactivo");
        }

        log.info("Login exitoso para usuario: {}", safeLogin);
        usuario.setLastLog(OffsetDateTime.now());
        return usuarioAuthRepository.save(usuario);
    }
}
