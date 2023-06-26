-- phpMyAdmin SQL Dump
-- version 4.4.15.10
-- https://www.phpmyadmin.net
--
-- Хост: localhost
-- Время создания: Июн 26 2023 г., 07:08
-- Версия сервера: 5.5.68-MariaDB
-- Версия PHP: 7.4.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База данных: `rst`
--

-- --------------------------------------------------------

--
-- Структура таблицы `accounts`
--

CREATE TABLE IF NOT EXISTS `accounts` (
  `id` int(11) NOT NULL,
  `login` varchar(18) DEFAULT NULL,
  `password` varchar(170) DEFAULT NULL,
  `status` int(3) DEFAULT '0',
  `session_key` varchar(170) DEFAULT NULL,
  `money` int(11) DEFAULT '0'
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `accounts`
--

INSERT INTO `accounts` (`id`, `login`, `password`, `status`, `session_key`, `money`) VALUES
(15, 'test', '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08', 0, 'a413e467dd49c9a7fe79ecfc9bd5c0d0cdfb0e5fb01f43dea6db0e66de85e99f', 308015);

-- --------------------------------------------------------

--
-- Структура таблицы `inventory`
--

CREATE TABLE IF NOT EXISTS `inventory` (
  `p_id` int(11) DEFAULT NULL,
  `item_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `inventory`
--

INSERT INTO `inventory` (`p_id`, `item_id`) VALUES
(10, 1000),
(10, 2000),
(10, 30001),
(10, 1001),
(10, 2001),
(10, 10001),
(10, 20001);

-- --------------------------------------------------------

--
-- Структура таблицы `items`
--

CREATE TABLE IF NOT EXISTS `items` (
  `id` int(11) NOT NULL,
  `team` int(2) DEFAULT NULL,
  `slot` int(4) DEFAULT NULL,
  `sub_slot` int(4) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=30002 DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `items`
--

INSERT INTO `items` (`id`, `team`, `slot`, `sub_slot`) VALUES
(10, 0, 0, 0),
(2000, 1, 0, 1),
(10001, 0, 1, 1),
(10002, 0, 1, 1),
(10003, 0, 1, 1),
(20001, 0, 1, 2),
(20002, 0, 1, 2),
(30001, 0, 1, 3);

-- --------------------------------------------------------

--
-- Структура таблицы `players`
--

CREATE TABLE IF NOT EXISTS `players` (
  `id` int(11) NOT NULL,
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
  `exp` int(11) DEFAULT '0'
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

--
-- Дамп данных таблицы `players`
--

INSERT INTO `players` (`id`, `a_id`, `name`, `points`, `red_character`, `blue_character`, `weapon_primary`, `weapon_secondary`, `weapon_melee`, `weapon_throw`, `weapon_special`, `wins`, `lose`, `kills`, `assists`, `deaths`, `exp`) VALUES
(10, 15, 'Admin', 1000000, 1001, 2001, 10001, 20001, 30001, 0, 0, 0, 0, 228, 0, 0, 514080);

-- --------------------------------------------------------

--
-- Структура таблицы `servers`
--

CREATE TABLE IF NOT EXISTS `servers` (
  `id` int(11) NOT NULL,
  `type` int(2) DEFAULT '0',
  `ip` varchar(16) DEFAULT NULL,
  `port` int(6) DEFAULT NULL,
  `max_players` int(11) DEFAULT '200',
  `current_players` int(11) DEFAULT '0',
  `game_mode` varchar(15) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8;

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `accounts`
--
ALTER TABLE `accounts`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `items`
--
ALTER TABLE `items`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `players`
--
ALTER TABLE `players`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `servers`
--
ALTER TABLE `servers`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT для сохранённых таблиц
--

--
-- AUTO_INCREMENT для таблицы `accounts`
--
ALTER TABLE `accounts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=16;
--
-- AUTO_INCREMENT для таблицы `items`
--
ALTER TABLE `items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=30002;
--
-- AUTO_INCREMENT для таблицы `players`
--
ALTER TABLE `players`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=11;
--
-- AUTO_INCREMENT для таблицы `servers`
--
ALTER TABLE `servers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=121;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
