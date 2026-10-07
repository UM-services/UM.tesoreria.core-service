# Gastos: artículo, ubicación y baja segura (#405)

Guía para consumidores (web Angular, VB6 mientras conviva) de las escrituras de `articulo` y `ubicacionArticulo`
desde la versión **7.0.0**. Sub-issue de la migración de Gestión VB6 (#403).

Los ejemplos se ejecutaron contra la base de desarrollo el 2026-10-07 con el id reservado `999001` (los datos se
borraron después). Las URLs canónicas llevan barra final en las altas: `POST /articulo/` y `POST /ubicacionArticulo/`.
Ambos controladores responden también bajo `/api/tesoreria/core/...`.

## Errores: un solo formato

Todo error responde `application/problem+json` (`ProblemDetail`) con `detail` en español y un `codigo` estable para
decidir en el cliente. Si el error es de un campo del cuerpo, viene también `campo` (nombre JSON del DTO).

| HTTP | `codigo` | Cuándo |
|---|---|---|
| 400 | `CAMPO_INVALIDO` | Un campo no cumple una regla o referencia algo que no existe (`campo` dice cuál) |
| 400 | `CUERPO_INVALIDO` | JSON mal formado o un valor que no entra en el tipo (por ejemplo `habilitado: 300`) |
| 400 | `PARAMETRO_INVALIDO` | Un parámetro de la ruta no es del tipo esperado (`/articulo/abc`) |
| 404 | `ARTICULO_NO_ENCONTRADO`, `UBICACION_ARTICULO_NO_ENCONTRADO` | No existe |
| 409 | `ARTICULO_ID_DUPLICADO` | Alta con un id que ya existe |
| 409 | `ARTICULO_REFERENCIADO` | Baja de un artículo en uso; trae `referencias` |
| 409 | `CONFLICTO` | Otra operación tiene tomada la fila; reintentar en unos segundos |
| 415 | `TIPO_DE_CONTENIDO_NO_SOPORTADO` | Falta `Content-Type: application/json` |
| 500 | `ERROR_INTERNO` | Sin SQL ni trazas en la respuesta; el detalle queda en el log del servicio |

```
$ curl -s -H 'Content-Type: text/plain' -X POST /articulo/ -d 'hola'
{"detail":"El cuerpo tiene que enviarse como application/json.","instance":"/articulo/","status":415,
 "title":"Unsupported Media Type","codigo":"TIPO_DE_CONTENIDO_NO_SOPORTADO"}
```

## Alta de artículo: `POST /articulo/`

1. Pedir un id candidato con `GET /articulo/new` (último id + 1). Ese DTO trae `tipo: ""` y `habilitado: 0`: el
   cliente tiene que completar al menos `tipo: "gasto"` (o `"bien"`).
2. Enviar el alta con ese id.

```
$ curl -s /articulo/new
{"articuloId":465,"nombre":"","descripcion":"","unidad":"","precio":0,"inventariable":0,"stockMinimo":0,
 "numeroCuenta":null,"tipo":"","directo":0,"habilitado":0,"cuenta":null}

$ curl -s -H 'Content-Type: application/json' -X POST /articulo/ -d '{"articuloId":999001,"nombre":"Fotocopias",
  "descripcion":"","unidad":"UN","precio":10.505,"tipo":"gasto","directo":0,"habilitado":1,"numeroCuenta":10101010001}'
HTTP 201
{"articuloId":999001,"nombre":"Fotocopias","descripcion":"","unidad":"UN","precio":10.51,"inventariable":0,
 "stockMinimo":0,"numeroCuenta":10101010001,"tipo":"gasto","directo":0,"habilitado":1,"cuenta":null}
```

Reglas (las columnas de `articulos` en dev mandan):

- `articuloId` obligatorio, de 1 a 2147483647. `tipo` obligatorio: `bien` o `gasto`.
- `directo`, `habilitado`, `inventariable`: 0 o 1.
- Largos: `nombre` 150, `descripcion` 64, `unidad` 16. El nombre vacío se acepta (hay artículos así en dev).
- `precio`: hasta 14 dígitos enteros; los decimales de más se **redondean** a 2 como hacía MySQL (10.505 → 10.51).
- `numeroCuenta`: entero de hasta 11 dígitos que exista en el plan de cuentas; nulo = artículo sin cuenta.
- La respuesta del alta y de la edición trae `cuenta: null` aunque haya `numeroCuenta`; `GET /articulo/{id}` la completa.

**El alta nunca sobrescribe.** Un id existente responde 409 y la fila queda intacta:

```
$ curl -s -H 'Content-Type: application/json' -X POST /articulo/ -d '{"articuloId":999001,"nombre":"Otro","tipo":"gasto"}'
HTTP 409
{"detail":"El id ya existe. Si no estás seguro de que tu alta anterior se haya guardado, consultá GET /articulo/999001
 antes de crear con otro id.","status":409,"codigo":"ARTICULO_ID_DUPLICADO","campo":"articuloId", ...}
```

**Resultado incierto.** Si el alta se cortó (timeout, red) y al reintentar llega `ARTICULO_ID_DUPLICADO`, puede ser
que la primera sí se haya guardado o que otro usuario haya tomado el mismo id. Leer `GET /articulo/{id}`: si es el
artículo que se mandó, el alta anterior quedó; si no, pedir otro id con `/new` y repetir. Los ids sin usar no se
reservan y se pueden reutilizar.

```
$ curl -s -H 'Content-Type: application/json' -X POST /articulo/ -d '{"articuloId":999002,"nombre":"X","tipo":"servicio"}'
HTTP 400
{"detail":"tipo debe ser 'bien' o 'gasto'.","status":400,"codigo":"CAMPO_INVALIDO","campo":"tipo", ...}
```

## Edición: `PUT /articulo/{id}`

Un campo nulo o ausente significa **sin cambios**; `0` explícito se aplica. El id del cuerpo se ignora. `numeroCuenta`
no se puede vaciar por PUT. Gana la última escritura (no hay control de versión).

```
$ curl -s -H 'Content-Type: application/json' -X PUT /articulo/999001 -d '{"habilitado":0}'
HTTP 200
{"articuloId":999001,"nombre":"Fotocopias",...,"precio":10.51,...,"numeroCuenta":10101010001,"tipo":"gasto",
 "directo":0,"habilitado":0,"cuenta":null}

$ curl -s -H 'Content-Type: application/json' -X PUT /articulo/999001 -d '{"numeroCuenta":99999999998}'
HTTP 400
{"detail":"La cuenta indicada no existe en el plan de cuentas.","status":400,"codigo":"CAMPO_INVALIDO",
 "campo":"numeroCuenta", ...}
```

## Asignación de ubicación y cuenta: `POST /ubicacionArticulo/`

Idempotente por par `(ubicacionId, articuloId)`: si el vínculo existe se le reemplaza la cuenta (nula lo deja sin
cuenta) y conserva su `ubicacionArticuloId`; si no, se crea. Ubicación, artículo y cuenta (si viene) tienen que
existir; si falta alguno responde 400 con el primer campo que falla, en ese orden, y no se escribe nada. La respuesta
trae la ubicación, el artículo y la cuenta vigentes.

```
$ curl -s -H 'Content-Type: application/json' -X POST /ubicacionArticulo/ -d '{"ubicacionId":1,"articuloId":999001,
  "numeroCuenta":10101010001}'
HTTP 200
{"ubicacionArticuloId":1213,"ubicacionId":1,"articuloId":999001,"numeroCuenta":10101010001,
 "ubicacion":{"ubicacionId":1,"nombre":"RECTORADO",...},"articulo":{"articuloId":999001,...},
 "cuenta":{"numeroCuenta":10101010001,"nombre":"Fondo Fijo Tesorero",...}}

$ curl -s -H 'Content-Type: application/json' -X POST /ubicacionArticulo/ -d '{"ubicacionId":1,"articuloId":999001,
  "numeroCuenta":10101010002}'
HTTP 200
{"ubicacionArticuloId":1213,"ubicacionId":1,"articuloId":999001,"numeroCuenta":10101010002,...,
 "cuenta":{"numeroCuenta":10101010002,"nombre":"Fondo Fijo Intendente",...}}

$ curl -s -H 'Content-Type: application/json' -X POST /ubicacionArticulo/ -d '{"ubicacionId":32000,"articuloId":999001}'
HTTP 400
{"detail":"La ubicación indicada no existe.","status":400,"codigo":"CAMPO_INVALIDO","campo":"ubicacionId", ...}
```

Si dos pedidos insertan el mismo par a la vez, el que pierde se reintenta una vez solo y termina actualizando: queda
una fila.

### Recuperar un guardado a medias

La web guarda en dos llamadas (artículo y después asignación); no hay un endpoint único. Si la asignación falló
después de guardar el artículo, consultar el par exacto y, si no está o tiene otra cuenta, repetir la asignación
(es idempotente):

```
$ curl -s /ubicacionArticulo/1/999001
HTTP 200
{"ubicacionArticuloId":1213,"ubicacionId":1,"articuloId":999001,"numeroCuenta":10101010002,...}
```

`404 UBICACION_ARTICULO_NO_ENCONTRADO` significa que el vínculo no existe.

## Baja: `DELETE /articulo/{id}`

Solo se borra un artículo que **ninguna entrega ni línea de factura usa**. Se cuentan `entrega_detalle` y
`movprov_detallefactura` (esta última no tiene FK: antes de 7.0.0 la baja pasaba y dejaba líneas de factura
huérfanas). Si está en uso responde 409 con cada tabla y su cantidad, y no borra nada:

```
$ curl -s -X DELETE /articulo/48
HTTP 409
{"detail":"El artículo 48 está referenciado y no se puede borrar. Este servicio guarda habilitado pero no filtra por
 ese campo: si se usa habilitado = 0 como baja, la web y VB6 tienen que respetarlo.","status":409,
 "codigo":"ARTICULO_REFERENCIADO","referencias":[{"tabla":"movprov_detallefactura","cantidad":8}], ...}
```

Si está libre, se borran sus vínculos de `ubicacion_articulo` y el artículo en la misma transacción (204):

```
$ curl -s -X DELETE /articulo/999001
HTTP 204

$ curl -s /ubicacionArticulo/1/999001
HTTP 404
{"detail":"El artículo no está asignado a esa ubicación.","codigo":"UBICACION_ARTICULO_NO_ENCONTRADO", ...}
```

No hay baja lógica. `habilitado` se guarda, pero ninguna lectura del servicio filtra por ese campo.

## Historial (#404)

Cada escritura registra su evento en `gestion_escritura_historial` dentro de la misma transacción: si falla el
historial no queda la escritura, y al revés. Lo que dejaron los ejemplos de arriba:

| operacion | entidad | entidad_clave |
|---|---|---|
| ALTA | articulo | 999001 |
| EDICION | articulo | 999001 |
| ALTA | ubicacion_articulo | 1:999001 |
| EDICION | ubicacion_articulo | 1:999001 |
| BAJA | ubicacion_articulo | 1:999001 |
| BAJA | articulo | 999001 |

- Clave: `"{id}"` para artículo y `"{ubicacionId}:{articuloId}"` para el vínculo.
- Los valores guardan solo los campos de la fila (sin la cuenta ni las asociaciones). El "antes" de una edición o
  baja sale de la fila leída con bloqueo.
- Una edición o asignación que no cambia nada no escribe la fila y no se registra (los dos 400 de arriba tampoco).

**Requisito de despliegue:** sin la tabla `gestion_escritura_historial` (script
`docs/sql/V404__gestion_escritura_historial.sql`) **toda** escritura de artículo o vínculo falla y se revierte. Hoy
la tabla existe en desarrollo y no en producción.

## Concurrencia y límites conocidos

- Edición y baja bloquean la fila del artículo; la baja bloquea después sus vínculos. Ante un interbloqueo de MySQL
  la operación se reintenta una vez; si otra operación tiene la fila tomada más que la espera de MySQL, responde 409
  `CONFLICTO` sin reintentar.
- Mientras la baja tiene el artículo bloqueado, una entrega o un vínculo nuevos para ese artículo esperan y fallan
  (su FK lo lee). Una **línea de factura** nueva no espera (`movprov_detallefactura` no tiene FK): si VB6 la carga
  justo durante la baja, puede quedar huérfana. La baja no garantiza cero huérfanos frente a otros escritores.

## Decisiones provisorias (hasta revisar `frmGasto` de VB6)

- El nombre vacío se acepta.
- La baja borra los vínculos del artículo (cada uno con su evento de historial) en lugar de bloquearse por ellos.
- El chequeo de referencias se hace para cualquier tipo de artículo, no solo `gasto`.
- No hay endpoint único artículo + asignación.
