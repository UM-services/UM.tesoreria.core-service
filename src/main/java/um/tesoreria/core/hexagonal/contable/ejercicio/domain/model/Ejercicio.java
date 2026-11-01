package um.tesoreria.core.hexagonal.contable.ejercicio.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** Fechas como día calendario (UTC): la base las guarda como datetime a las 00:00. */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Ejercicio {
    private Integer ejercicioId;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFinal;
    private Byte bloqueado;
    private Integer ordenContableResultado;
    private Integer ordenContableBienesUso;
}
