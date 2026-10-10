package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.model;

import java.math.BigDecimal;

/**
 * Límite de autorización por monto de un usuario para un ejercicio dado.
 *
 * <ul>
 *   <li>{@code tieneAutoridad}: el usuario tiene al menos un perfil activo asignado.</li>
 *   <li>{@code ilimitado}: alguno de sus perfiles es "sin límite" (multiplico nulo).</li>
 *   <li>{@code multiplico}: mayor múltiplo entre sus perfiles ({@code null} si ilimitado o sin perfil).</li>
 *   <li>{@code referencia}: importe de referencia del ejercicio ({@code null} si no está cargado).</li>
 *   <li>{@code limite}: {@code multiplico × referencia}; {@code null} si ilimitado o si no puede
 *       calcularse (sin referencia).</li>
 * </ul>
 *
 * <p>La fachada decide fail-closed: deniega si {@code !tieneAutoridad}, o si
 * {@code !ilimitado && (limite == null || monto > limite)}.</p>
 */
public record LimiteAutorizacion(
        Integer usuarioId,
        Integer ejercicioId,
        Integer multiplico,
        BigDecimal referencia,
        BigDecimal limite,
        boolean ilimitado,
        boolean tieneAutoridad) {

}
