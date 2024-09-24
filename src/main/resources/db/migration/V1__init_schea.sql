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

CREATE TABLE `items` (
`id` bigint NOT NULL AUTO_INCREMENT,
`license_plat` varchar(255) NOT NULL,
`title` varchar(255) NOT NULL,
`inspector_id` bigint NOT NULL,
`brand_id` bigint NOT NULL,
`model_id` bigint NOT NULL,
`variant_id` bigint NOT NULL,
`fuel_type` varchar(255) NOT NULL,
`transmission` varchar(255) NOT NULL,
`chassis_number` varchar(255) NOT NULL,
`engine_number` varchar(255) NOT NULL,
`mileage` bigint NOT NULL,
`image` varchar(255) NOT NULL,
`starting_price` bigint NOT NULL,
`physical_color` varchar(255) NOT NULL,
`document` varchar(255) NOT NULL,
`auction_start_date`bigint NOT NULL,
`auction_end_date` bigint NOT NULL,
`status` varchar(255) NOT NULL,
`keur_date` DATE,
`stnk_date` DATE,
`interior_grade` char(1) NOT NULL,
`exterior_grade` char(1) NOT NULL,
`chassis_grade` char(1) NOT NULL,
`engine_grade` char(1)
);

CREATE TABLE `bid` (
`id` bigint NOT NULL,
`item_id` bigint NOT NULL,
`bidder_id` bigint NOT NULL,
`bid_amount` bigint NOT NULL,
`bid_time` bigint NOT NULL
);

CREATE TABLE `auction_result`(
`id` bigint NOT NULL,
`item_id` bigint NOT NULL,
`winner_id` bigint NOT NULL,
`final_bid_amount` bigint NOT NULL,
`auction_end_time` bigint NOT NULL
);

CREATE TABLE `payment`(
`id` bigint NOT NULL,
`auction_result_id` NOT NULL,
`amount` bigint NOT NULL,
`payment_method` varchar(255) NOT NULL,
`payment_status` varchar(255) NOT NULL,
`payment_date` bigint NOT NULL,
`payment_deadline` bigint NOT NULL
)