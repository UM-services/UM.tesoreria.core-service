package um.tesoreria.core.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import um.tesoreria.core.hexagonal.cursoCargoContratado.domain.model.CursoHaberes;

@FeignClient(name = "haberes-core-service", contextId = "cursoClient", path = "/api/haberes/core/curso")
public interface CursoClient {

    @GetMapping("/{cursoId}")
    CursoHaberes findByCursoId(@PathVariable Long cursoId);

}
