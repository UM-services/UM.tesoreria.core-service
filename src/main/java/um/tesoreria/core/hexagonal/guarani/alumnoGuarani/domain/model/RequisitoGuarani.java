package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequisitoGuarani {
    private Integer requisito;
    private String nombre;
    private Integer requisitoTipo;
    private RequisitoTipoGuarani requisitoTipoRel;
    private Integer requisitoSubordinadoDe;
    private String descripcion;
    private Integer regla;
    private Integer cacheable;
    private String requisitoIngreso;
    private String activo;
    private String tipoDocumentoDigital;
    private String requisitoEgreso;
    private String aplicaABecas;
}
