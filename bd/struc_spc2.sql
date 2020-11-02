/*
 Navicat Premium Data Transfer

 Source Server         : 12
 Source Server Type    : MySQL
 Source Server Version : 80021
 Source Host           : localhost:3306
 Source Schema         : bas4

 Target Server Type    : MySQL
 Target Server Version : 80021
 File Encoding         : 65001

 Date: 26/10/2020 22:56:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for data_spec
-- ----------------------------
DROP TABLE IF EXISTS `data_spec`;
CREATE TABLE `data_spec`  (
  `id_dataSpec` int NOT NULL AUTO_INCREMENT,
  `date_upd` datetime(0) NULL DEFAULT NULL,
  `id_user` int NULL DEFAULT NULL,
  `id_pusher` int NULL DEFAULT NULL,
  PRIMARY KEY (`id_dataSpec`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for datas
-- ----------------------------
DROP TABLE IF EXISTS `datas`;
CREATE TABLE `datas`  (
  `id_data` bigint NOT NULL AUTO_INCREMENT,
  `dateTime` datetime(0) NULL DEFAULT NULL,
  `id_spec` bigint NULL DEFAULT NULL,
  `n_cicle` int NULL DEFAULT NULL,
  `ves` int NULL DEFAULT NULL,
  `tik_shelf` int NULL DEFAULT NULL,
  `tik_back` int NULL DEFAULT NULL,
  `tik_stop` int NULL DEFAULT NULL,
  `forceNominal` int NULL DEFAULT NULL,
  `moveNominal` int NULL DEFAULT NULL,
  `unclenchingTime` int NULL DEFAULT NULL,
  `dataMeasured` longblob NULL,
  PRIMARY KEY (`id_data`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for pushers
-- ----------------------------
DROP TABLE IF EXISTS `pushers`;
CREATE TABLE `pushers`  (
  `id_pusher` int NOT NULL AUTO_INCREMENT,
  `date_reg` datetime(0) NULL DEFAULT NULL COMMENT 'дата регистрации толкателя',
  `id_loggerPusher` int NULL DEFAULT NULL COMMENT 'указатель на данные по толкателю',
  `date_unreg` datetime(0) NULL DEFAULT NULL COMMENT 'дата деактивации толкателя',
  PRIMARY KEY (`id_pusher`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for pushers_logger
-- ----------------------------
DROP TABLE IF EXISTS `pushers_logger`;
CREATE TABLE `pushers_logger`  (
  `id_loggerPusher` int NOT NULL AUTO_INCREMENT,
  `date_upd` datetime(0) NOT NULL COMMENT 'дата изменения толкателя',
  `id_loggerUserEdit` int NOT NULL COMMENT 'кто изменил',
  `id_pusher` int NOT NULL COMMENT 'индификатор толкателя',
  `namePusher` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'инвентарный номер толкателя',
  `id_typePusher` int NOT NULL COMMENT 'индификатор типа толкателя',
  PRIMARY KEY (`id_loggerPusher`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for pusherstype
-- ----------------------------
DROP TABLE IF EXISTS `pusherstype`;
CREATE TABLE `pusherstype`  (
  `id_typePusher` int NOT NULL AUTO_INCREMENT COMMENT 'индификатор типа толкателя',
  `date_reg` datetime(0) NULL DEFAULT NULL COMMENT 'дата регистрации типа',
  `id_loggerTypePusher` int NOT NULL COMMENT 'указатель на данные по типу',
  `date_unreg` datetime(0) NULL DEFAULT NULL COMMENT 'дата деактивации типа',
  PRIMARY KEY (`id_typePusher`) USING BTREE,
  INDEX `id_loggerTypePusher`(`id_loggerTypePusher`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for pusherstype_logger
-- ----------------------------
DROP TABLE IF EXISTS `pusherstype_logger`;
CREATE TABLE `pusherstype_logger`  (
  `id_loggerTypePusher` int NOT NULL AUTO_INCREMENT,
  `date_upd` datetime(0) NULL DEFAULT NULL COMMENT 'дата изменения типа',
  `id_loggerUserEdit` int NULL DEFAULT NULL COMMENT 'кто исправил',
  `id_typePusher` int NOT NULL COMMENT 'индификатор типа',
  `nameType` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'название типа',
  `forceNominal` int NULL DEFAULT NULL COMMENT 'номинальное усилие',
  `moveNominal` int NULL DEFAULT NULL COMMENT 'номинальный ход минимальный',
  `unclenchingTime` int NULL DEFAULT NULL COMMENT 'номинальный ход максимальный',
  PRIMARY KEY (`id_loggerTypePusher`, `id_typePusher`) USING BTREE,
  INDEX `id_typePusher`(`id_typePusher`) USING BTREE,
  INDEX `id_loggerTypePusher`(`id_loggerTypePusher`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id_user` int NOT NULL AUTO_INCREMENT,
  `date_reg` datetime(0) NOT NULL,
  `id_loggerUser` int NOT NULL,
  `date_unreg` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id_user`, `id_loggerUser`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for users_logger
-- ----------------------------
DROP TABLE IF EXISTS `users_logger`;
CREATE TABLE `users_logger`  (
  `id_loggerUser` int NOT NULL AUTO_INCREMENT,
  `date_upd` datetime(0) NULL DEFAULT NULL,
  `id_loggerUserEdit` int NOT NULL,
  `id_user` int NOT NULL,
  `surName` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `userPassword` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `rang` int NULL DEFAULT NULL,
  PRIMARY KEY (`id_loggerUser`, `id_user`, `id_loggerUserEdit`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
