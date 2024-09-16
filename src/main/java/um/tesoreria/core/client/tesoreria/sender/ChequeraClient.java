package um.tesoreria.core.client.tesoreria.sender;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tesoreria-sender-service/api/tesoreria/sender/chequera")
public interface ChequeraClient {

    @GetMapping("/generatePdf/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
    Resource generatePdf(@PathVariable("facultadId") Integer facultadId,
                                         @PathVariable("tipoChequeraId") Integer tipoChequeraId,
                                         @PathVariable("chequeraSerieId") Long chequeraSerieId,
                                         @PathVariable("alternativaId") Integer alternativaId);

    @GetMapping("/generatePdf/completa/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
    Resource generatePdfCompleta(@PathVariable("facultadId") Integer facultadId,
                                                 @PathVariable("tipoChequeraId") Integer tipoChequeraId,
                                                 @PathVariable("chequeraSerieId") Long chequeraSerieId,
                                                 @PathVariable("alternativaId") Integer alternativaId);

    @GetMapping("/generatePdf/reemplazo/completa/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
    Resource generatePdfReemplazoCompleta(@PathVariable("facultadId") Integer facultadId,
                                                          @PathVariable("tipoChequeraId") Integer tipoChequeraId,
                                                          @PathVariable("chequeraSerieId") Long chequeraSerieId,
                                                          @PathVariable("alternativaId") Integer alternativaId);

    @GetMapping("/generateCuotaPdf/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}/{productoId}/{cuotaId}")
    Resource generateCuotaPdf(@PathVariable("facultadId") Integer facultadId,
                                              @PathVariable("tipoChequeraId") Integer tipoChequeraId,
                                              @PathVariable("chequeraSerieId") Long chequeraSerieId,
                                              @PathVariable("alternativaId") Integer alternativaId,
                                              @PathVariable("productoId") Integer productoId,
                                              @PathVariable("cuotaId") Integer cuotaId);

    @GetMapping("/sendChequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}/{copiaInformes}")
    String sendChequera(@PathVariable("facultadId") Integer facultadId,
                                        @PathVariable("tipoChequeraId") Integer tipoChequeraId,
                                        @PathVariable("chequeraSerieId") Long chequeraSerieId,
                                        @PathVariable("alternativaId") Integer alternativaId,
                                        @PathVariable("copiaInformes") Boolean copiaInformes);

    @GetMapping("/sendCuota/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}/{productoId}/{cuotaId}/{copiaInformes}")
    String sendCuota(@PathVariable("facultadId") Integer facultadId,
                                     @PathVariable("tipoChequeraId") Integer tipoChequeraId,
                                     @PathVariable("chequeraSerieId") Long chequeraSerieId,
                                     @PathVariable("alternativaId") Integer alternativaId,
                                     @PathVariable("productoId") Integer productoId,
                                     @PathVariable("cuotaId") Integer cuotaId,
                                     @PathVariable("copiaInformes") Boolean copiaInformes);

}
