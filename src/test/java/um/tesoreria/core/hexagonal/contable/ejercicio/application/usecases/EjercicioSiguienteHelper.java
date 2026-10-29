package um.tesoreria.core.hexagonal.contable.ejercicio.application.usecases;

import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;

import java.time.LocalDate;

final class EjercicioSiguienteHelper {
    private EjercicioSiguienteHelper() {
    }

    static Ejercicio ejercicio(int id, String inicio, String fin) {
        return Ejercicio.builder().ejercicioId(id).fechaInicio(LocalDate.parse(inicio)).fechaFinal(LocalDate.parse(fin))
                .bloqueado((byte) 0).ordenContableResultado(0).ordenContableBienesUso(0).nombre("Ejercicio").build();
    }
}
