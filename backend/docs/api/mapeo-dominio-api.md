| Operacion del dominio | Método HTTP | Endpoint | 
|---|---|---| 
| crear rutina de cuidado | POST | /rutinas |
| Listar rutinas de cuidado | GET | /rutinas |
| consultar una rutina de cuidado | GET | /rutinas/{id} |
| Actualizar rutina de cuidado | PUT | /api/rutinas/{id}|
| Agregar artículo a una rutina de cuidado | POST | /api/rutinas/{id}/articulos/{articuloId}|
| listar artículos disponibles | GET| /articulos |
| publicar articulo | POST | /articulos |
| Consultar detalle de un artículo | GET | /api/articulos/{id} |
| Eliminar artículo (eliminación lógica) | DELETE | /api/articulos/{id} |
| Consultar inventario completo | GET | /api/inventario |
| Consultar inventario de un artículo | GET | /api/articulos/{id}/inventario |
| Aumentar inventario de un artículo | POST | /api/articulos/{id}/inventario/aumentar |
| Disminuir inventario de un artículo | POST | /api/articulos/{id}/inventario/disminuir |
| Buscar artículos aplicando filtros opcionales | GET | /api/buscarArticulos |
| Alertar stock bajo | GET | /api/inventario/stock-bajo |

# Mapeo Dominio -> API - Carrito

| Operacion del dominio | Método HTTP | Endpoint |
|---|---|---|
| Agregar artículo al carrito | POST | /api/carrito/items |
| Ver carrito | GET | /api/carrito |
| Vaciar carrito | DELETE | /api/carrito/items |
| Quitar artículo del carrito | DELETE | /api/carrito/items/{articuloId} |
| Modificar cantidad en el carrito | PUT | /api/carrito/items/{articuloId} |
| Validar disponibilidad del carrito | GET | /api/carrito/disponibilidad |
El carrito es siempre el del cliente con sesión iniciada: se identifica con `Authorization: Bearer <token>` (sin token → 401).
# Mapeo Dominio -> API - Pedido

| Operacion del dominio | Método HTTP | Endpoint |
|---|---|---|
| Confirmar pedido | POST | /api/pedidos |

El pedido se arma con los artículos del carrito del cliente con sesión iniciada (`Authorization: Bearer <token>`). Antes de confirmarlo se valida la disponibilidad del carrito (stock y precio vigentes); si algo falla, no se modifica nada.

# Mapeo Dominio -> API - Usuario

Regla de negocio (RN03): clientes y vendedores se registran solos; solo un administrador puede crear otros administradores (también puede crear vendedores). Un vendedor necesita que un administrador apruebe su tienda antes de publicar (RN05, RN06).

| Operacion del dominio | Método HTTP | Endpoint | Respuesta exitosa | Estado |
|---|---|---|---|---|
| Usuario.registrarCliente (autorregistro) | POST | /api/usuarios | 201 Created + Location | Hecho |
| Usuario.registrarVendedor (autorregistro) | POST | /api/usuarios/vendedores | 201 Created + Location | Hecho |
| autenticarUsuario | POST | /api/auth/login | 200 OK | Hecho |
| administrador.crearUsuarioConRol (vendedor o administrador) | POST | /api/admin/usuarios | 201 Created + Location | Hecho (requiere JWT de administrador) |

# Mapeo Dominio -> API - Pago

| Operacion del dominio | Método HTTP | Endpoint |
|---|---|---|
| Procesar pago de pedido | POST | /api/pedidos/{pedidoId}/pagos |
| Consultar estado de pago | GET | /api/pagos/{id} |
| Reintentar pago | POST | /api/pagos/{id}/reintentos |
| Listar historial de pagos | GET | /api/pagos |

El monto se toma del total del pedido. Un pago rechazado por la pasarela (por ejemplo, por fondos insuficientes) también responde 201: el pago queda registrado con su estado y su motivo.

## Errores

| Situación | Excepción de dominio | Código HTTP |
|---|---|---|
| Datos inválidos (formato de correo, nombre vacío, contraseña vacía) | Validación del DTO / ReglaDominioException | 400 |
| Contraseña débil (menos de 8 caracteres, sin mayúscula o sin número) | ContrasenaDebilException | 400 |
| Correo ya registrado (RN01) | CorreoElectronicoDuplicadoException | 409 |
| Correo o contraseña incorrectos | CredencialesInvalidasException | 401 |
| Usuario inactivo | UsuarioInactivoException | 403 |
| Quien intenta crear un vendedor o administrador no es un administrador activo | CreacionUsuarioNoPermitidaException | 403 |


## Usuario: teléfono, fecha de nacimiento y sesión (JWT)

| Método | Ruta | Quién | Cuerpo | Respuesta |
|--------|------|-------|--------|-----------|
| POST | `/api/usuarios` | Público | nombre, email, contrasena, telefono, fechaNacimiento | 201 + `UsuarioResponse` (rol CLIENTE) |
| POST | `/api/usuarios/vendedores` | Público | nombre, email, contrasena, telefono, fechaNacimiento | 201 + `UsuarioResponse` (rol VENDEDOR) |
| POST | `/api/auth/login` | Público | email, contrasena | 200 + `{token, tipo:"Bearer", usuario}` |
| POST | `/api/admin/usuarios` | Administrador (`Authorization: Bearer <token>`) | nombre, email, contrasena, telefono, fechaNacimiento, rol (VENDEDOR o ADMINISTRADOR) | 201 + `UsuarioResponse` |

Errores adicionales:

| Situación | Excepción | HTTP |
|-----------|-----------|------|
| Teléfono inválido (7-15 dígitos, `+` opcional) | `TelefonoInvalidoException` | 400 |
| Fecha de nacimiento nula, futura o anterior a 1900 | `FechaNacimientoInvalidaException` | 400 |
| Token ausente, alterado o expirado | `TokenInvalidoException` | 401 |
| Quien pide no es administrador activo | `CreacionUsuarioNoPermitidaException` | 403 |

Los ids de usuario son `String` (UUID generado en el controller) y el `sub` del JWT es ese UUID.
En el login se verifica la contraseña antes que el estado del usuario: con contraseña incorrecta la respuesta es siempre 401, aunque la cuenta esté desactivada.

El primer administrador se crea al arrancar (`glow.admin.*` en `application.properties`; en producción usar `GLOW_ADMIN_EMAIL`, `GLOW_ADMIN_PASSWORD` y `GLOW_JWT_SECRET`).
