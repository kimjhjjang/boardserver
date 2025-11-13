CREATE TABLE IF NOT EXISTS `user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `userId` VARCHAR(100) NOT NULL,
  `passWord` VARCHAR(255),
  `nickName` VARCHAR(100),
  `createTime` DATETIME,
  `isWithDraw` TINYINT(1) DEFAULT 0,
  `isAdmin` TINYINT(1) DEFAULT 0,
  `status` VARCHAR(20) DEFAULT 'DEFAULT',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;