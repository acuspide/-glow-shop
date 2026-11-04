# Glow Shop

**Marketplace de productos de belleza** (maquillaje y cuidado facial, corporal y capilar).
Varias tiendas venden sus artículos en un mismo sitio; la clienta compra, paga, recibe su pedido y califica lo que compró.

**Problema que resuelve:** las tiendas pequeñas de belleza no tienen un canal propio para vender en línea, y a la clienta le cuesta encontrar productos adecuados para su tipo de piel y tono. Glow Shop reúne varias tiendas en un catálogo con filtros por tipo de piel, tono, categoría y marca, y propone rutinas de cuidado.

Proyecto del curso **Programación Avanzada** · Universidad del Quindío · Docente: Valentina Oviedo Sánchez
Entrega 2: backend completo (Spring Boot 4.1.1, Java 21, arquitectura hexagonal).

---

## Integrantes

| Integrante | GitHub |
|---|---|
| Alejandro Cubides | [@acuspide](https://github.com/acuspide) |
| Yeraldin Noguera | [@Yeral30](https://github.com/Yeral30) |
| Yull Vanegas | [@yullvanegas](https://github.com/yullvanegas) |

---

## Cómo ejecutar

Requisitos: **Java 21**. No hace falta instalar Gradle ni una base de datos (se usa el *wrapper* de Gradle y H2 en memoria).

```bash
cd backend/ecommerce

# Windows
.\gradlew.bat bootRun

# Linux / macOS
./gradlew bootRun
```

| Qué | Dónde |
|---|---|
| API | http://localhost:8080 |
| Swagger (documentación y pruebas) | http://localhost:8080/swagger-ui.html |
| Consola de la base de datos H2 | http://localhost:8080/h2-console |

Datos para entrar a la consola H2: **JDBC URL** `jdbc:h2:mem:glowshop` · **Usuario** `sa` · **Contraseña** (vacía).

Correr las pruebas:

```bash
cd backend/ecommerce
.\gradlew.bat clean test      # Windows
./gradlew clean test          # Linux / macOS
```

### Usar el token en Swagger

1. Ejecutar `POST /api/auth/login` con el correo y la contraseña.
2. Copiar el `token` de la respuesta.
3. Clic en **Authorize** (arriba a la derecha) y pegar el token.
4. Desde ese momento Swagger envía `Authorization: Bearer <token>` en cada petición.

---

## Usuarios y datos de prueba

Al arrancar se cargan datos de prueba desde `backend/ecommerce/src/main/resources/data.sql` (tiendas, categorías, marcas, 10 artículos, pedidos, pagos y calificaciones). Las contraseñas están guardadas con **BCrypt**.

| Rol | Correo | Contraseña | Qué tiene |
|---|---|---|---|
| ADMINISTRADOR | `admin@glowshop.com` | `Admin12345` | Lo crea la aplicación al arrancar (`application.properties`) |
| VENDEDOR | `vera@glow.com` | `Clave12345` | Tienda **Glow Natural** (ACTIVA), 5 artículos, uno con stock bajo y uno eliminado |
| VENDEDOR | `lina@glow.com` | `Clave12345` | Tienda **Lina Beauty** (ACTIVA), 5 artículos |
| VENDEDOR | `sofia@glow.com` | `Clave12345` | Tienda **Rosa Cosméticos** PENDIENTE (para probar "aprobar tienda") |
| CLIENTE | `ana@correo.com` | `Clave12345` | 2 pedidos entregados y 1 confirmado sin pagar (`ped-005`) |
| CLIENTE | `carlos@correo.com` | `Clave12345` | 1 pedido entregado y 1 enviado |

Ids útiles para Swagger: artículos `art-labial-mate`, `art-base-fit`, `art-protector`…; categorías `cat-labios`, `cat-rostro`…; marcas `marca-maybelline`, `marca-nivea`…; tiendas `tienda-glow-natural`, `tienda-lina-beauty`, `tienda-rosa`.

Cuerpo de ejemplo para registrar un usuario nuevo (`POST /api/usuarios` o `/api/usuarios/vendedores`):

```json
{
  "nombre": "María",
  "email": "maria@correo.com",
  "contrasena": "Clave12345",
  "telefono": "3001234567",
  "fechaNacimiento": "1998-05-10"
}
```

---

## Roles y permisos

| Rol | Qué puede hacer |
|---|---|
| **CLIENTE** | Carrito, confirmar compra, pagar, consultar y listar sus pedidos, calificar artículos que ya recibió |
| **VENDEDOR** | Registrar su tienda, publicar / actualizar / eliminar artículos de su tienda, manejar su inventario, ver y despachar los pedidos de su tienda |
| **ADMINISTRADOR** | Aprobar tiendas, crear categorías, marcas y rutinas de cuidado, crear y desactivar usuarios |

- Sin token → **401**. Con token pero sin permiso (rol equivocado o recurso de otra tienda / otro cliente) → **403**.
- Los permisos se verifican en el **dominio** (`Usuario.verificarQueEsAdministradorActivo`, `Tienda.verificarQueEsDe`, `Pedido.verificarQuePerteneceA`…), no en el controller.

---

## Endpoints principales

🔒 = requiere token.

### Autenticación y usuarios
| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/usuarios` | Registrar cliente |
| POST | `/api/usuarios/vendedores` | Registrar vendedor |
| POST | `/api/auth/login` | Iniciar sesión (devuelve el JWT) |
| POST | `/api/auth/logout` 🔒 | Cerrar sesión (revoca el token) |
| POST | `/api/auth/recuperar-contrasena` | Solicitar código de recuperación |
| POST | `/api/auth/restablecer-contrasena` | Restablecer contraseña con el código |
| GET / PUT | `/api/usuarios/me` 🔒 | Consultar / actualizar mi perfil |
| PUT | `/api/usuarios/me/contrasena` 🔒 | Cambiar contraseña |

### Administración 🔒 (solo ADMINISTRADOR)
| Método | Ruta | Descripción |
|---|---|---|
| PUT | `/api/admin/tiendas/{id}/aprobar` | Aprobar una tienda pendiente |
| POST | `/api/categorias` | Crear categoría |
| POST | `/api/marcas` | Crear marca |
| GET / POST | `/api/admin/usuarios` | Listar / crear usuarios |
| PUT | `/api/admin/usuarios/{id}/desactivar` | Desactivar usuario |

### Tiendas, artículos e inventario
| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/tiendas` 🔒 | Registrar mi tienda (queda PENDIENTE hasta que el admin la apruebe) |
| GET | `/api/articulos` | Listar artículos publicados |
| GET | `/api/articulos/{id}` | Detalle de un artículo |
| GET | `/api/buscarArticulos?page=0&size=10` | Búsqueda **paginada** (JPQL) por texto, categoría, marca, tienda, rango de precio, tono o tipo de piel. Devuelve `content`, `totalElements` y `totalPages`; no muestra eliminados |
| POST | `/api/articulos` 🔒 | Publicar artículo (vendedor con tienda ACTIVA) |
| PUT | `/api/articulos/{id}` 🔒 | Actualizar artículo (vendedor dueño) |
| DELETE | `/api/articulos/{id}` 🔒 | Eliminar artículo — eliminación lógica (vendedor dueño) |
| POST | `/api/articulos/{id}/inventario/aumentar` 🔒 | Aumentar inventario (vendedor dueño) |
| POST | `/api/articulos/{id}/inventario/disminuir` 🔒 | Disminuir inventario (vendedor dueño) |
| GET | `/api/inventario` 🔒 | Inventario de mi tienda |
| GET | `/api/inventario/stock-bajo` 🔒 | Artículos de mi tienda con menos de 5 unidades |

### Reportes
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/reportes/articulos-mas-vendidos?limite=5` | Ranking de artículos más vendidos (JPQL con JOIN, GROUP BY y SUM sobre los pedidos entregados) |

### Carrito, pedidos y pagos 🔒
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/carrito` | Ver carrito |
| POST | `/api/carrito/items` | Agregar artículo al carrito |
| PUT / DELETE | `/api/carrito/items/{articuloId}` | Cambiar cantidad / quitar artículo |
| DELETE | `/api/carrito/items` | Vaciar carrito |
| GET | `/api/carrito/disponibilidad` | Validar que todo lo del carrito esté disponible |
| POST | `/api/pedidos` | Confirmar compra (crea **un pedido por tienda**) |
| GET | `/api/pedidos` | Listar mis pedidos |
| GET | `/api/pedidos/{id}` | Consultar un pedido mío |
| POST | `/api/pedidos/{pedidoId}/pagos` | Pagar un pedido |
| POST | `/api/pagos/{id}/reintentos` | Reintentar un pago rechazado |
| GET | `/api/pagos` · `/api/pagos/{id}` | Historial de pagos / estado de un pago |
| GET | `/api/vendedor/pedidos` | (Vendedor) Pedidos que recibió mi tienda |
| PUT | `/api/vendedor/pedidos/{id}/estado` | (Vendedor) Avanzar el pedido: EN_PREPARACION → ENVIADO → ENTREGADO |

### Calificaciones y rutinas
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/articulos/{id}/calificaciones` | Ver calificaciones de un artículo |
| POST | `/api/articulos/{id}/calificaciones` 🔒 | Calificar (solo si la clienta ya recibió el artículo) |
| PUT / DELETE | `/api/calificaciones/{id}` 🔒 | Editar / eliminar mi calificación (eliminación lógica) |
| GET | `/api/rutinas` · `/api/rutinas/{id}` | Listar rutinas / ver una rutina |
| POST | `/api/rutinas` 🔒 | Crear rutina (administrador) |
| PUT | `/api/rutinas/{id}` 🔒 | Editar rutina (administrador) |
| POST | `/api/rutinas/{id}/articulos/{articuloId}` 🔒 | Agregar artículo a una rutina (administrador) |

---

## Flujo de punta a punta (para probar en Swagger)

Con los datos de prueba se puede empezar directamente:

1. **Vera** (`vera@glow.com`) inicia sesión y publica un artículo en `tienda-glow-natural`.
2. **Ana** (`ana@correo.com`) inicia sesión, agrega el artículo al carrito y confirma la compra.
3. **Ana** paga el pedido.
4. **Vera** lista los pedidos de su tienda y lo avanza: EN_PREPARACION → ENVIADO → ENTREGADO.
5. **Ana** califica el artículo recibido.

Errores de negocio para mostrar: **Sofía** publica con su tienda PENDIENTE (422), Ana califica un artículo que todavía no ha recibido (422), Vera pasa `ped-005` de CONFIRMADO directo a ENTREGADO (422), Ana califica dos veces el mismo artículo (409) o Vera intenta eliminar un artículo de Lina (403).

---

## Arquitectura

Arquitectura hexagonal en `backend/ecommerce/src/main/java/com/example/ecommerce`:

| Capa | Paquete | Qué contiene |
|---|---|---|
| Dominio | `domain` | Entidades, value objects, servicios de dominio, excepciones e interfaces de repositorio. **Sin imports de Spring ni JPA.** |
| Aplicación | `application` | Casos de uso (coordinan: buscan en el repositorio, piden al dominio la operación y guardan), DTOs y puertos (`TokenService`, `PasswordHasher`) |
| Infraestructura | `infrastructure` | Controllers REST y sus mappers, manejo global de errores, adaptadores JPA (`persistence/jpa`), seguridad (JWT) y configuración |

- **Agregados:** `Articulo` y `Pedido` (diagramas en `Docs/`).
- **Persistencia:** cada agregado tiene su entidad JPA separada, un mapper y un repositorio JPA que implementa la interfaz del dominio. Los repositorios en memoria se usan en las pruebas unitarias. Las eliminaciones son lógicas.
- **Seguridad:** JWT propio firmado con HMAC-SHA256. El payload lleva el id del usuario (`sub`), el correo, el rol y la fecha de vencimiento (`exp`); nunca la contraseña. La clave secreta y la duración del token están en `application.properties` (`glow.jwt.secret`, `glow.jwt.expiracion-minutos`). Las contraseñas se guardan con BCrypt.
- **Errores:** `GlobalExceptionHandler` devuelve JSON con el mensaje: 400 datos inválidos o parámetros fuera de rango, 401 sin sesión o token inválido, 403 sin permiso, 404 no existe, 409 duplicado, 422 regla de negocio rota.

Documentación de diseño (Entrega 1) en la carpeta [`Docs/`](Docs): glosario del lenguaje ubicuo, reglas de negocio, funcionalidades y diagramas de los agregados.

---

## Correcciones de la Entrega 1

| Observación que nos dieron | Qué cambiamos | Dónde se ve (archivo) |
|---|---|---|
| Las entidades tenían constructores públicos: se podía crear un objeto sin pasar por las reglas | Constructores **privados**; los objetos nuevos se crean con un método de fábrica que valida (`crear`, `registrarCliente`…) y los que vienen de la base de datos con `reconstruir`. Los casos de uso y controllers dejaron de tener constructores escritos a mano y usan `@RequiredArgsConstructor` | `Usuario.java`, `Articulo.java`, `Pedido.java`, `Tienda.java` y los casos de uso en `application/usecase/` |
| Publicar artículo tenía dos métodos `ejecutar` (uno recibía los objetos armados y otro un artículo completo que se publicaba sin validar) | Quedó un solo `ejecutar` que recibe ids; el caso de uso carga vendedor, tienda, categoría y marca de sus repositorios y la tienda verifica que sea del vendedor y esté activa | `PublicarArticuloUseCase.java` |
| Cuando algo no existía se lanzaba una excepción genérica (`orElseThrow()` vacío) | Excepción propia de dominio `RecursoNoEncontradoException`, que el manejador convierte en 404 con un mensaje claro | `domain/exception/RecursoNoEncontradoException.java`, `GlobalExceptionHandler.java` |
| Había decisiones de negocio (`if`) dentro de casos de uso | Se movieron al dominio: el pago decide si queda aprobado o rechazado y el pedido decide si lo registra | `Pago.registrarResultado`, `Pago.medioParaReintentar`, `Pedido.registrarSiFueAprobado` |

---

## Pruebas avanzadas

Todas corren con `./gradlew test` (patrón AAA).

| Punto | Prueba | Dónde |
|---|---|---|
| 0 · Repositorio | Guardar y recuperar cada agregado con sus value objects contra H2 (`@DataJpaTest`) | `infrastructure/persistence/jpa/*RepositoryJpaTest.java` |
| 0 · Paginación y ranking | Búsqueda paginada (totales, orden, filtros, sin eliminados) y ranking con cantidades y orden conocidos | `infrastructure/persistence/jpa/BusquedaYRankingRepositoryJpaTest.java` |
| 1 · Endpoint con seguridad | `GET /api/admin/usuarios`: sin token 401, token de cliente 403, token de administrador 200 (con login real) | `integracion/SeguridadEndpointTest.java` |
| 2 · `@DataJpaTest` | 8 pruebas de repositorio con `@DataJpaTest` + `@Import` del adaptador | `infrastructure/persistence/jpa/` |
| 3 · Integración | Compra de punta a punta sin simular nada: carrito → pedido → inventario descontado en la base de datos | `integracion/ComprarArticuloIntegracionTest.java` |
| 4 · Controller | 404 y regla de negocio rota (422) con `verify(pedidoRepository, never()).guardar(any())` | `infrastructure/rest/VendedorPedidoControllerErroresTest.java` |

### 1. Probar un endpoint con seguridad
Probamos la ruta `GET /api/admin/usuarios`, que solo puede usar el administrador. Hay cuatro casos: sin token responde 401, con el token de Ana (clienta) responde 403, con el token del administrador responde 200, y con un token al que le cambiamos la firma responde 401. Como no usamos Spring Security, no podíamos usar `jwt()` de spring-security-test para inventar un token. Por eso levantamos la aplicación completa con `@SpringBootTest` y `@AutoConfigureMockMvc`, y en cada prueba hacemos un login de verdad con `POST /api/auth/login` usando los usuarios de `data.sql`. Así se prueba todo el camino: el login, la firma del token, la lectura del token y la regla de permisos del dominio. Lo que más nos costó fue entender por qué el 401 y el 403 salen de lugares distintos. El 401 sale del servicio de tokens, cuando el token falta, está mal firmado o ya venció. El 403 sale del dominio, cuando el usuario es válido pero su rol no le permite hacer la acción.

### 2. `@DataJpaTest` contra `@SpringBootTest`
`@SpringBootTest` levanta toda la aplicación: controllers, casos de uso, repositorios y la base de datos. `@DataJpaTest` solo levanta la parte de JPA, es decir, las entidades, los repositorios de Spring Data y una base H2. Por eso es más rápido y sirve para probar solo las consultas. Como nuestros adaptadores (`ArticuloRepositoryJpa`, `PedidoRepositoryJpa`) y los mappers son clases propias, `@DataJpaTest` no los carga solo, y hay que traerlos con `@Import`. En Spring Boot 4 la anotación cambió de paquete: ahora es `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`, y además hay que agregar la dependencia `spring-boot-starter-data-jpa-test`. También nos pasó que `@DataJpaTest` carga el `data.sql`. Algunas pruebas fallaron porque los nombres que usábamos ya existían en los datos de prueba y los conteos no coincidían, así que tuvimos que cambiar esos nombres y ajustar los números esperados.

### 3. Prueba de integración
Una prueba unitaria revisa una sola clase y simula todo lo demás con mocks. En cambio, `ComprarArticuloIntegracionTest` no simula nada: Ana agrega un labial al carrito, confirma el pedido, y la prueba revisa en la base de datos que el pedido quedó creado y que el inventario bajó. Otra prueba revisa que la vendedora no pueda pasar un pedido de CONFIRMADO a ENTREGADO y que el pedido quede igual. Usamos `@Transactional` para que al final se deshaga todo y una prueba no afecte a la otra. La ventaja es que encuentra errores que las unitarias no ven, como un mapper que guarda mal un dato o una transacción que no se cierra. La desventaja es que es más lenta y, cuando falla, cuesta más saber en qué parte está el problema. Por eso conviene usarlas para los flujos más importantes, como la compra, y dejar las reglas pequeñas para las pruebas unitarias.
