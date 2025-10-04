# Diagramas de Documentación

Este directorio contiene los diagramas Mermaid generados automáticamente para la documentación del servicio:

- `dependencies.mmd`: Diagrama de dependencias del proyecto.
- `architecture.mmd`: Arquitectura general (controladores, servicios, repositorios, entidades).
- `hexagonal-architecture.mmd`: Arquitectura hexagonal implementada en el caso de uso Curso Cargo Contratado.
- `entities.mmd`: Diagrama entidad-relación (ER) de los principales modelos de dominio.
- `deployment.mmd`: Diagrama de despliegue del microservicio.
- `sequence-example.mmd`: Ejemplo de diagrama de secuencia para una petición típica.

## Notas sobre el pipeline
- El pipeline de documentación debe validar la sintaxis Mermaid antes de publicar.
- Si un archivo de diagrama falta o tiene error de sintaxis, debe mostrar una advertencia clara pero no fallar el build.
- Los diagramas pueden ser visualizados en la documentación generada automáticamente.
