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

 Date: 23/09/2020 16:31:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for logger_pushers
-- ----------------------------
DROP TABLE IF EXISTS `logger_pushers`;
CREATE TABLE `logger_pushers`  (
  `id_loggerPushers` int NOT NULL AUTO_INCREMENT,
  `date` datetime(0) NULL DEFAULT NULL,
  `id_loggerUserEdit` int NOT NULL,
  `id_pusher` int NOT NULL,
  PRIMARY KEY (`id_loggerPushers`, `id_pusher`, `id_loggerUserEdit`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for logger_users
-- ----------------------------
DROP TABLE IF EXISTS `logger_users`;
CREATE TABLE `logger_users`  (
  `id_loggerUser` int NOT NULL AUTO_INCREMENT,
  `date` datetime(0) NULL DEFAULT NULL,
  `id_loggerUserEdit` int NOT NULL,
  `id_user` int NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `rang` int NULL DEFAULT NULL,
  PRIMARY KEY (`id_loggerUser`, `id_user`, `id_loggerUserEdit`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for table_pushers
-- ----------------------------
DROP TABLE IF EXISTS `table_pushers`;
CREATE TABLE `table_pushers`  (
  `id_pusher` int NOT NULL AUTO_INCREMENT,
  `date_reg` datetime(0) NULL DEFAULT NULL,
  `date_unreg` datetime(0) NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `id_unreg` int NULL DEFAULT NULL,
  PRIMARY KEY (`id_pusher`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for table_spec
-- ----------------------------
DROP TABLE IF EXISTS `table_spec`;
CREATE TABLE `table_spec`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_users` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
