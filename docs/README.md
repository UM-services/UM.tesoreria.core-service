# Diagramas de Documentación

**Versión actual del servicio: 3.23.0**

Este directorio contiene los diagramas Mermaid generados automáticamente para la documentación del servicio:

## Diagramas de Arquitectura
- `architecture.mmd`: Arquitectura general (controladores, servicios, repositorios, entidades).
- `hexagonal-architecture.mmd`: Arquitectura hexagonal implementada en el caso de uso Curso Cargo Contratado.
- `hexagonal-articulo.mmd`: Arquitectura hexagonal del módulo Artículo (gestión de artículos) - v3.13.0.
- `hexagonal-chequeraCuota.mmd`: Arquitectura hexagonal del caso de uso Chequera Cuota (flujo de pago de cuotas).
- `hexagonal-auth.mmd`: Arquitectura hexagonal del módulo Auth (autenticación de usuarios).
- `hexagonal-geografica.mmd`: Arquitectura hexagonal del módulo Geografica (entidades geográficas).
- `hexagonal-proveedor.mmd`: Arquitectura hexagonal del módulo Proveedor (gestión de proveedores).
- `hexagonal-cuenta.mmd`: Arquitectura hexagonal del módulo Cuenta (gestión de cuentas contables) - v3.8.0.
- `hexagonal-mercadopago-context-history.mmd`: Historial de contexto de MercadoPago.
- `hexagonal-ubicacion.mmd`: Arquitectura hexagonal del módulo Ubicacion (gestión de ubicaciones) - v3.14.0.
- `hexagonal-ubicacionArticulo.mmd`: Arquitectura hexagonal del módulo UbicacionArticulo (gestión de ubicaciones de artículos) - v3.14.0.
- `hexagonal-dependencia.mmd`: Arquitectura hexagonal del módulo Dependencia (gestión de dependencias) - v3.17.0.
- `hexagonal-facturaPendiente.mmd`: Arquitectura hexagonal del módulo FacturaPendiente (gestión de facturas pendientes) - v3.15.0.
- `hexagonal-facultad.mmd`: Arquitectura hexagonal del módulo Facultad (gestión de facultades) - v3.18.0.
- `hexagonal-contrato.mmd`: Arquitectura hexagonal del módulo Contrato (gestión de contratos) - v3.19.0.
- `hexagonal-chequeraSerie.mmd`: Arquitectura hexagonal del módulo ChequeraSerie (gestión de series de chequeras) - v3.21.0.
- `hexagonal-baja.mmd`: Arquitectura hexagonal del módulo Baja (gestión de bajas de chequeras) - v3.21.0.
- `hexagonal-campanha.mmd`: Arquitectura hexagonal del módulo Campanha (gestión de campañas UM Hub) - v3.23.0.
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
