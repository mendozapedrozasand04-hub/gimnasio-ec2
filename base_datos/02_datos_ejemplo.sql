USE gimnasio_db;
-- Ejecutar una sola vez, despues de 01_crear_base.sql.
INSERT INTO administrador(nombres,apellidos,correo,password_hash) VALUES('Administrador','Gimnasio','admin@gimnasio.com','$2a$12$.I4jzTSzrtu/kuyiG8Ff6Oi7K6CnbHU255QuQnOPX2tVGHfGg9YuW');
INSERT INTO cliente(tipo_documento,documento,nombres,apellidos,telefono,correo) VALUES
('DNI','12345678','Ana','Torres','987654321','ana@example.com'),
('DNI','23456789','Luis','Ramos','987654322','luis@example.com'),
('DNI','34567890','Carla','Flores','987654323','carla@example.com');
INSERT INTO plan(nombre,duracion_dias,precio,descripcion) VALUES
('Pase diario',1,15.00,'Acceso por un dia'),
('Mensual',30,90.00,'Acceso por 30 dias'),
('Trimestral',90,240.00,'Acceso por 90 dias');
-- Fechas relativas al dia de importacion para facilitar la demostracion.
INSERT INTO membresia(id_cliente,id_plan,fecha_inicio,fecha_fin,precio_pactado) VALUES
(1,2,CURDATE(),DATE_ADD(CURDATE(),INTERVAL 29 DAY),90.00),
(2,2,CURDATE(),DATE_ADD(CURDATE(),INTERVAL 29 DAY),90.00),
(3,1,DATE_SUB(CURDATE(),INTERVAL 2 DAY),DATE_SUB(CURDATE(),INTERVAL 2 DAY),15.00);
INSERT INTO pago(id_membresia,monto,fecha_pago,metodo_pago) VALUES
(1,90.00,NOW(),'Efectivo'),(3,15.00,DATE_SUB(NOW(),INTERVAL 2 DAY),'Transferencia');
