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
