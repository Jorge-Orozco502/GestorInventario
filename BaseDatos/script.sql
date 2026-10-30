-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema inventario_vidrieria
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema inventario_vidrieria
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `inventario_vidrieria` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ;
USE `inventario_vidrieria` ;

-- -----------------------------------------------------
-- Table `inventario_vidrieria`.`cliente`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inventario_vidrieria`.`cliente` (
  `codigo` VARCHAR(30) NOT NULL,
  `nombre` VARCHAR(150) NOT NULL,
  `nit_cf` VARCHAR(20) NOT NULL,
  `telefono` VARCHAR(30) NULL DEFAULT NULL,
  `direccion` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`codigo`),
  INDEX `idx_cliente_nombre` (`nombre` ASC) VISIBLE,
  INDEX `idx_cliente_nit_cf` (`nit_cf` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `inventario_vidrieria`.`proveedor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inventario_vidrieria`.`proveedor` (
  `codigo` VARCHAR(30) NOT NULL,
  `nombre_empresa` VARCHAR(150) NOT NULL,
  `nit` VARCHAR(20) NOT NULL,
  `telefono` VARCHAR(30) NULL DEFAULT NULL,
  `direccion` VARCHAR(255) NULL DEFAULT NULL,
  `correo_electronico` VARCHAR(150) NULL DEFAULT NULL,
  PRIMARY KEY (`codigo`),
  UNIQUE INDEX `uq_proveedor_nit` (`nit` ASC) VISIBLE,
  INDEX `idx_proveedor_nombre` (`nombre_empresa` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `inventario_vidrieria`.`usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inventario_vidrieria`.`usuario` (
  `id_usuario` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `tipo_usuario` ENUM('ADMINISTRADOR', 'EMPLEADO') NOT NULL,
  `nombre_usuario` VARCHAR(50) NOT NULL,
  `contrasena_hash` VARCHAR(255) NOT NULL,
  `nombre_completo` VARCHAR(150) NOT NULL,
  `activo` TINYINT(1) NOT NULL DEFAULT '1',
  `fecha_creacion` DATE NOT NULL DEFAULT (CURRENT_DATE),
  PRIMARY KEY (`id_usuario`),
  UNIQUE INDEX `uq_usuario_nombre_usuario` (`nombre_usuario` ASC) VISIBLE,
  UNIQUE INDEX `uq_usuario_id_tipo` (`id_usuario` ASC, `tipo_usuario` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 5
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `inventario_vidrieria`.`compra`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inventario_vidrieria`.`compra` (
  `numero_compra` VARCHAR(30) NOT NULL,
  `fecha` DATE NOT NULL,
  `codigo_proveedor` VARCHAR(30) NOT NULL,
  `id_usuario_responsable` INT UNSIGNED NOT NULL,
  `forma_pago` ENUM('EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'CREDITO') NOT NULL,
  `total` DECIMAL(14,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`numero_compra`),
  INDEX `idx_compra_fecha` (`fecha` ASC) VISIBLE,
  INDEX `idx_compra_proveedor` (`codigo_proveedor` ASC) VISIBLE,
  INDEX `idx_compra_usuario` (`id_usuario_responsable` ASC) VISIBLE,
  CONSTRAINT `fk_compra_proveedor`
    FOREIGN KEY (`codigo_proveedor`)
    REFERENCES `inventario_vidrieria`.`proveedor` (`codigo`),
  CONSTRAINT `fk_compra_usuario`
    FOREIGN KEY (`id_usuario_responsable`)
    REFERENCES `inventario_vidrieria`.`usuario` (`id_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `inventario_vidrieria`.`consecutivo_venta`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inventario_vidrieria`.`consecutivo_venta` (
  `id` INT NOT NULL,
  `ultimo_numero` BIGINT NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `inventario_vidrieria`.`producto`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inventario_vidrieria`.`producto` (
  `codigo` VARCHAR(30) NOT NULL,
  `tipo_producto` ENUM('MATERIA_PRIMA', 'HERRAMIENTA', 'PRODUCTO_TERMINADO') NOT NULL,
  `nombre` VARCHAR(150) NOT NULL,
  `categoria` VARCHAR(100) NOT NULL,
  `descripcion` VARCHAR(500) NULL DEFAULT NULL,
  `unidad_medida` VARCHAR(30) NOT NULL,
  `precio_compra` DECIMAL(12,2) NOT NULL DEFAULT '0.00',
  `precio_venta` DECIMAL(12,2) NOT NULL DEFAULT '0.00',
  `existencia_actual` DECIMAL(14,3) NOT NULL DEFAULT '0.000',
  `existencia_minima` DECIMAL(14,3) NOT NULL DEFAULT '0.000',
  `estado` ENUM('ACTIVO', 'INACTIVO', 'AGOTADO', 'BAJO_EN_STOCK') NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (`codigo`),
  UNIQUE INDEX `uq_producto_codigo_tipo` (`codigo` ASC, `tipo_producto` ASC) VISIBLE,
  INDEX `idx_producto_nombre` (`nombre` ASC) VISIBLE,
  INDEX `idx_producto_categoria` (`categoria` ASC) VISIBLE,
  INDEX `idx_producto_tipo` (`tipo_producto` ASC) VISIBLE,
  INDEX `idx_producto_estado` (`estado` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `inventario_vidrieria`.`detalle_compra`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inventario_vidrieria`.`detalle_compra` (
  `id_detalle_compra` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `numero_compra` VARCHAR(30) NOT NULL,
  `codigo_producto` VARCHAR(30) NOT NULL,
  `cantidad` DECIMAL(14,3) NOT NULL,
  `costo_unitario` DECIMAL(12,2) NOT NULL,
  `subtotal` DECIMAL(14,2) NOT NULL,
  PRIMARY KEY (`id_detalle_compra`),
  INDEX `idx_detalle_compra_compra` (`numero_compra` ASC) VISIBLE,
  INDEX `idx_detalle_compra_producto` (`codigo_producto` ASC) VISIBLE,
  CONSTRAINT `fk_detalle_compra_compra`
    FOREIGN KEY (`numero_compra`)
    REFERENCES `inventario_vidrieria`.`compra` (`numero_compra`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_detalle_compra_producto`
    FOREIGN KEY (`codigo_producto`)
    REFERENCES `inventario_vidrieria`.`producto` (`codigo`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `inventario_vidrieria`.`venta`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inventario_vidrieria`.`venta` (
  `numero_venta` VARCHAR(30) NOT NULL,
  `fecha` DATE NOT NULL,
  `codigo_cliente` VARCHAR(30) NOT NULL,
  `nombre_cf` VARCHAR(150) NULL DEFAULT NULL,
  `direccion_cf` VARCHAR(255) NULL DEFAULT NULL,
  `id_usuario_responsable` INT UNSIGNED NOT NULL,
  `forma_pago` ENUM('EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'CREDITO') NOT NULL,
  `subtotal_general` DECIMAL(14,2) NOT NULL DEFAULT '0.00',
  `total_descuentos` DECIMAL(14,2) NOT NULL DEFAULT '0.00',
  `total_iva` DECIMAL(14,2) NOT NULL DEFAULT '0.00',
  `total_final` DECIMAL(14,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`numero_venta`),
  INDEX `idx_venta_fecha` (`fecha` ASC) VISIBLE,
  INDEX `idx_venta_cliente` (`codigo_cliente` ASC) VISIBLE,
  INDEX `idx_venta_usuario` (`id_usuario_responsable` ASC) VISIBLE,
  CONSTRAINT `fk_venta_cliente`
    FOREIGN KEY (`codigo_cliente`)
    REFERENCES `inventario_vidrieria`.`cliente` (`codigo`),
  CONSTRAINT `fk_venta_usuario`
    FOREIGN KEY (`id_usuario_responsable`)
    REFERENCES `inventario_vidrieria`.`usuario` (`id_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `inventario_vidrieria`.`detalle_venta`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inventario_vidrieria`.`detalle_venta` (
  `id_detalle_venta` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `numero_venta` VARCHAR(30) NOT NULL,
  `codigo_producto` VARCHAR(30) NOT NULL,
  `cantidad_solicitada` DECIMAL(14,3) NOT NULL,
  `precio_unitario` DECIMAL(12,2) NOT NULL,
  `subtotal` DECIMAL(14,2) NOT NULL,
  `iva_linea` DECIMAL(14,2) NOT NULL DEFAULT '0.00',
  `total_linea` DECIMAL(14,2) NOT NULL,
  PRIMARY KEY (`id_detalle_venta`),
  INDEX `idx_detalle_venta_venta` (`numero_venta` ASC) VISIBLE,
  INDEX `idx_detalle_venta_producto` (`codigo_producto` ASC) VISIBLE,
  CONSTRAINT `fk_detalle_venta_producto`
    FOREIGN KEY (`codigo_producto`)
    REFERENCES `inventario_vidrieria`.`producto` (`codigo`),
  CONSTRAINT `fk_detalle_venta_venta`
    FOREIGN KEY (`numero_venta`)
    REFERENCES `inventario_vidrieria`.`venta` (`numero_venta`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 12
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `inventario_vidrieria`.`empleado_accion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inventario_vidrieria`.`empleado_accion` (
  `id_usuario` INT UNSIGNED NOT NULL,
  `tipo_usuario` ENUM('ADMINISTRADOR', 'EMPLEADO') NOT NULL DEFAULT 'EMPLEADO',
  `accion` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id_usuario`, `accion`),
  INDEX `fk_empleado_accion_usuario` (`id_usuario` ASC, `tipo_usuario` ASC) VISIBLE,
  CONSTRAINT `fk_empleado_accion_usuario`
    FOREIGN KEY (`id_usuario` , `tipo_usuario`)
    REFERENCES `inventario_vidrieria`.`usuario` (`id_usuario` , `tipo_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `inventario_vidrieria`.`trabajo_fabricacion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inventario_vidrieria`.`trabajo_fabricacion` (
  `numero_trabajo` VARCHAR(30) NOT NULL,
  `fecha` DATE NOT NULL,
  `descripcion` VARCHAR(500) NULL DEFAULT NULL,
  `codigo_producto_terminado` VARCHAR(30) NOT NULL,
  `tipo_producto` ENUM('MATERIA_PRIMA', 'HERRAMIENTA', 'PRODUCTO_TERMINADO') NOT NULL DEFAULT 'PRODUCTO_TERMINADO',
  `id_usuario_responsable` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`numero_trabajo`),
  INDEX `fk_trabajo_producto_terminado` (`codigo_producto_terminado` ASC, `tipo_producto` ASC) VISIBLE,
  INDEX `idx_trabajo_fecha` (`fecha` ASC) VISIBLE,
  INDEX `idx_trabajo_producto` (`codigo_producto_terminado` ASC) VISIBLE,
  INDEX `idx_trabajo_usuario` (`id_usuario_responsable` ASC) VISIBLE,
  CONSTRAINT `fk_trabajo_producto_terminado`
    FOREIGN KEY (`codigo_producto_terminado` , `tipo_producto`)
    REFERENCES `inventario_vidrieria`.`producto` (`codigo` , `tipo_producto`),
  CONSTRAINT `fk_trabajo_usuario`
    FOREIGN KEY (`id_usuario_responsable`)
    REFERENCES `inventario_vidrieria`.`usuario` (`id_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `inventario_vidrieria`.`material_utilizado`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inventario_vidrieria`.`material_utilizado` (
  `id_material_utilizado` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `numero_trabajo` VARCHAR(30) NOT NULL,
  `codigo_materia_prima` VARCHAR(30) NOT NULL,
  `tipo_producto` ENUM('MATERIA_PRIMA', 'HERRAMIENTA', 'PRODUCTO_TERMINADO') NOT NULL DEFAULT 'MATERIA_PRIMA',
  `cantidad_utilizada` DECIMAL(14,3) NOT NULL,
  PRIMARY KEY (`id_material_utilizado`),
  INDEX `fk_material_utilizado_materia_prima` (`codigo_materia_prima` ASC, `tipo_producto` ASC) VISIBLE,
  INDEX `idx_material_trabajo` (`numero_trabajo` ASC) VISIBLE,
  INDEX `idx_material_materia` (`codigo_materia_prima` ASC) VISIBLE,
  CONSTRAINT `fk_material_utilizado_materia_prima`
    FOREIGN KEY (`codigo_materia_prima` , `tipo_producto`)
    REFERENCES `inventario_vidrieria`.`producto` (`codigo` , `tipo_producto`),
  CONSTRAINT `fk_material_utilizado_trabajo`
    FOREIGN KEY (`numero_trabajo`)
    REFERENCES `inventario_vidrieria`.`trabajo_fabricacion` (`numero_trabajo`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `inventario_vidrieria`.`movimiento_inventario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inventario_vidrieria`.`movimiento_inventario` (
  `id_movimiento` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `fecha` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `codigo_producto` VARCHAR(30) NOT NULL,
  `tipo_movimiento` ENUM('ENTRADA_COMPRA', 'SALIDA_VENTA', 'ENTRADA_AJUSTE', 'SALIDA_AJUSTE', 'SALIDA_FABRICACION') NOT NULL,
  `cantidad` DECIMAL(14,3) NOT NULL,
  `existencia_anterior` DECIMAL(14,3) NOT NULL,
  `existencia_nueva` DECIMAL(14,3) NOT NULL,
  `id_usuario` INT UNSIGNED NOT NULL,
  `referencia_documento` VARCHAR(50) NULL DEFAULT NULL,
  `motivo` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_movimiento`),
  INDEX `idx_movimiento_fecha` (`fecha` ASC) VISIBLE,
  INDEX `idx_movimiento_producto` (`codigo_producto` ASC, `fecha` ASC) VISIBLE,
  INDEX `idx_movimiento_tipo` (`tipo_movimiento` ASC) VISIBLE,
  INDEX `idx_movimiento_usuario` (`id_usuario` ASC) VISIBLE,
  INDEX `idx_movimiento_referencia` (`referencia_documento` ASC) VISIBLE,
  CONSTRAINT `fk_movimiento_producto`
    FOREIGN KEY (`codigo_producto`)
    REFERENCES `inventario_vidrieria`.`producto` (`codigo`),
  CONSTRAINT `fk_movimiento_usuario`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `inventario_vidrieria`.`usuario` (`id_usuario`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `inventario_vidrieria`.`plancha_vidrio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `inventario_vidrieria`.`plancha_vidrio` (
  `codigo` VARCHAR(30) NOT NULL,
  `tipo_producto` ENUM('MATERIA_PRIMA', 'HERRAMIENTA', 'PRODUCTO_TERMINADO') NOT NULL DEFAULT 'MATERIA_PRIMA',
  `tipo_vidrio` ENUM('CLARO', 'BRONCE', 'GRIS', 'NEGRO', 'REFLECTIVO_BRONCE', 'REFLECTIVO_AZUL', 'ESMERILADO', 'LAMINADO', 'TEMPLADO') NOT NULL,
  `espesor_mm` INT UNSIGNED NOT NULL,
  `ancho_mm` INT UNSIGNED NOT NULL,
  `largo_mm` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`codigo`),
  INDEX `fk_plancha_vidrio_producto` (`codigo` ASC, `tipo_producto` ASC) VISIBLE,
  CONSTRAINT `fk_plancha_vidrio_producto`
    FOREIGN KEY (`codigo` , `tipo_producto`)
    REFERENCES `inventario_vidrieria`.`producto` (`codigo` , `tipo_producto`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

USE `inventario_vidrieria`;

DELIMITER $$
USE `inventario_vidrieria`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `inventario_vidrieria`.`trg_producto_estado_insert`
BEFORE INSERT ON `inventario_vidrieria`.`producto`
FOR EACH ROW
BEGIN
    IF NEW.estado <> 'INACTIVO' THEN
        IF NEW.existencia_actual <= 0 THEN
            SET NEW.estado = 'AGOTADO';
        ELSEIF NEW.existencia_actual <= NEW.existencia_minima THEN
            SET NEW.estado = 'BAJO_EN_STOCK';
        ELSE
            SET NEW.estado = 'ACTIVO';
        END IF;
    END IF;
END$$

USE `inventario_vidrieria`$$
CREATE
DEFINER=`root`@`localhost`
TRIGGER `inventario_vidrieria`.`trg_producto_estado_update`
BEFORE UPDATE ON `inventario_vidrieria`.`producto`
FOR EACH ROW
BEGIN
    IF NEW.estado <> 'INACTIVO' THEN
        IF NEW.existencia_actual <= 0 THEN
            SET NEW.estado = 'AGOTADO';
        ELSEIF NEW.existencia_actual <= NEW.existencia_minima THEN
            SET NEW.estado = 'BAJO_EN_STOCK';
        ELSE
            SET NEW.estado = 'ACTIVO';
        END IF;
    END IF;
END$$


DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;



INSERT INTO usuario (tipo_usuario, nombre_usuario, contrasena_hash, nombre_completo, activo, fecha_creacion) 
VALUES ('ADMINISTRADOR', 'admin_sistema', '$10$dNmDV3r9LvVYkZFvdSSNHudQN8/TEUP7A8Q./26Dv1sUQGMLeahBq', 'Administrador Principal', 1, '2026-10-21');


   UPDATE usuario
   SET contrasena_hash = '$2a$10$dNmDV3r9LvVYkZFvdSSNHudQN8/TEUP7A8Q./26Dv1sUQGMLeahBq'
   WHERE nombre_usuario = 'admin_sistema';