package um.tesoreria.core.client.tesoreria.sender;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tesoreria-sender-service", contextId = "chequeraClient", path = "/api/tesoreria/sender/chequera")
public interface ChequeraClient {

    @GetMapping("/generatePdf/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
    Resource generatePdf(@PathVariable Integer facultadId,
                         @PathVariable Integer tipoChequeraId,
                         @PathVariable Long chequeraSerieId,
                         @PathVariable Integer alternativaId);

    @GetMapping("/generatePdf/completa/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
    Resource generatePdfCompleta(@PathVariable Integer facultadId,
                                 @PathVariable Integer tipoChequeraId,
                                 @PathVariable Long chequeraSerieId,
                                 @PathVariable Integer alternativaId);

    @GetMapping("/generatePdf/reemplazo/completa/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
    Resource generatePdfReemplazoCompleta(@PathVariable Integer facultadId,
                                          @PathVariable Integer tipoChequeraId,
                                          @PathVariable Long chequeraSerieId,
                                          @PathVariable Integer alternativaId);

    @GetMapping("/generateCuotaPdf/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}/{productoId}/{cuotaId}")
    Resource generateCuotaPdf(@PathVariable Integer facultadId,
                              @PathVariable Integer tipoChequeraId,
                              @PathVariable Long chequeraSerieId,
                              @PathVariable Integer alternativaId,
                              @PathVariable Integer productoId,
                              @PathVariable Integer cuotaId);

    @GetMapping("/sendChequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}/{copiaInformes}/{codigoBarras}")
    String sendChequera(@PathVariable Integer facultadId,
                        @PathVariable Integer tipoChequeraId,
                        @PathVariable Long chequeraSerieId,
                        @PathVariable Integer alternativaId,
                        @PathVariable Boolean copiaInformes,
                        @PathVariable Boolean codigoBarras);

    @GetMapping("/sendCuota/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}/{productoId}/{cuotaId}/{copiaInformes}")
    String sendCuota(@PathVariable Integer facultadId,
                     @PathVariable Integer tipoChequeraId,
                     @PathVariable Long chequeraSerieId,
                     @PathVariable Integer alternativaId,
                     @PathVariable Integer productoId,
                     @PathVariable Integer cuotaId,
                     @PathVariable Boolean copiaInformes);

}
