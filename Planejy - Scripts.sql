CREATE TABLE `Usuario`(
    `Id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `Nome` VARCHAR(120) NOT NULL,
    `Nascimento` DATE NOT NULL,
    `Nick` VARCHAR(40) NOT NULL,
    `Senha` VARCHAR(20) NOT NULL,
    `Email` VARCHAR(120) NOT NULL,
    `Sexo` CHAR(1) NULL,
    `Token` VARCHAR(80) NULL
);
ALTER TABLE
    `Usuario` ADD UNIQUE `usuario_nick_unique`(`Nick`);
ALTER TABLE
    `Usuario` ADD UNIQUE `usuario_email_unique`(`Email`);
CREATE TABLE `Notas`(
    `Chave` BIGINT NOT NULL,
    `Id_usuario` INT UNSIGNED NOT NULL AUTO_INCREMENT INDEX,
    `Titulo` VARCHAR(40) NOT NULL,
    `Descricao` VARCHAR(255) NULL,
    `Horario` TIME NULL,
    `Categoria` VARCHAR(40) NOT NULL,
    `Cor` CHAR(7) NOT NULL
);
ALTER TABLE
    `Notas` ADD INDEX `notas_id_usuario_index`(`Id_usuario`);
ALTER TABLE
    `Notas` ADD PRIMARY KEY(`Chave`);
ALTER TABLE
    `Notas` ADD CONSTRAINT `notas_id_usuario_foreign` FOREIGN KEY(`Id_usuario`) REFERENCES `Usuario`(`Id`);