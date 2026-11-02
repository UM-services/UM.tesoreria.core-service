package um.tesoreria.core.client.tesoreria.guarani;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "tesoreria-guarani-service", contextId = "guaraniCursadaClient",
        path = "/api/tesoreria/guarani/cursada")
public interface GuaraniCursadaClient {
    @GetMapping("/documento/{nroDocumento}/anioAcademico/{anioAcademico}")
    List<CursadaGuaraniResponse> findByDocumentoAndAnioAcademico(
            @PathVariable("nroDocumento") String nroDocumento,
            @PathVariable("anioAcademico") Integer anioAcademico);
}
