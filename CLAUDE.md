# CLAUDE.md

El contexto completo para agentes de IA de este repositorio vive en **[AGENTS.md](./AGENTS.md)**. Leelo primero y entero antes de tocar código: tiene el mapa del proyecto, comandos, reglas de los slices hexagonales, tabla de rutas REST y el flujo de release.

Reglas mínimas (el detalle está en AGENTS.md):

1. Desarrollo nuevo → siempre a un slice de `src/main/java/um/tesoreria/core/hexagonal/...`; no agregar features a la capa legacy ni a `kotlin/`.
2. Estructura y pureza de slices → seguir estrictamente la skill `hexagonal-arch` (necesidad cross-slice: **detenerse y preguntar al usuario**).
3. `domain/` sin frameworks; `application/` importa solo `domain/`; controladores hablan DTOs (`Request`/`Response`), nunca `{X}Entity`.
4. Cambiar una URL pública o un campo de un DTO de respuesta es breaking → major SemVer + migración documentada del consumidor (ver AGENTS.md §5/§7).
5. Al liberar versión: checklist de `release-documentation` (pom + CHANGELOG + README + docs/README + diagramas `.mmd`).
