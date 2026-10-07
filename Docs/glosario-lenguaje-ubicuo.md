# Glosario del Lenguaje Ubicuo - GLOW SHOP

## Conceptos Centrales

### Rutina de Cuidado
**Definición:** Conjunto personalizado de productos de una o varias marcas que una clienta utiliza de manera continua para atender una necesidad específica de cuidado facial, corporal o capilar. Representa un comportamiento sostenido de cuidado en el tiempo, en el que diferentes productos pueden complementarse para cumplir un propósito común.

**Sinónimos aceptados:** Ritual, ritual de cuidado, ritual de belleza, rutina de belleza
**No usar:** Combo, lista de productos, conjunto de productos, rutina de compra

**Precondiciones:** La clienta debe tener identificada al menos una necesidad de cuidado, ya sea facial, corporal o capilar, y haber incorporado uno o más productos destinados a atenderla. Los productos pueden pertenecer a una misma marca o a diferentes marcas y, al utilizarse de manera complementaria y continua, conforman una rutina de cuidado.

**Ejemplo de uso en código:**
\`\`\`java
RutinaCuidado rutina = RutinaCuidado.crear("Cuidado facial");
\`\`\`

---

### Tono
**Definición:** Característica que identifica la tonalidad de un artículo cosmético, especialmente en productos de maquillaje como bases, correctores, labiales o sombras. Permite facilitar la selección de una tonalidad adecuada para el cliente.

**Sinónimos aceptados:** Tonalidad, tono cosmético

**No usar:** Color genérico

**Precondiciones:** Solo aplica a artículos que manejan diferentes tonalidades, el tono debe pertenecer a los tonos disponibles para la referencia correspondiente

**Ejemplo de uso:**
\`\`\`java
Tono tono = Tono.crear("Beige medio");
\`\`\`

---

### Tipo de Piel
**Definición:** Característica que describe las condiciones generales de la piel de una persona según su comportamiento y necesidades, permitiendo identificar categorías como piel seca, grasa, mixta, normal o sensible. Se utiliza para relacionar las necesidades del cliente con productos de cuidado facial que sean adecuados para su tipo de piel.

**Sinónimos aceptados:** Característica de piel, clasificación de piel, tipo de piel facial

**No usar:** Estado de la piel, problema de piel, necesidad de piel

**Precondiciones:** La clienta debe tener identificado su Tipo de Piel para poder asociarlo a su perfil o utilizarlo como criterio de búsqueda y recomendación de productos. Un producto puede estar dirigido a uno o varios tipos de piel.

**Ejemplo de uso:**
\`\`\`java
TipoPiel tipo = TipoPiel.seleccionar("Piel mixta");
\`\`\`

---

### Best Seller
**Definición:** Producto que se encuentra entre los más vendidos dentro del marketplace durante un período determinado, de acuerdo con su cantidad de ventas. Representa un producto con alta demanda y preferencia por parte de los clientes.

**Sinónimos aceptados:** Más vendido, producto destacado por ventas, producto de mayor demanda

**No usar:** Producto popular, producto recomendado, producto en tendencia, producto favorito

**Precondiciones:** El producto debe estar publicado y disponible para la venta en el marketplace y contar con un historial suficiente de ventas que permita determinar su nivel de demanda frente a otros productos. La condición de Best Seller puede variar según el período de análisis y actualizarse de acuerdo con el comportamiento de las ventas.

**Ejemplo de uso:**
\`\`\`java
Producto producto = marketplace.obtenerBestSeller("Cuidado facial");
\`\`\`

---

### Lanzamiento
**Definición:** Acción mediante la cual un vendedor incorpora y presenta un producto nuevo en el marketplace para ponerlo a disposición de los clientes. Representa la introducción de una novedad dentro del catálogo de una tienda o marca, que puede ser destacada para aumentar su visibilidad y darla a conocer a los clientes.

**Sinónimos aceptados:** Novedad, producto nuevo, estreno

**No usar:** Producto reciente, producto destacado

**Precondiciones:** El vendedor debe estar registrado en el marketplace y contar con un producto nuevo que cumpla con la información y condiciones necesarias para ser publicado. El producto debe encontrarse disponible en el catálogo para que pueda ser identificado como un Lanzamiento.

**Ejemplo de uso:**
\`\`\`java
Lanzamiento lanzamiento = Lanzamiento.crear(producto);
\`\`\`

---

## Anti-patrones (Términos a EVITAR en nuestro proyecto)

| No usar | Usar |
|---|---|
| Lista de productos | Rutina de Cuidado |
| Color | Tono |
| Problema de piel  | Tipo de Piel |
| Producto popular  | Best Seller |
| Producto reciente  | Lanzamiento |