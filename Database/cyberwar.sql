/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50720
Source Host           : localhost:3306
Source Database       : cyberwar

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2018-04-07 22:56:29
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for accounts
-- ----------------------------
DROP TABLE IF EXISTS `accounts`;
CREATE TABLE `accounts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(18) DEFAULT NULL,
  `password` varchar(170) DEFAULT NULL,
  `status` int(3) DEFAULT '0',
  `session_key` varchar(170) DEFAULT NULL,
  `money` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of accounts
-- ----------------------------
INSERT INTO `accounts` VALUES ('15', 'test', '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', '0', '16ca6269bab5c1e9ec1c1977140b3f81181c7fa3ef60c738c92b92d617cddabd', '308015');

-- ----------------------------
-- Table structure for inventory
-- ----------------------------
DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory` (
  `p_id` int(11) DEFAULT NULL,
  `item_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of inventory
-- ----------------------------
INSERT INTO `inventory` VALUES ('10', '1000');
INSERT INTO `inventory` VALUES ('10', '2000');
INSERT INTO `inventory` VALUES ('10', '3001');
INSERT INTO `inventory` VALUES ('10', '1001');
INSERT INTO `inventory` VALUES ('10', '2001');

-- ----------------------------
-- Table structure for items
-- ----------------------------
DROP TABLE IF EXISTS `items`;
CREATE TABLE `items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `team` int(2) DEFAULT NULL,
  `slot` int(4) DEFAULT NULL,
  `sub_slot` int(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10001 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of items
-- ----------------------------
INSERT INTO `items` VALUES ('1000', '2', '0', '0');
INSERT INTO `items` VALUES ('2000', '1', '0', '0');

-- ----------------------------
-- Table structure for players
-- ----------------------------
DROP TABLE IF EXISTS `players`;
CREATE TABLE `players` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `a_id` int(11) DEFAULT NULL,
  `name` varchar(14) DEFAULT NULL,
  `points` int(11) DEFAULT '0',
  `red_character` int(11) DEFAULT '20000',
  `blue_character` int(11) DEFAULT '10000',
  `weapon_primary` int(11) DEFAULT '0',
  `weapon_secondary` int(11) DEFAULT '0',
  `weapon_melee` int(11) DEFAULT '0',
  `weapon_throw` int(11) DEFAULT '0',
  `weapon_special` int(11) DEFAULT '0',
  `wins` int(11) DEFAULT '0',
  `lose` int(11) DEFAULT '0',
  `kills` int(11) DEFAULT '0',
  `assists` int(11) DEFAULT '0',
  `deaths` int(11) DEFAULT '0',
  `exp` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of players
-- ----------------------------
INSERT INTO `players` VALUES ('10', '15', 'Admin', '1000000', '1000', '2000', '1002', '2001', '3001', '0', '0', '0', '0', '0', '0', '0', '514080');

-- ----------------------------
-- Table structure for servers
-- ----------------------------
DROP TABLE IF EXISTS `servers`;
CREATE TABLE `servers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(2) DEFAULT '0',
  `ip` varchar(16) DEFAULT NULL,
  `port` int(6) DEFAULT NULL,
  `max_players` int(11) DEFAULT '200',
  `current_players` int(11) DEFAULT '0',
  `game_mode` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of servers
-- ----------------------------
