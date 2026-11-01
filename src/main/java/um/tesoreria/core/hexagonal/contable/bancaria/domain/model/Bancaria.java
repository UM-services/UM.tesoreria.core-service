package um.tesoreria.core.hexagonal.contable.bancaria.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Cuenta bancaria. Los textos nunca son null (la tabla los guarda NOT NULL, el «sin dato» es ''); la cuenta contable
 * es obligatoria y el proveedor es opcional. created, updated y uuid los fija el alta o la base, no el cliente.
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Bancaria {
    private Long bancariaId;
    private String banco;
    private String numero;
    private String cbu;
    private String titular;
    private String cuit;
    private BigDecimal numeroCuenta;
    private Integer proveedorId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String uuid;

    /** Igualdad de los datos editables: ignora id, created, updated y uuid, y compara la cuenta por valor (no por escala). */
    public boolean mismosDatos(Bancaria otra) {
        return Objects.equals(banco, otra.banco)
                && Objects.equals(numero, otra.numero)
                && Objects.equals(cbu, otra.cbu)
                && Objects.equals(titular, otra.titular)
                && Objects.equals(cuit, otra.cuit)
                && (numeroCuenta == null ? otra.numeroCuenta == null
                        : otra.numeroCuenta != null && numeroCuenta.compareTo(otra.numeroCuenta) == 0)
                && Objects.equals(proveedorId, otra.proveedorId);
    }
}
