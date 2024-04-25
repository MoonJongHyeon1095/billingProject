
CREATE TABLE IF NOT EXISTS `User` (
  `userId` int unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(100) NOT NULL,
  `refreshToken` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `role` varchar(10) NOT NULL DEFAULT 'NORMAL',
  PRIMARY KEY (`userId`),
  UNIQUE KEY `User_UN` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `Advertisement` (
  `advertisementId` int unsigned NOT NULL AUTO_INCREMENT,
  `adPriority` int unsigned NOT NULL,
  PRIMARY KEY (`advertisementId`),
  UNIQUE KEY `Advertisement_UN` (`adPriority`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `Video` (
  `videoId` int unsigned NOT NULL AUTO_INCREMENT,
  `userId` int unsigned NOT NULL,
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `duration` int unsigned NOT NULL DEFAULT '0',
  `viewCount` int unsigned NOT NULL DEFAULT '0',
  `createdAt` datetime DEFAULT NULL,
  PRIMARY KEY (`videoId`),
  KEY `userId` (`userId`),
  CONSTRAINT `video_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `WatchHistory` (
  `watchHistoryId` int unsigned NOT NULL AUTO_INCREMENT,
  `userId` int unsigned DEFAULT NULL,
  `videoId` int unsigned NOT NULL,
  `playedTime` int unsigned NOT NULL DEFAULT '0',
  `lastWatched` int unsigned NOT NULL DEFAULT '0',
  `UUID` varchar(50) DEFAULT NULL,
  `createdAt` datetime DEFAULT NULL,
  `day` smallint unsigned NOT NULL DEFAULT '1',
  `week` smallint unsigned NOT NULL DEFAULT '1',
  `month` smallint unsigned NOT NULL DEFAULT '1',
  `year` smallint unsigned NOT NULL DEFAULT '2024',
  `adViewCount` smallint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`watchHistoryId`),
  KEY `userId` (`userId`),
  KEY `videoId` (`videoId`),
  CONSTRAINT `watchhistory_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`),
  CONSTRAINT `watchhistory_ibfk_2` FOREIGN KEY (`videoId`) REFERENCES `Video` (`videoId`)
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

CREATE TABLE `VideoStatistic` (
  `videoStatisticId` int unsigned NOT NULL AUTO_INCREMENT,
  `videoId` int unsigned NOT NULL,
  `dailyWatchedTime` bigint unsigned NOT NULL DEFAULT '0',
  `weeklyWatchedTime` bigint unsigned NOT NULL DEFAULT '0',
  `monthlyWatchedTime` bigint unsigned NOT NULL DEFAULT '0',
  `dailyViewCount` int unsigned NOT NULL DEFAULT '0',
  `weeklyViewCount` int unsigned NOT NULL DEFAULT '0',
  `monthlyViewCount` int unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`videoStatisticId`),
  UNIQUE KEY `VideoStatistic_UN` (`videoId`),
  CONSTRAINT `VideoStatistic_FK` FOREIGN KEY (`videoId`) REFERENCES `Video` (`videoId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;