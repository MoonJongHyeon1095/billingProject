CREATE TABLE IF NOT EXISTS `User` (
  `email` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(100) NOT NULL,
  `refreshToken` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `role` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`email`),
  UNIQUE KEY `User_UN` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- hh99_2.Advertisement definition
CREATE TABLE IF NOT EXISTS `Advertisement` (
  `advertisementId` smallint unsigned NOT NULL AUTO_INCREMENT,
  `adPriority` smallint unsigned NOT NULL,
  PRIMARY KEY (`advertisementId`),
  UNIQUE KEY `Advertisement_UN` (`adPriority`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- hh99_2.Video definition
CREATE TABLE IF NOT EXISTS `Video` (
  `videoId` int unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(200) NOT NULL,
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `duration` int unsigned NOT NULL DEFAULT '0',
  `totalViewCount` int unsigned NOT NULL DEFAULT '0',
  `totalAdViewCount` int unsigned NOT NULL DEFAULT '0',
  `createdAt` datetime DEFAULT NULL,
  PRIMARY KEY (`videoId`),
  KEY `email` (`email`),
  CONSTRAINT `video_ibfk_1` FOREIGN KEY (`email`) REFERENCES `User` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `AdvertisementDetail` (
  `adDetailId` int unsigned NOT NULL AUTO_INCREMENT,
  `adPriority` int unsigned NOT NULL,
  `videoId` int unsigned NOT NULL,
  `viewCount` int unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`adDetailId`),
  KEY `AdvertisementDetail_FK` (`adPriority`),
  KEY `AdvertisementDetail_FK_1` (`videoId`),
  CONSTRAINT `AdvertisementDetail_FK` FOREIGN KEY (`adPriority`) REFERENCES `Advertisement` (`adPriority`),
  CONSTRAINT `AdvertisementDetail_FK_1` FOREIGN KEY (`videoId`) REFERENCES `Video` (`videoId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- hh99_2.VideoStatistic definition
CREATE TABLE IF NOT EXISTS `VideoStatistic` (
  `videoStatisticId` int unsigned NOT NULL AUTO_INCREMENT,
  `videoId` int unsigned NOT NULL,
  `dailyWatchedTime` bigint unsigned NOT NULL DEFAULT '0',
  `dailyViewCount` int unsigned NOT NULL DEFAULT '0',
  `dailyAdViewCount` int unsigned NOT NULL DEFAULT '0',
  `dailyBill` int unsigned NOT NULL DEFAULT '0',
  `createdAt` DATE NOT NULL DEFAULT 2024-04-01,
  PRIMARY KEY (`videoStatisticId`),
  CONSTRAINT `VideoStatistic_FK` FOREIGN KEY (`videoId`) REFERENCES `Video` (`videoId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- hh99_2.WatchHistory definition

CREATE TABLE IF NOT EXISTS `WatchHistory` (
  `watchHistoryId` varchar(50) NOT NULL,
  `email` varchar(200) DEFAULT NULL,
  `videoId` int unsigned NOT NULL,
  `playedTime` int unsigned NOT NULL DEFAULT '0',
  `lastWatched` int unsigned NOT NULL DEFAULT '0',
  `deviceUUID` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `adViewCount` smallint unsigned NOT NULL DEFAULT '0',
  `createdAt` date NOT NULL DEFAULT '2024-04-01',
  `watchedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`createdAt`,`watchHistoryId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
/*!50500 PARTITION BY LIST  COLUMNS(createdAt)
(PARTITION p240401 VALUES IN ('2024-04-01') ENGINE = InnoDB,
 PARTITION p240402 VALUES IN ('2024-04-02') ENGINE = InnoDB,
 PARTITION p240403 VALUES IN ('2024-04-03') ENGINE = InnoDB,
 PARTITION p240404 VALUES IN ('2024-04-04') ENGINE = InnoDB,
 PARTITION p240405 VALUES IN ('2024-04-05') ENGINE = InnoDB,
 PARTITION p240406 VALUES IN ('2024-04-06') ENGINE = InnoDB,
 PARTITION p240407 VALUES IN ('2024-04-07') ENGINE = InnoDB,
 PARTITION p240408 VALUES IN ('2024-04-08') ENGINE = InnoDB,
 PARTITION p240409 VALUES IN ('2024-04-09') ENGINE = InnoDB,
 PARTITION p240410 VALUES IN ('2024-04-10') ENGINE = InnoDB,
 PARTITION p240411 VALUES IN ('2024-04-11') ENGINE = InnoDB,
 PARTITION p240412 VALUES IN ('2024-04-12') ENGINE = InnoDB,
 PARTITION p240413 VALUES IN ('2024-04-13') ENGINE = InnoDB,
 PARTITION p240414 VALUES IN ('2024-04-14') ENGINE = InnoDB,
 PARTITION p240415 VALUES IN ('2024-04-15') ENGINE = InnoDB,
 PARTITION p240416 VALUES IN ('2024-04-16') ENGINE = InnoDB,
 PARTITION p240417 VALUES IN ('2024-04-17') ENGINE = InnoDB,
 PARTITION p240418 VALUES IN ('2024-04-18') ENGINE = InnoDB,
 PARTITION p240419 VALUES IN ('2024-04-19') ENGINE = InnoDB,
 PARTITION p240420 VALUES IN ('2024-04-20') ENGINE = InnoDB,
 PARTITION p240421 VALUES IN ('2024-04-21') ENGINE = InnoDB,
 PARTITION p240422 VALUES IN ('2024-04-22') ENGINE = InnoDB,
 PARTITION p240423 VALUES IN ('2024-04-23') ENGINE = InnoDB,
 PARTITION p240424 VALUES IN ('2024-04-24') ENGINE = InnoDB,
 PARTITION p240425 VALUES IN ('2024-04-25') ENGINE = InnoDB,
 PARTITION p240426 VALUES IN ('2024-04-26') ENGINE = InnoDB,
 PARTITION p240427 VALUES IN ('2024-04-27') ENGINE = InnoDB,
 PARTITION p240428 VALUES IN ('2024-04-28') ENGINE = InnoDB,
 PARTITION p240429 VALUES IN ('2024-04-29') ENGINE = InnoDB,
 PARTITION p240430 VALUES IN ('2024-04-30') ENGINE = InnoDB) */;