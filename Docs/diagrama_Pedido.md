# Diagrama de Agregado --- Pedido

En el "diagrama de agregado" de nuestro e-commerce de belleza elegimos
Pedido como la raíz del agregado, porque representa la compra
realizada por el cliente y es el encargado de controlar las reglas y
operaciones relacionadas con ella.

Dentro del agregado ubicamos (EstadoPedido), (DetallePedido) y
(Precio), ya que hacen parte directamente del pedido y deben
mantenerse consistentes junto con él. Por ejemplo, (DetallePedido)
guarda el artículo, la cantidad y el precio unitario utilizado en el
momento de la compra. Así, aunque el precio del artículo cambie
posteriormente, el pedido conserva el precio con el que fue realizado.

Por fuera del agregado dejamos las entidades (Usuario), (Articulo),
(Cupon) y (Pago), porque cada una tiene su propio ciclo de vida y no
depende directamente del pedido para existir. Por eso, (Pedido) las
referencia mediante sus identificadores, como (clienteId), (articuloId)
y (cuponId), en lugar de incluirlas completamente dentro del agregado.

La entidad (Pedido) también contiene los principales métodos de negocio:
(confirmar()), (cancelar()), (aplicarCupon()) y (agregarDetalle()).
Estos métodos permiten controlar las modificaciones del pedido mediante
las reglas del negocio y evitar cambios directos que puedan generar
información inconsistente.

## Invariantes del agregado

Las "invariantes" son las reglas que siempre deben cumplirse dentro
del agregado para mantener el pedido correcto y consistente:

-   **Un Pedido nunca puede pasar a (EN_PREPARACION) si no tiene un pago
    confirmado asociado**.
-   **Un Pedido siempre debe contener al menos un (DetallePedido); no
    puede confirmarse vacío**. 
-   **El (precioUnitario) de un (DetallePedido) nunca cambia**, aunque
    posteriormente cambie el precio del (Articulo) original.
-   **Un Pedido en estado (CANCELADO) nunca puede volver directamente a
    (CONFIRMADO)**; para realizar nuevamente la compra se requiere un
    pedido nuevo.
-   **El total del Pedido siempre debe corresponder a la suma de sus
    detalles**, teniendo en cuenta descuentos, impuestos y envío cuando
    aplique.
