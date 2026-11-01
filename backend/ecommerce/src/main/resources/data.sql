-- ============================================================================
-- Datos de prueba de Glow Shop. Se cargan cada vez que arranca la aplicación
-- (H2 en memoria). Todos los usuarios de aquí tienen la contraseña Clave12345
-- guardada con BCrypt. El administrador (admin@glowshop.com / Admin12345)
-- lo crea AdministradorInicialInicializador al arrancar.
-- ============================================================================

-- Usuarios ---------------------------------------------------------------------
INSERT INTO usuarios (id, nombre, email, contrasena_hash, telefono, fecha_nacimiento, rol, activo) VALUES
('usr-vera',   'Vera Ramírez',   'vera@glow.com',      '$2a$10$Jos5DmL8jdBKmA.7nd9vaOj6gS6Hnsy42crNrS/qlXUgpLLogOgkC', '3001112233', '1995-03-14', 'VENDEDOR', TRUE),
('usr-lina',   'Lina Gómez',     'lina@glow.com',      '$2a$10$Jos5DmL8jdBKmA.7nd9vaOj6gS6Hnsy42crNrS/qlXUgpLLogOgkC', '3002223344', '1992-07-21', 'VENDEDOR', TRUE),
('usr-sofia',  'Sofía Herrera',  'sofia@glow.com',     '$2a$10$Jos5DmL8jdBKmA.7nd9vaOj6gS6Hnsy42crNrS/qlXUgpLLogOgkC', '3003334455', '1999-11-02', 'VENDEDOR', TRUE),
('usr-ana',    'Ana Torres',     'ana@correo.com',     '$2a$10$Jos5DmL8jdBKmA.7nd9vaOj6gS6Hnsy42crNrS/qlXUgpLLogOgkC', '3101234567', '1998-05-10', 'CLIENTE',  TRUE),
('usr-carlos', 'Carlos Mejía',   'carlos@correo.com',  '$2a$10$Jos5DmL8jdBKmA.7nd9vaOj6gS6Hnsy42crNrS/qlXUgpLLogOgkC', '3159876543', '1994-09-30', 'CLIENTE',  TRUE);

-- Tiendas (Sofía tiene la suya PENDIENTE, para probar "aprobar tienda") ------------
INSERT INTO tiendas (id, nombre, descripcion, vendedor_id, estado) VALUES
('tienda-glow-natural', 'Glow Natural',    'Maquillaje y cuidado facial', 'usr-vera',  'ACTIVA'),
('tienda-lina-beauty',  'Lina Beauty',     'Cuidado de la piel y el cabello', 'usr-lina', 'ACTIVA'),
('tienda-rosa',         'Rosa Cosméticos', 'Cosmética natural',           'usr-sofia', 'PENDIENTE');

-- Categorías y marcas ------------------------------------------------------------
INSERT INTO categorias (id, nombre) VALUES
('cat-labios', 'Labios'),
('cat-rostro', 'Rostro'),
('cat-ojos', 'Ojos'),
('cat-cuidado-facial', 'Cuidado facial'),
('cat-cuidado-capilar', 'Cuidado capilar');

INSERT INTO marcas (id, nombre) VALUES
('marca-maybelline', 'Maybelline'),
('marca-loreal', 'L''Oréal'),
('marca-vogue', 'Vogue'),
('marca-nivea', 'Nivea'),
('marca-cetaphil', 'Cetaphil');

-- Artículos ----------------------------------------------------------------------
-- art-serum-vitc y art-protector tienen stock bajo (< 5).
-- art-gloss-2023 está eliminado (soft delete): no debe aparecer en los listados.
INSERT INTO articulos (id, nombre, precio, categoria_id, marca_id, tienda_id, tono, cantidad_disponible, fecha_vencimiento, eliminado, publicado) VALUES
('art-labial-mate',     'Labial mate Ruby',          35000, 'cat-labios',          'marca-maybelline', 'tienda-glow-natural', 'MEDIO',  25, '2028-06-30', FALSE, TRUE),
('art-base-fit',        'Base líquida Fit Me',       52000, 'cat-rostro',          'marca-maybelline', 'tienda-glow-natural', 'CLARO',  18, '2028-03-31', FALSE, TRUE),
('art-mascara',         'Máscara de pestañas volumen', 41000, 'cat-ojos',          'marca-loreal',     'tienda-glow-natural', 'OSCURO', 12, '2027-12-31', FALSE, TRUE),
('art-serum-vitc',      'Sérum vitamina C',          89000, 'cat-cuidado-facial',  'marca-loreal',     'tienda-glow-natural', 'MEDIO',   4, '2027-09-30', FALSE, TRUE),
('art-gloss-2023',      'Brillo labial edición 2023', 22000, 'cat-labios',         'marca-vogue',      'tienda-glow-natural', 'CLARO',   6, '2027-01-31', TRUE,  FALSE),
('art-hidratante',      'Crema hidratante Cetaphil', 64000, 'cat-cuidado-facial',  'marca-cetaphil',   'tienda-lina-beauty',  'CLARO',  30, '2028-01-31', FALSE, TRUE),
('art-limpiador',       'Gel limpiador espumoso',    48000, 'cat-cuidado-facial',  'marca-cetaphil',   'tienda-lina-beauty',  'MEDIO',  22, '2028-02-29', FALSE, TRUE),
('art-shampoo',         'Shampoo reparador',         27000, 'cat-cuidado-capilar', 'marca-nivea',      'tienda-lina-beauty',  'MEDIO',  40, '2028-08-31', FALSE, TRUE),
('art-protector',       'Protector solar FPS 50',    59000, 'cat-cuidado-facial',  'marca-nivea',      'tienda-lina-beauty',  'CLARO',   3, '2027-11-30', FALSE, TRUE),
('art-polvo',           'Polvo compacto mate',       31000, 'cat-rostro',          'marca-vogue',      'tienda-lina-beauty',  'OSCURO', 15, '2028-04-30', FALSE, TRUE);

INSERT INTO articulo_tipos_piel (articulo_id, tipo_piel) VALUES
('art-labial-mate', 'SECA'), ('art-labial-mate', 'NORMAL'),
('art-base-fit', 'GRASA'), ('art-base-fit', 'MIXTA'),
('art-mascara', 'NORMAL'),
('art-serum-vitc', 'NORMAL'), ('art-serum-vitc', 'MIXTA'), ('art-serum-vitc', 'SECA'),
('art-gloss-2023', 'NORMAL'),
('art-hidratante', 'SENSIBLE'), ('art-hidratante', 'SECA'),
('art-limpiador', 'GRASA'), ('art-limpiador', 'MIXTA'),
('art-shampoo', 'NORMAL'),
('art-protector', 'SENSIBLE'), ('art-protector', 'NORMAL'), ('art-protector', 'GRASA'),
('art-polvo', 'GRASA');

-- Pedidos (uno por tienda) -----------------------------------------------------------
-- Entregados: sirven para calificar y para el ranking de más vendidos.
--   Unidades entregadas: labial 3, protector 2, base 1, sérum 1, hidratante 1.
-- ped-004 está ENVIADO (pagado, aún no entregado) y ped-005 CONFIRMADO sin pagar.
INSERT INTO pedidos (id, cliente_id, tienda_id, estado, cupon_id, descuento, impuestos, costo_envio, pago_confirmado_id) VALUES
('ped-001', 'usr-ana',    'tienda-glow-natural', 'ENTREGADO',  NULL, 0, 0, 0, 'pag-001'),
('ped-002', 'usr-carlos', 'tienda-glow-natural', 'ENTREGADO',  NULL, 0, 0, 0, 'pag-002'),
('ped-003', 'usr-ana',    'tienda-lina-beauty',  'ENTREGADO',  NULL, 0, 0, 0, 'pag-003'),
('ped-004', 'usr-carlos', 'tienda-lina-beauty',  'ENVIADO',    NULL, 0, 0, 0, 'pag-004'),
('ped-005', 'usr-ana',    'tienda-glow-natural', 'CONFIRMADO', NULL, 0, 0, 0, NULL);

INSERT INTO pedido_detalles (pedido_id, posicion, articulo_id, cantidad, precio_unitario) VALUES
('ped-001', 0, 'art-labial-mate', 2, 35000),
('ped-001', 1, 'art-base-fit',    1, 52000),
('ped-002', 0, 'art-labial-mate', 1, 35000),
('ped-002', 1, 'art-serum-vitc',  1, 89000),
('ped-003', 0, 'art-hidratante',  1, 64000),
('ped-003', 1, 'art-protector',   2, 59000),
('ped-004', 0, 'art-limpiador',   1, 48000),
('ped-004', 1, 'art-hidratante',  1, 64000),
('ped-005', 0, 'art-labial-mate', 1, 35000);

INSERT INTO pagos (id, pedido_id, monto, medio_pago, fecha_pago, estado, transaccion_id, motivo_rechazo) VALUES
('pag-001', 'ped-001', 122000, 'NEQUI',           '2026-10-20 10:15:00', 'APROBADO', 'TX-0001', NULL),
('pag-002', 'ped-002', 124000, 'TARJETA_CREDITO', '2026-10-21 16:40:00', 'APROBADO', 'TX-0002', NULL),
('pag-003', 'ped-003', 182000, 'PSE',             '2026-10-22 09:05:00', 'APROBADO', 'TX-0003', NULL),
('pag-004', 'ped-004', 112000, 'TARJETA_DEBITO',  '2026-10-25 18:30:00', 'APROBADO', 'TX-0004', NULL);

-- Calificaciones (solo de artículos ya entregados a quien califica) -----------------
INSERT INTO calificaciones (id, articulo_id, cliente_id, puntuacion, comentario, fecha, eliminada) VALUES
('cal-001', 'art-labial-mate', 'usr-ana',    5, 'Me encanta el color, dura todo el día', '2026-10-24 20:00:00', FALSE),
('cal-002', 'art-labial-mate', 'usr-carlos', 4, 'Buen producto, se lo regalé a mi novia', '2026-10-25 12:30:00', FALSE),
('cal-003', 'art-hidratante',  'usr-ana',    5, 'Ideal para piel sensible',              '2026-10-26 08:45:00', FALSE);
