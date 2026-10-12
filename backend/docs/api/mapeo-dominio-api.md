| Operacion del dominio | Método HTTP | Endpoint | 
|---|---|---| 
| crear rutina de cuidado | POST | /rutinas |
| consultar una rutina de cuidado | GET | /rutinas/{id} |
| publicar articulo | POST | /articulos |
| Aumentar inventario de un artículo | POST | /api/articulos/{id}/inventario/aumentar |
| Disminuir inventario de un artículo | POST | /api/articulos/{id}/inventario/disminuir |

# Mapeo Dominio -> API - Carrito

| Operacion del dominio | Método HTTP | Endpoint |
|---|---|---|
| agregarArticulo(articulo, cantidad) | POST | /api/carritos/{clienteId}/items |