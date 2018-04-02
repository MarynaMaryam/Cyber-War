/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50720
Source Host           : localhost:3306
Source Database       : cyberwar

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2018-04-02 16:04:49
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8;
