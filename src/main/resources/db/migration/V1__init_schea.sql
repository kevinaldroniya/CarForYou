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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB AUTO_INCREMENT=668 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;