-- carforyou.brand definition

CREATE TABLE `brand` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `image` json NOT NULL,
  `created_at` bigint NOT NULL DEFAULT (unix_timestamp(now())),
  `created_by` bigint NOT NULL,
  `updated_at` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `deleted_at` bigint DEFAULT NULL,
  `deleted_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- carforyou.`group` definition

CREATE TABLE `group` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `created_at` bigint NOT NULL DEFAULT (unix_timestamp(now())),
  `created_by` bigint NOT NULL,
  `updated_at` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `deleted_at` bigint DEFAULT NULL,
  `deleted_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- carforyou.model definition

CREATE TABLE `model` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `brand_id` bigint NOT NULL,
  `created_at` bigint NOT NULL DEFAULT (unix_timestamp(now())),
  `created_by` bigint NOT NULL,
  `updated_at` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `deleted_at` bigint DEFAULT NULL,
  `deleted_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=126 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- carforyou.otp definition

CREATE TABLE `otp` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `otp_number` bigint NOT NULL,
  `otp_expiration` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- carforyou.`user` definition

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `phone_number` varchar(15) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `is_verified` bit(1) NOT NULL DEFAULT b'0',
  `created_at` bigint NOT NULL DEFAULT (unix_timestamp(now())),
  `created_by` bigint NOT NULL,
  `updated_at` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `deleted_at` bigint DEFAULT NULL,
  `deleted_by` bigint DEFAULT NULL,
  `group_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone_number` (`phone_number`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- carforyou.variant definition

CREATE TABLE `variant` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `model_id` bigint NOT NULL,
  `year` bigint NOT NULL,
  `engine` json NOT NULL,
  `transmission` json NOT NULL,
  `fuel` json NOT NULL,
  `created_at` bigint NOT NULL DEFAULT (unix_timestamp(now())),
  `created_by` bigint NOT NULL,
  `updated_at` bigint DEFAULT NULL,
  `updated_by` bigint DEFAULT NULL,
  `deleted_at` bigint DEFAULT NULL,
  `deleted_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1335 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;