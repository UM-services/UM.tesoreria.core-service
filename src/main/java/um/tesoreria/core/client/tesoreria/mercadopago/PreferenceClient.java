package um.tesoreria.core.client.tesoreria.mercadopago;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import um.tesoreria.core.kotlin.model.dto.UMPreferenceMPDto;
import um.tesoreria.core.model.dto.MercadoPagoContextDto;

@FeignClient(name = "tesoreria-mercadopago-service", contextId = "preferenceClient", path = "/api/tesoreria/mercadopago/preference")
public interface PreferenceClient {

    @PostMapping("/create")
    MercadoPagoContextDto createPreference(
            @RequestBody UMPreferenceMPDto umPreferenceMPDto);

}
