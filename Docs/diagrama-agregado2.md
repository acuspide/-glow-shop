# Invariantes del agregado Articulo

1. Un artículo no puede publicarse sin categoría.
2. Un artículo no puede publicarse sin marca, precio, inventario o tienda.
3. Un artículo no puede publicarse si su fecha de vencimiento ya expiró.
4. El inventario no puede tener una cantidad disponible negativa.
5. Si una publicación es rechazada, el artículo debe conservar su estado anterior.

# Codigo usado para imagen en mermeid

classDiagram

    class Articulo {
        -long id
        -NombreArticulo nombre
        -Precio precio
        -Categoria categoria
        -Marca marca
        -Tono tono
        -List~TipoPiel~ tiposPiel
        -Inventario inventario
        -FechaVencimiento fechaVencimiento
        -Tienda tienda
        -boolean eliminado
        -boolean publicado
        +publicar() void
    }

    class NombreArticulo {
        <<Value Object>>
    }

    class Precio {
        <<Value Object>>
    }

    class Inventario {
        -int cantidadDisponible
    }

    class FechaVencimiento {
        <<Value Object>>
        +estaVencida() boolean
    }

    class Tono {
        <<enumeration>>
        CLARO
        MEDIO
        OSCURO
    }

    class TipoPiel {
        <<enumeration>>
        SECA
        GRASA
        MIXTA
        NORMAL
        SENSIBLE
    }

    class Categoria {
        <<fuera del agregado>>
        -long id
        -String nombre
    }

    class Marca {
        <<fuera del agregado>>
        -long id
        -String nombre
    }

    class Tienda {
        <<fuera del agregado>>
        -long id
        -String nombre
    }

    Articulo *-- NombreArticulo : dentro del limite
    Articulo *-- Precio : dentro del limite
    Articulo *-- Inventario : dentro del limite
    Articulo *-- FechaVencimiento : dentro del limite
    Articulo *-- Tono : dentro del limite
    Articulo *-- TipoPiel : dentro del limite

    Articulo ..> Categoria : fuera del agregado
    Articulo ..> Marca : fuera del agregado
    Articulo ..> Tienda : fuera del agregado