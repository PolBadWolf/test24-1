/*
 Navicat Premium Data Transfer

 Source Server         : 12
 Source Server Type    : MySQL
 Source Server Version : 80020
 Source Host           : localhost:3306
 Source Schema         : spc3

 Target Server Type    : MySQL
 Target Server Version : 80020
 File Encoding         : 65001

 Date: 29/09/2020 16:31:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for logger_pushers
-- ----------------------------
DROP TABLE IF EXISTS `logger_pushers`;
CREATE TABLE `logger_pushers`  (
  `id_loggerPusher` int NOT NULL AUTO_INCREMENT,
  `date_upd` datetime(0) NOT NULL COMMENT 'дата изменения толкателя',
  `id_loggerUserEdit` int NOT NULL COMMENT 'кто изменил',
  `id_pusher` int NOT NULL COMMENT 'индификатор толкателя',
  `namePusher` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'инвентарный номер толкателя',
  `id_loggerTypePusher` int NOT NULL COMMENT 'индификатор типа толкателя',
  PRIMARY KEY (`id_loggerPusher`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for logger_type_pushers
-- ----------------------------
DROP TABLE IF EXISTS `logger_type_pushers`;
CREATE TABLE `logger_type_pushers`  (
  `id_loggerTypePusher` int NOT NULL AUTO_INCREMENT,
  `data_upd` datetime(0) NULL DEFAULT NULL COMMENT 'дата изменения типа',
  `id_loggerUserEdit` int NULL DEFAULT NULL COMMENT 'кто исправил',
  `id_typePusher` int NOT NULL COMMENT 'индификатор типа',
  `nameType` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'название типа',
  `forceNominal` int NULL DEFAULT NULL COMMENT 'номинальное усилие',
  `moveNominal` int NULL DEFAULT NULL COMMENT 'номинальный ход минимальный',
  `unclenchingTime` int NULL DEFAULT NULL COMMENT 'номинальный ход максимальный',
  PRIMARY KEY (`id_loggerTypePusher`, `id_typePusher`) USING BTREE,
  INDEX `id_typePusher`(`id_typePusher`) USING BTREE,
  INDEX `id_loggerTypePusher`(`id_loggerTypePusher`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for logger_users
-- ----------------------------
DROP TABLE IF EXISTS `logger_users`;
CREATE TABLE `logger_users`  (
  `id_loggerUser` int NOT NULL AUTO_INCREMENT,
  `date_upd` datetime(0) NULL DEFAULT NULL,
  `id_loggerUserEdit` int NOT NULL,
  `id_user` int NOT NULL,
  `surName` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `userPassword` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `rang` int NULL DEFAULT NULL,
  PRIMARY KEY (`id_loggerUser`, `id_user`, `id_loggerUserEdit`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for table_data
-- ----------------------------
DROP TABLE IF EXISTS `table_data`;
CREATE TABLE `table_data`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dateTime` datetime(0) NULL DEFAULT NULL,
  `id_spec` bigint NULL DEFAULT NULL,
  `n_cicle` int NULL DEFAULT NULL,
  `ves` int NULL DEFAULT NULL,
  `tik_shelf` int NULL DEFAULT NULL,
  `tik_back` int NULL DEFAULT NULL,
  `tik_stop` int NULL DEFAULT NULL,
  `dis` longblob NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for table_pushers
-- ----------------------------
DROP TABLE IF EXISTS `table_pushers`;
CREATE TABLE `table_pushers`  (
  `id_pusher` int NOT NULL AUTO_INCREMENT,
  `date_reg` datetime(0) NULL DEFAULT NULL COMMENT 'дата регистрации толкателя',
  `id_loggerPusher` int NULL DEFAULT NULL COMMENT 'указатель на данные по толкателю',
  `date_unreg` datetime(0) NULL DEFAULT NULL COMMENT 'дата деактивации толкателя',
  PRIMARY KEY (`id_pusher`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for table_spec
-- ----------------------------
DROP TABLE IF EXISTS `table_spec`;
CREATE TABLE `table_spec`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_users` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for table_users
-- ----------------------------
DROP TABLE IF EXISTS `table_users`;
CREATE TABLE `table_users`  (
  `id_user` int NOT NULL AUTO_INCREMENT,
  `date_reg` datetime(0) NOT NULL,
  `id_loggerUser` int NOT NULL,
  `date_unreg` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id_user`, `id_loggerUser`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for type_pushers
-- ----------------------------
DROP TABLE IF EXISTS `type_pushers`;
CREATE TABLE `type_pushers`  (
  `id_typePusher` int NOT NULL AUTO_INCREMENT COMMENT 'индификатор типа толкателя',
  `date_reg` datetime(0) NULL DEFAULT NULL COMMENT 'дата регистрации типа',
  `id_loggerTypePusher` int NOT NULL COMMENT 'указатель на данные по типу',
  `date_unreg` datetime(0) NULL DEFAULT NULL COMMENT 'дата деактивации типа',
  PRIMARY KEY (`id_typePusher`) USING BTREE,
  INDEX `id_loggerTypePusher`(`id_loggerTypePusher`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
