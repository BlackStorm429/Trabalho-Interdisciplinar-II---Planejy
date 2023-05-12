CREATE TABLE `Calendario`(
    `Id` INT NOT NULL,
    `Data` CHAR(10) NOT NULL,
    `Usuario_Id` INT NOT NULL
);
ALTER TABLE
    `Calendario` ADD PRIMARY KEY(`Id`);
ALTER TABLE
    `Calendario` ADD INDEX `calendario_usuario_id_index`(`Usuario_Id`);
CREATE TABLE `Artigos`(
    `Codigo` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `URL` VARCHAR(300) NOT NULL,
    `Nome` VARCHAR(45) NOT NULL,
    `Descricao` VARCHAR(500) NULL,
    `Usuario_Id` INT NOT NULL
);
ALTER TABLE
    `Artigos` ADD PRIMARY KEY(`Codigo`);
ALTER TABLE
    `Artigos` ADD INDEX `artigos_usuario_id_index`(`Usuario_Id`);
CREATE TABLE `Possui`(
    `Usuario_Id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `Artigos_Codigo` INT NOT NULL,
    `Artigos_Usuario_Id` INT NOT NULL
);
ALTER TABLE
    `Possui` ADD PRIMARY KEY(`Usuario_Id`);
ALTER TABLE
    `Possui` ADD PRIMARY KEY(`Artigos_Codigo`);
ALTER TABLE
    `Possui` ADD PRIMARY KEY(`Artigos_Usuario_Id`);
CREATE TABLE `Usuario`(
    `Id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `Username` VARCHAR(30) NOT NULL,
    `Senha` VARCHAR(20) NOT NULL,
    `Nome` VARCHAR(45) NOT NULL,
    `Idade` INT NOT NULL,
    `Token` VARCHAR(20) NOT NULL,
    `Email` VARCHAR(255) NOT NULL
);
ALTER TABLE
    `Usuario` ADD PRIMARY KEY(`Id`);
CREATE TABLE `Tarefas`(
    `Id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `Descricao` VARCHAR(250) NOT NULL,
    `Usuario` INT NOT NULL
);
ALTER TABLE
    `Tarefas` ADD PRIMARY KEY(`Id`);
ALTER TABLE
    `Tarefas` ADD INDEX `tarefas_usuario_index`(`Usuario`);
ALTER TABLE
    `Possui` ADD CONSTRAINT `possui_artigos_codigo_foreign` FOREIGN KEY(`Artigos_Codigo`) REFERENCES `Artigos`(`Codigo`);
ALTER TABLE
    `Tarefas` ADD CONSTRAINT `tarefas_usuario_foreign` FOREIGN KEY(`Usuario`) REFERENCES `Usuario`(`Id`);
ALTER TABLE
    `Calendario` ADD CONSTRAINT `calendario_usuario_id_foreign` FOREIGN KEY(`Usuario_Id`) REFERENCES `Usuario`(`Id`);
ALTER TABLE
    `Artigos` ADD CONSTRAINT `artigos_usuario_id_foreign` FOREIGN KEY(`Usuario_Id`) REFERENCES `Usuario`(`Id`);
ALTER TABLE
    `Possui` ADD CONSTRAINT `possui_usuario_id_foreign` FOREIGN KEY(`Usuario_Id`) REFERENCES `Usuario`(`Id`);