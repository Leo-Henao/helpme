USE helpme_iud;

-- ROLES
INSERT INTO roles (id, nombre, descripcion)
VALUES (1, 'ROLE_ADMIN', 'Administrador');

INSERT INTO roles (id, nombre, descripcion)
VALUES (2, 'ROLE_USER', 'Usuario');

-- USUARIOS
INSERT INTO usuarios
(id, username, nombre, apellido, password, fecha_nacimiento, enabled, red_social, image)
VALUES
(1, 'leohenaosis@gmail.com', 'Jorge', 'Henao',
'', '1996-11-24', 1, 0,
'https://w7.pngwing.com/pngs/81/570/png-transparent-profile-logo-computer-icons-user-user-blue-heroes-logo.png');

-- ASIGNAR ROL
INSERT INTO roles_usuarios(usuarios_id, roles_id)
VALUES
(1, 1);
INSERT INTO roles_usuarios(usuarios_id, roles_id)
VALUES
(1, 2);
