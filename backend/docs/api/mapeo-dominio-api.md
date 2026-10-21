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
| Agregar artículo al carrito | POST | /api/carritos/{clienteId}/items |
| Ver carrito | GET | /api/carritos/{clienteId} |
| Vaciar carrito | DELETE | /api/carritos/{clienteId}/items |