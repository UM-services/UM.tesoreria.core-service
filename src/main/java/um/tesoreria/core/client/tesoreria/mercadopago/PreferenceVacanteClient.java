package um.tesoreria.core.client.tesoreria.mercadopago;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import um.tesoreria.core.model.dto.MercadoPagoContextDto;
import um.tesoreria.core.model.dto.UMPreferenceMPDto;

@FeignClient(name = "tesoreria-mercadopago-service", contextId = "preferenceVacanteClient", path = "/api/tesoreria/mercadopago/vacante/preference")
public interface PreferenceVacanteClient {

    @PostMapping("/create")
    MercadoPagoContextDto createPreference(@RequestBody UMPreferenceMPDto umPreferenceMPDto);

}
