| Operacion del dominio | Método HTTP | Endpoint | 
|---|---|---| 
| crear rutina de cuidado | POST | /rutinas |
| consultar una rutina de cuidado | GET | /rutinas/{id} |
| listar artículos disponibles | GET| /articulos |
| publicar articulo | POST | /articulos |
| Consultar detalle de un artículo | GET | /api/articulos/{id} |
| Aumentar inventario de un artículo | POST | /api/articulos/{id}/inventario/aumentar |
| Disminuir inventario de un artículo | POST | /api/articulos/{id}/inventario/disminuir |
| Buscar artículos aplicando filtros opcionales | GET | /api/buscarArticulos |

# Mapeo Dominio -> API - Carrito

| Operacion del dominio | Método HTTP | Endpoint |
|---|---|---|
| agregarArticulo(articulo, cantidad) | POST | /api/carritos/{clienteId}/items |