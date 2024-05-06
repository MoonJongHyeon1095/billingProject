CREATE TABLE IF NOT EXISTS VideoStatistic (
  videoStatisticId int NOT NULL AUTO_INCREMENT,
  videoId int NOT NULL,
  dailyWatchedTime bigint NOT NULL DEFAULT 0,
  dailyViewCount int NOT NULL DEFAULT 0,
  dailyAdViewCount int NOT NULL DEFAULT 0,
  dailyVideoProfit int NOT NULL DEFAULT 0,
  createdAt date NOT NULL DEFAULT '2024-04-01',
  dailyAdProfit int NOT NULL DEFAULT 0,
  PRIMARY KEY (videoStatisticId),
  FOREIGN KEY (videoId) REFERENCES Video(videoId)
);
