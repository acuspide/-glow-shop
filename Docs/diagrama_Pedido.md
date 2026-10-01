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
(Precio) también se usa para el descuento, los impuestos y el costo de
envío del pedido.
 
Por fuera del agregado dejamos las entidades (Usuario), (Articulo),
(Cupon) y (Pago), porque cada una tiene su propio ciclo de vida y no
depende directamente del pedido para existir. Por eso, (Pedido) las
referencia mediante sus identificadores, como (clienteId), (articuloId),
(cuponId) y (pagoConfirmadoId), en lugar de incluirlas completamente
dentro del agregado. A su vez, (Pago) conoce el pedido al que
pertenece mediante (pedidoId).
 
La entidad (Pedido) también contiene los principales métodos de negocio:
 
- Para armar el pedido: (agregarDetalle()), (aplicarCupon()),
  (asignarImpuestos()) y (asignarCostoEnvio()).
- Para avanzar en su ciclo de vida: (confirmar()), (registrarPago()),
  (iniciarPreparacion()), (enviar()), (entregar()) y (cancelar()).
- Para consultar sus valores: (calcularSubtotal()) y (calcularTotal()).
Estos métodos permiten controlar las modificaciones del pedido mediante
las reglas del negocio y evitar cambios directos que puedan generar
información inconsistente.

## Invariantes del agregado

Las "invariantes" son las reglas que siempre deben cumplirse dentro
del agregado para mantener el pedido correcto y consistente:

1. Un Pedido no puede pasar a EN_PREPARACION si no tiene registrado un pago confirmado.
2. Un Pedido siempre debe contener al menos un DetallePedido. 
3. El precioUnitario de un DetallePedido nunca cambia, aunque después cambie el precio del Artículo original.
4. CANCELADO y ENTREGADO son estados finales: un pedido en cualquiera de ellos no puede pasar a otro estado. Para volver a comprar se requiere un pedido nuevo.
5. El total del Pedido siempre es igual a la suma de (precioUnitario × cantidad) de sus detalles, menos el descuento, más los impuestos y el costo de envío.
6. El descuento nunca puede ser mayor que la suma de los detalles, de modo que el total nunca es negativo.
7. La cantidad de cada DetallePedido debe ser mayor que 0.
8. Los detalles y el cupón solo pueden modificarse mientras el Pedido está en estado PENDIENTE.
9. Un Pedido admite como máximo un cupón.

## Diagrama de agregado Pedido elaborado en Marmaid
```mermaid
classDiagram
direction TB
    class Pedido {
        -id: long
        -clienteId: long
        -detalles: List~DetallePedido~
        -estado: EstadoPedido
        -cuponId: Long
        -descuento: Precio
        -impuestos: Precio
        -costoEnvio: Precio
        -pagoConfirmadoId: Long
        +Pedido(id: long, clienteId: long, primerDetalle: DetallePedido)
        +agregarDetalle(detalle: DetallePedido)
        +aplicarCupon(cupon: Cupon)
        +asignarImpuestos(impuestos: Precio)
        +asignarCostoEnvio(costoEnvio: Precio)
        +confirmar()
        +registrarPago(pago: Pago)
        +iniciarPreparacion()
        +enviar()
        +entregar()
        +cancelar()
        +calcularSubtotal() BigDecimal
        +calcularTotal() BigDecimal
        -transicionarA(nuevoEstado: EstadoPedido)
        -validarModificable()
    }

    class EstadoPedido {
        PENDIENTE
        CONFIRMADO
        EN_PREPARACION
        ENVIADO
        ENTREGADO
        CANCELADO
        +puedeTransicionarA(nuevoEstado: EstadoPedido) boolean
        +esFinal() boolean
    }

    class DetallePedido {
        -articuloId: long
        -cantidad: int
        -precioUnitario: Precio
        -validarCantidad(cantidad: int)
        +subtotal() BigDecimal
    }

    class Precio {
        -valor: BigDecimal
        +Precio(valor: BigDecimal)
        +valor() BigDecimal
    }

    class Pago {
        -id: long
        -pedidoId: long
        -monto: Precio
        -estado: EstadoPago
        +aprobar()
        +rechazar()
        +reembolsar()
        +estaAprobado() boolean
    }

    class EstadoPago {
        PENDIENTE
        APROBADO
        RECHAZADO
        REEMBOLSADO
    }

    class Cupon {
        -id: long
        -codigo: String
        -porcentaje: int
        -fechaExpiracion: LocalDate
        +estaVigente() boolean
        +calcularDescuento(subtotal: BigDecimal) Precio
    }

    class Usuario {
        -id: long
        -nombre: String
        -email: Email
        -contrasenaHash: String
        -rol: RolUsuario
        -activo: boolean
        +desactivar()
    }

    class Articulo {
        -id: long
        -nombre: NombreArticulo
        -precio: Precio
        -categoria: Categoria
        -marca: Marca
        -tono: Tono
        -tiposPiel: List~TipoPiel~
        -inventario: Inventario
        -fechaVencimiento: FechaVencimiento
        -tienda: Tienda
        -eliminado: boolean
        -publicado: boolean
        +publicar()
        -validarPuedePublicarse()
    }

    <<AggregateRoot>> Pedido
    <<ValueObject>> EstadoPedido
    <<ValueObject>> DetallePedido
    <<ValueObject>> Precio
    <<Entity>> Pago
    <<ValueObject>> EstadoPago
    <<Entity>> Cupon
    <<Entity>> Usuario
    <<Entity>> Articulo

    Pedido "1" *-- "1" EstadoPedido : estado (dentro del límite)
    Pedido "1" *-- "1..*" DetallePedido : detalles (dentro del límite)
    Pedido *-- Precio : descuento, impuestos, costoEnvio (dentro del límite)
    DetallePedido "1" *-- "1" Precio : precioUnitario (dentro del límite)
    Pedido ..> Usuario : clienteId (fuera del agregado)
    DetallePedido ..> Articulo : articuloId (fuera del agregado)
    Pedido ..> Cupon : cuponId (fuera del agregado)
    Pedido ..> Pago : pagoConfirmadoId (fuera del agregado)
    Pago "0..*" ..> "1" Pedido : pedidoId (fuera del agregado)
    Pago "1" *-- "1" EstadoPago : estado
```
