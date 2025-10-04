package um.tesoreria.core.client.tesoreria.mercadopago;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tesoreria-mercadopago-service", contextId = "preferenceClient", path = "/api/tesoreria/mercadopago/preference")
public interface PreferenceClient {

    @GetMapping("/create/{chequeraCuotaId}")
    String createPreference(@PathVariable Long chequeraCuotaId);

}
