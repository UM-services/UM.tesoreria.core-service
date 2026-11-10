package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.ResultadoGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.out.PagarFileGenerator;
import um.tesoreria.core.service.facade.PagarFileService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class PagarFileGeneratorAdapter implements PagarFileGenerator {

    private final PagarFileService pagarFileService;

    @Override
    public ResultadoGeneracion generar(OffsetDateTime desde, OffsetDateTime hasta) {
        try {
            Path path = Path.of(pagarFileService.generateFiles(desde, hasta));
            return new ResultadoGeneracion(path.getFileName().toString(), "application/zip",
                    Files.readAllBytes(path), 0, BigDecimal.ZERO);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo generar el archivo PAGAR", e);
        }
    }

}
