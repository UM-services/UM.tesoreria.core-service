# Diagramas de Documentación

**Versión actual del servicio: 3.3.1**

Este directorio contiene los diagramas Mermaid generados automáticamente para la documentación del servicio:

## Diagramas de Arquitectura
- `architecture.mmd`: Arquitectura general (controladores, servicios, repositorios, entidades).
- `hexagonal-architecture.mmd`: Arquitectura hexagonal implementada en el caso de uso Curso Cargo Contratado.
- `hexagonal-chequeraCuota.mmd`: Arquitectura hexagonal del caso de uso Chequera Cuota (flujo de pago de cuotas).
- `hexagonal-mercadopago-context-history.mmd`: Historial de contexto de MercadoPago.
- `deployment.mmd`: Diagrama de despliegue del microservicio.

## Diagramas de Datos
- `dependencies.mmd`: Diagrama de dependencias del proyecto.
- `entities.mmd`: Diagrama entidad-relación (ER) de los principales modelos de dominio.

## Diagramas de Secuencia
- `sequence-example.mmd`: Ejemplo de diagrama de secuencia para una petición típica.
- `sequence-alta-usuario.mmd`: Alta de usuario.
- `sequence-baja-usuario.mmd`: Baja de usuario.
- `sequence-chequera-serie-sede.mmd`: Chequera serie sede.
- `sequence-comprobante.mmd`: Comprobante.
- `sequence-consulta-articulos.mmd`: Consulta de artículos.
- `sequence-liquidacion-sueldos.mmd`: Liquidación de sueldos.
- `sequence-mercadopago-to-change.mmd`: MercadoPago to change.
- `sequence-movimientos-cuenta.mmd`: Movimientos de cuenta.
- `sequence-pago-chequera.mmd`: Pago de chequera.
- `sequence-reemplazo-chequera.mmd`: Reemplazo de chequera.

## Notas sobre el pipeline
- El pipeline de documentación debe validar la sintaxis Mermaid antes de publicar.
- Si un archivo de diagrama falta o tiene error de sintaxis, debe mostrar una advertencia clara pero no fallar el build.
- Los diagramas pueden ser visualizados en la documentación generada automáticamente.
