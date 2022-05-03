-- TABLA USUARIOS (Contraseña de todos: aa)
INSERT INTO IWUser (id, disc_rol, enabled, roles, username, password)
VALUES (1, 'ADMIN', TRUE, 'ADMIN,USER', 'a',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');
INSERT INTO IWUser (id, disc_rol, enabled, roles, username, password, direccion,first_name,last_name)
VALUES (2, 'CLIENTE', TRUE, 'USER', 'b',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 'C/ de las Moscas, 5, 8ºF','Alberto','Ferichola');
INSERT INTO IWUser (id, disc_rol, enabled, roles, username, password)
VALUES (3, 'REPARTIDOR', TRUE, 'REPARTIDOR,USER', 'repartidor',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');
INSERT INTO IWUser (id, disc_rol, enabled, roles, username, first_name, last_name, password)
VALUES (4, 'RESTAURANTE', TRUE, 'RESTAURANTE,USER', 'restaurante', 'Juan', 'Cuesta',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');
INSERT INTO IWUser (id, disc_rol, enabled, roles, username, password)
VALUES (5, 'REPARTIDOR', TRUE, 'REPARTIDOR,USER', 'repartidor2',
    '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W');
-- TABLA RESTAURANTE
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, propietario_id)
VALUES (1,'Vips', 'Calle Falsa, 123', '24/7', 'Te queremos en Vips.  El sitio perfecto para planes con amigos, comidas familiares o para pasar un rato disfrutando de tus platos favoritos.', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, propietario_id)
VALUES (2,'Fosters Hollywood', 'Calle Falsa, 123', '24/7', 'Conoce en Fosters Hollywood el sabor de la comida americana: hamburguesas, costillas... Descubre nuestra carta y encuentra tu restaurante mas cercano.', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, propietario_id)
VALUES (3,'Ginos Ristorante', 'Calle Falsa, 123', '24/7', 'Ginos Ristorante. Pizzas de masa napoletana, pastas importadas directamente desde Italia... Vente y disfruta de Ginos', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, propietario_id)
VALUES (4,'McDonalds', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, propietario_id)
VALUES (5,'Burger King', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, propietario_id)
VALUES (6,'Dominos Pizza', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, propietario_id)
VALUES (7,'Telepizza', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, propietario_id)
VALUES (8,'Rodilla', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, propietario_id)
VALUES (9,'Tagliatella', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, propietario_id)
VALUES (10,'Goiko', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, propietario_id)
VALUES (11,'The Good Burger', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, propietario_id)
VALUES (12,'Udon', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, propietario_id)
VALUES (13,'Dunkin Coffee', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, propietario_id)
VALUES (14,'Tony Romas', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, propietario_id)
VALUES (15,'Starbucks', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, propietario_id)
VALUES (16,'Subway', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, propietario_id)
VALUES (17,'Pans & company', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, propietario_id)
VALUES (18,'100 Montaditos', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);
INSERT INTO Restaurante (id, nombre, direccion, horario, descripcion, valoracion, propietario_id)
VALUES (19,'Five Guys', 'Calle Falsa, 123', '24/7', 'Lorem ipsum', 4.0, 4);

-- TABLA PLATOS
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (1, 'Carne de vacuno, queso estilo americano y cheddar fundidos, bacon crujiente, salsa BBQ ahumada, cebolla roja a la plancha, pepinillo y salsa baconesa en pan brioche tostado con dos sesamos. Junto de guarnicion a elegir y salsa baconesa.', 'Hamburguesa', 10.99, 1);
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (2, 'Pan blanco, tres pisos de pollo, bacon, jamon York, queso americano y queso Emmental, mayonesa, tomate y lechuga. Disponible tambien con pan integral.', 'Sandwich', 13.55, 1);
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (3, 'Combo de: Aros de cebolla con salsa especial VIPS, Quesadilla de jamon York y queso con pico de gallo, crema agria y guacamole, Alitas Chili BBQ y Nachos Tex-Mex.', 'Combo', 9.35, 1);
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (4, 'Cremosas croquetas de jamon iberico coronadas con paleta iberica y un toque de salsa mayo-sriracha.', 'Croquetas', 8.55, 1);
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (5, 'Bacon crujiente y ajetes sobre Mozzarella fundida y una cremosa salsa blanca en base de focaccia.', 'Pizza', 10.50, 1);
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (6, 'Aguacate, mango, fresas, queso de cabra, queso feta, tomate cherry, cebolla roja y nueces, rociada con vinagreta de lima y cilantro sobre una base de quinoa y mezcla de lechugas con brotes.', 'Ensalada', 9.35, 1);
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (7, 'Costillas de cerdo ahumadas a la parrilla', 'National Star Ribs', 15.99, 2);
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (8, 'Carne de vacuno, con queso Cheddar y bacon ahumado, sobre base de lechuga romana, tomate, cebolla y salsa especial FH', 'Directors Choice Version Original', 10.99, 2);
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (9, 'Pechua de pollo a la parrilla, bacon crujiente, lechuga romana y salsa parmesana en pan con semillas y copos de avena','Caesar', 12.99, 2);
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (10, 'Con chorizo junto con nata, salsa pomodoro y bechamel, gratinados con queso parmesano', 'Rigatoni al Forno', 17.00, 3);
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (11, 'Base de tomate triturado, mozzarela, panceta, cebolla caramelizada, aceitunas negras taggiasca y orégano.', 'Pizza', 13.95, 3);
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (12, 'Crujiente milanesa de pechuga de pollo, junto de patatas asados con ajo y romero, y salsa de tomate', 'Carne', 14.99, 3);
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (13, 'Delicioso pollo crujiente por fuera y jugoso por dentro, junto de queso gouda, cebolla fresca y salsa buffalo', 'Hamburguesa', 5.99, 4);
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (14, 'Queso por encima y entre su doble de pollo crispy, queso en la salsa, queso por dentro y fuera del pan', 'Hamburguesa', 5.99, 5);
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (15, '100% Vegetal sabor Big King, con su misma lechuga, pepinillos y cebolla de siempre, rociado en su increible salsa entre dos panes de sésamo crujiente pero vegetal. Sin carne', 'Hamburguesa', 6.99, 5);
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (16, 'Suave masa horneada con canela y una espiral de glaseado', 'Roll Canela', 3.99, 15);
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (17, 'Napolitana con decilioso chocolate', 'Napolitana', 3.99, 15);
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (18, 'Carbonara de crema, queso 100%, mozzarella, bacon y cebolla', 'Pizza', 9.99, 6);
INSERT INTO Plato (id, descripcion, nombre, precio, restaurante_id)
VALUES (19, 'Texas BBQ Crispy con salsa BBQ Texas, queso 100%, mozarrella, bacon crispy, bacon, pollo a la parrilla, carne de vacuno, queso cheddar en el borde', 'Pizza', 3.99, 6);



-- TABLA EXTRA
INSERT INTO Extra (id, nombre, precio, plato_id, plato_pedido_id)
VALUES (1, 'Bacon', 3.0, 1, 1);
INSERT INTO Extra (id, nombre, precio, plato_id, plato_pedido_id)
VALUES (2, 'Patatas fritas', 3.0, 1, 1);
INSERT INTO Extra (id, nombre, precio, plato_id)
VALUES (3, 'Cebolla', 1.0, 2);
INSERT INTO Extra (id, nombre, precio, plato_id)
VALUES (4, 'Patatas fritas', 1.0, 13);
INSERT INTO Extra (id, nombre, precio, plato_id)
VALUES (5, 'Patatas cheedar', 1.0, 13);
-- TABLA COMENTARIO
INSERT INTO Comentario (id, texto, user_id, plato_id, restaurante_id)
VALUES (1, 'Tiene buen sabor', 2, 1, 1);
-- TABLA PEDIDO
INSERT INTO Pedido (id, dir_entrega, estado, fecha_pedido, precio_entrega, precio_servicio, cliente_id, repartidor_id, restaurante_id, valoracion,lat,lng)
VALUES (1,'Calle Falsisima1, 345', 0, CURRENT_TIMESTAMP, 3.54, 6.56, 2, null, 1, 0.0,40.420177,-3.703928);
INSERT INTO Pedido (id, dir_entrega, estado, fecha_pedido, precio_entrega, precio_servicio, cliente_id, repartidor_id, restaurante_id, valoracion,lat,lng)
VALUES (2,'Calle Falsisima2, 345', 1, CURRENT_TIMESTAMP, 3.54, 6.56, 2, null, 2, 0.0,0,0);
INSERT INTO Pedido (id, dir_entrega, estado, fecha_pedido, precio_entrega, precio_servicio, cliente_id, repartidor_id, restaurante_id, valoracion,lat,lng)
VALUES (3,'Calle Falsisima68, 419', 1, CURRENT_TIMESTAMP, 3.54, 6.56, 2, null, 1, 0.0,40.420177,-3.703928);
INSERT INTO Pedido (id, dir_entrega, estado, fecha_pedido, precio_entrega, precio_servicio, cliente_id, repartidor_id, restaurante_id, valoracion,lat,lng)
VALUES (4,'Calle Falsisima69, 420', 2, CURRENT_TIMESTAMP, 3.54, 6.56, 2, null, 1, 0.0,40.420177,-3.703928);

--TABLA PLATOPEDIDO               
INSERT INTO Plato_pedido (id, cantidad, pedido_id, plato_id)
VALUES (1, 1, 1, 1);
INSERT INTO Plato_pedido (id, cantidad, pedido_id, plato_id)
VALUES (2, 1, 1, 3);
INSERT INTO Plato_pedido (id, cantidad, pedido_id, plato_id)
VALUES (3, 1, 2, 2);
INSERT INTO Plato_pedido (id, cantidad, pedido_id, plato_id)
VALUES (4, 1, 3, 4);
INSERT INTO Plato_pedido (id, cantidad, pedido_id, plato_id)
VALUES (5, 1, 4, 4);
--TABLA CATEGORIAS
INSERT INTO LABEL (id,nombre)
VALUES(1, 'Desayuno');
INSERT INTO LABEL (id,nombre)
VALUES(2, 'Pizzas');
INSERT INTO LABEL (id,nombre)
VALUES(3, 'Hamburguesas');
INSERT INTO LABEL (id,nombre)
VALUES(4, 'Pasta');
INSERT INTO LABEL (id,nombre)
VALUES(5, 'Meriendas');
INSERT INTO LABEL (id,nombre)
VALUES(6, 'Burritos');

ALTER SEQUENCE "PUBLIC"."GEN" RESTART WITH 1024;
