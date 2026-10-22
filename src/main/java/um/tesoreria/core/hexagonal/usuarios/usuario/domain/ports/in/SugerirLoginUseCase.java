package um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in;

public interface SugerirLoginUseCase {

    /**
     * Devuelve el primer login disponible para los nombres y apellido indicados.
     */
    String sugerirLogin(String nombre, String apellido);
}
