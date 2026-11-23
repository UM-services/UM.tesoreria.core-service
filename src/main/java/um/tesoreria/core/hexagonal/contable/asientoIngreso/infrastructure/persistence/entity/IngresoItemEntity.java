package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Tabla legacy {@code ingreso_items}: líneas de ajuste del asiento de ingreso que carga un proceso
 * externo (el sistema no las da de alta ni de baja). Es de sólo lectura.
 *
 * <p>La clave real es {@code (iit_acred, iit_tpa_id, iit_item)}; {@code iit_id} es un autoincremento
 * con índice no único, por eso no se usa como {@code @Id}. {@code iit_importe} es {@code double} en
 * MySQL y se lee como {@code BigDecimal} (mismo mapeo que hacía el acceso JDBC anterior).</p>
 */
@Getter
@Setter
@Entity
@Immutable
@Table(name = "ingreso_items")
@IdClass(IngresoItemEntity.IngresoItemId.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngresoItemEntity {

    @Id
    @Column(name = "iit_acred")
    private LocalDate acreditacion;

    @Id
    @Column(name = "iit_tpa_id")
    private Integer tipoPagoId;

    @Id
    @Column(name = "iit_item")
    private Integer item;

    @Column(name = "iit_pla_cuenta")
    private BigDecimal numeroCuenta;

    @Column(name = "iit_debita")
    private Byte debita;

    @Column(name = "iit_importe")
    private BigDecimal importe;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngresoItemId implements Serializable {

        private LocalDate acreditacion;
        private Integer tipoPagoId;
        private Integer item;

        @Override
        public boolean equals(Object otro) {
            if (this == otro) {
                return true;
            }
            if (!(otro instanceof IngresoItemId clave)) {
                return false;
            }
            return Objects.equals(acreditacion, clave.acreditacion)
                    && Objects.equals(tipoPagoId, clave.tipoPagoId)
                    && Objects.equals(item, clave.item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(acreditacion, tipoPagoId, item);
        }

    }

}
