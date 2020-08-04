/*
 Navicat Premium Data Transfer

 Source Server         : 12
 Source Server Type    : MySQL
 Source Server Version : 80020
 Source Host           : localhost:3306
 Source Schema         : spc1

 Target Server Type    : MySQL
 Target Server Version : 80020
 File Encoding         : 65001

 Date: 04/08/2020 16:14:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of table_data
-- ----------------------------
INSERT INTO `table_data` VALUES (1, '2020-08-04 15:43:06', 0, 0, 0, 0, 0, 0, 0x0C0000000D00FFFF00000040);
INSERT INTO `table_data` VALUES (2, '2020-08-04 15:43:48', 0, 0, 0, 0, 0, 0, 0x0C0000000D00FFFF00000040);
INSERT INTO `table_data` VALUES (3, '2020-08-04 16:12:07', 0, 0, 0, 0, 0, 0, 0x0C0000000D00FFFF00000040);

SET FOREIGN_KEY_CHECKS = 1;
