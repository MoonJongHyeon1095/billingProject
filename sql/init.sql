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

-- hh99_2.AdvertisementDetail definition
CREATE TABLE IF NOT EXISTS `AdvertisementDetail` (
  `adDetailId` int unsigned NOT NULL AUTO_INCREMENT,
  `adPriority` smallint unsigned NOT NULL,
  `videoId` int unsigned NOT NULL,
  `viewCount` int unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`adDetailId`),
  KEY `AdvertisementDetail_FK` (`adPriority`),
  KEY `AdvertisementDetail_FK_1` (`videoId`),
  KEY `AdvertisementDetail_videoId_IDX` (`videoId`,`adPriority`) USING BTREE,
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
  `dailyVideoProfit` int unsigned NOT NULL DEFAULT '0',
  `createdAt` date NOT NULL DEFAULT '2024-05-01',
  `dailyAdProfit` int unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`videoStatisticId`),
  KEY `VideoStatistic_FK` (`videoId`),
  KEY `VideoStatistic_createdAt_IDX` (`createdAt`,`videoId`,`dailyViewCount`,`dailyAdViewCount`,`dailyWatchedTime`,`dailyVideoProfit`,`dailyAdProfit`) USING BTREE,
  CONSTRAINT `VideoStatistic_FK` FOREIGN KEY (`videoId`) REFERENCES `Video` (`videoId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
  `numericOrderKey` bigint unsigned NOT NULL AUTO_INCREMENT,
  `assignedServer` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`createdAt`,`watchHistoryId`),
  UNIQUE KEY `WatchHistory_UN` (`numericOrderKey`,`createdAt`),
  KEY `WatchHistory_createdAt_IDX` (`createdAt`,`assignedServer`,`numericOrderKey`,`videoId`,`playedTime`,`adViewCount`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=35034360 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
/*!50500 PARTITION BY LIST  COLUMNS(createdAt)
(PARTITION p240501 VALUES IN ('2024-05-01') ENGINE = InnoDB,
 PARTITION p240502 VALUES IN ('2024-05-02') ENGINE = InnoDB,
 PARTITION p240503 VALUES IN ('2024-05-03') ENGINE = InnoDB,
 PARTITION p240504 VALUES IN ('2024-05-04') ENGINE = InnoDB,
 PARTITION p240505 VALUES IN ('2024-05-05') ENGINE = InnoDB,
 PARTITION p240506 VALUES IN ('2024-05-06') ENGINE = InnoDB,
 PARTITION p240507 VALUES IN ('2024-05-07') ENGINE = InnoDB,
 PARTITION p240508 VALUES IN ('2024-05-08') ENGINE = InnoDB,
 PARTITION p240509 VALUES IN ('2024-05-09') ENGINE = InnoDB,
 PARTITION p240510 VALUES IN ('2024-05-10') ENGINE = InnoDB,
 PARTITION p240511 VALUES IN ('2024-05-11') ENGINE = InnoDB,
 PARTITION p240512 VALUES IN ('2024-05-12') ENGINE = InnoDB,
 PARTITION p240513 VALUES IN ('2024-05-13') ENGINE = InnoDB,
 PARTITION p240514 VALUES IN ('2024-05-14') ENGINE = InnoDB,
 PARTITION p240515 VALUES IN ('2024-05-15') ENGINE = InnoDB,
 PARTITION p240516 VALUES IN ('2024-05-16') ENGINE = InnoDB,
 PARTITION p240517 VALUES IN ('2024-05-17') ENGINE = InnoDB,
 PARTITION p240518 VALUES IN ('2024-05-18') ENGINE = InnoDB,
 PARTITION p240519 VALUES IN ('2024-05-19') ENGINE = InnoDB,
 PARTITION p240520 VALUES IN ('2024-05-20') ENGINE = InnoDB,
 PARTITION p240521 VALUES IN ('2024-05-21') ENGINE = InnoDB,
 PARTITION p240522 VALUES IN ('2024-05-22') ENGINE = InnoDB,
 PARTITION p240523 VALUES IN ('2024-05-23') ENGINE = InnoDB,
 PARTITION p240524 VALUES IN ('2024-05-24') ENGINE = InnoDB,
 PARTITION p240525 VALUES IN ('2024-05-25') ENGINE = InnoDB,
 PARTITION p240526 VALUES IN ('2024-05-26') ENGINE = InnoDB,
 PARTITION p240527 VALUES IN ('2024-05-27') ENGINE = InnoDB,
 PARTITION p240528 VALUES IN ('2024-05-28') ENGINE = InnoDB,
 PARTITION p240529 VALUES IN ('2024-05-29') ENGINE = InnoDB,
 PARTITION p240530 VALUES IN ('2024-05-30') ENGINE = InnoDB,
 PARTITION p240531 VALUES IN ('2024-05-31') ENGINE = InnoDB) */;