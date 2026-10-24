INSERT INTO usuarios (nombre, username, password_hash, rol) VALUES
('Administrador', 'admin', '$2a$10$hashDeEjemploAdmin', 'ADMIN'),
('Carlos Mesero', 'cmesero', '$2a$10$hashDeEjemploMesero', 'MESERO'),
('Ana Cocina', 'acocina', '$2a$10$hashDeEjemploCocina', 'COCINA');

INSERT INTO productos (nombre, categoria, precio, activo) VALUES
('Pupusa revuelta', 'Comida', 1.50, TRUE),
('Pupusa de queso', 'Comida', 1.25, TRUE),
('Hamburguesa clásica', 'Comida', 6.50, TRUE),
('Pizza personal', 'Comida', 7.00, TRUE),
('Ensalada César', 'Comida', 5.25, TRUE),
('Refresco natural', 'Bebida', 1.75, TRUE),
('Café', 'Bebida', 1.50, TRUE),
('Flan', 'Postre', 2.50, TRUE);

INSERT INTO mesas (numero, capacidad, estado) VALUES
(1, 4, 'LIBRE'),
(2, 4, 'OCUPADA'),
(3, 2, 'LIBRE'),
(4, 6, 'LIBRE'),
(5, 8, 'LIBRE');

INSERT INTO pedidos (mesa_id, usuario_id, estado, total) VALUES
(1, 2, 'CERRADO', 9.50),
(2, 2, 'ABIERTO', 10.50);

INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, precio_unitario) VALUES
(1, 1, 4, 1.50),
(1, 6, 2, 1.75),
(2, 3, 1, 6.50),
(2, 7, 1, 1.50),
(2, 8, 1, 2.50);

INSERT INTO reportes_venta (fecha, total_ventas, cantidad_pedidos, generado_por) VALUES
(CURDATE(), 9.50, 1, 1);