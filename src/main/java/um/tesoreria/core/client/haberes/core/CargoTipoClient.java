package um.tesoreria.core.client.haberes.core;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import um.tesoreria.core.hexagonal.cursoCargoContratado.domain.model.CargoTipo;

@FeignClient(name = "haberes-core-service", contextId = "cargoTipoClient", path = "/api/haberes/core/cargotipo")
public interface CargoTipoClient {

    @GetMapping("/{cargoTipoId}")
    CargoTipo findByCargoTipoId(@PathVariable Integer cargoTipoId);

}
