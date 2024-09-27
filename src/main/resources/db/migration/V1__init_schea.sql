-- carforyou.brand definition

CREATE TABLE `brand` (
  `id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `image` json NOT NULL,
  `created_at` int NOT NULL DEFAULT (unix_timestamp(now())),
  `created_by` int NOT NULL,
  `updated_at` int DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  `deleted_at` int DEFAULT NULL,
  `deleted_by` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- carforyou.`group` definition

CREATE TABLE `group` (
  `id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `created_at` int NOT NULL DEFAULT (unix_timestamp(now())),
  `created_by` int NOT NULL,
  `updated_at` int DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  `deleted_at` int DEFAULT NULL,
  `deleted_by` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- carforyou.item definition

CREATE TABLE `item` (
  `id` int NOT NULL,
  `license_plat` varchar(10) NOT NULL,
  `title` varchar(255) NOT NULL,
  `inspector_id` int NOT NULL,
  `variant_id` int NOT NULL,
  `fuel_type` varchar(255) NOT NULL,
  `transmission` varchar(255) NOT NULL,
  `engine_capacity` int NOT NULL,
  `mileage` int NOT NULL,
  `starting_price` varchar(255) NOT NULL,
  `physical_color` varchar(255) NOT NULL,
  `auction_start_date` timestamp NULL DEFAULT NULL,
  `auction_end_date` timestamp NULL DEFAULT NULL,
  `status` enum('AVAILABLE','SOLD','REMOVED','RESERVED') NOT NULL,
  `interior_grade` enum('A','B','C','D') DEFAULT NULL,
  `exterior_grade` enum('A','B','C','D') DEFAULT NULL,
  `chassis_grade` enum('A','B','C','D') DEFAULT NULL,
  `engine_grade` enum('A','B','C','D') DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` int NOT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  `deleted_at` timestamp NULL DEFAULT NULL,
  `deleted_by` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- carforyou.model definition

CREATE TABLE `model` (
  `id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `brand_id` int NOT NULL,
  `created_at` int NOT NULL DEFAULT (unix_timestamp(now())),
  `created_by` int NOT NULL,
  `updated_at` int DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  `deleted_at` int DEFAULT NULL,
  `deleted_by` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- carforyou.otp definition

CREATE TABLE `otp` (
  `id` int NOT NULL,
  `otp_number` int NOT NULL,
  `otp_expiration` int NOT NULL,
  `user_id` int NOT NULL,
  `otp_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- carforyou.refresh_token definition

CREATE TABLE `refresh_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `token` varchar(255) NOT NULL,
  `expiration_time` timestamp NOT NULL DEFAULT ((now() + interval 7 day)),
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- carforyou.`user` definition

CREATE TABLE `user` (
  `id` int NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `phone_number` varchar(15) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `is_verified` bit(1) NOT NULL DEFAULT b'0',
  `created_at` int NOT NULL DEFAULT (unix_timestamp(now())),
  `created_by` int NOT NULL,
  `updated_at` int DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  `deleted_at` int DEFAULT NULL,
  `deleted_by` int DEFAULT NULL,
  `group_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone_number` (`phone_number`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- carforyou.variant definition

CREATE TABLE `variant` (
  `id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `model_id` int NOT NULL,
  `year` bigint NOT NULL,
  `engine` json NOT NULL,
  `transmission` json NOT NULL,
  `fuel` json NOT NULL,
  `created_at` int NOT NULL DEFAULT (unix_timestamp(now())),
  `created_by` int NOT NULL,
  `updated_at` int DEFAULT NULL,
  `updated_by` int DEFAULT NULL,
  `deleted_at` int DEFAULT NULL,
  `deleted_by` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;