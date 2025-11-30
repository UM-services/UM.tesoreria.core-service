package um.tesoreria.core.client.tesoreria.mercadopago;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import um.tesoreria.core.kotlin.model.dto.UMPreferenceMPDto;

@FeignClient(name = "tesoreria-mercadopago-service", contextId = "preferenceClient", path = "/api/tesoreria/mercadopago/preference")
public interface PreferenceClient {

    @PostMapping("/create")
    String createPreference(@RequestBody UMPreferenceMPDto umPreferenceMPDto);

}
