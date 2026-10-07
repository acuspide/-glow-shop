| Operacion del dominio | Método HTTP | Endpoint | 
|---|---|---| 
| crear rutina de cuidado | POST | /rutinas |
| consultar una rutina de cuidado | GET | /rutinas/{id} |
| publicar articulo | |  |

# Mapeo Dominio -> API - Carrito

| Operacion del dominio | Método HTTP | Endpoint |
|---|---|---|
| agregarArticulo(articulo, cantidad) | POST | /api/carritos/{clienteId}/items |