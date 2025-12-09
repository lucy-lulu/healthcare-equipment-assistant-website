CREATE TABLE `user` (
  `user_id` int PRIMARY KEY,
  `role` varchar(255),
  `password` varchar(255),
  `name` varchar(255),
  `email` varchar(255),
  `shipping_address` varchar(255),
  `billing_address` varchar(255)
);

CREATE TABLE `enquire` (
  `enquire_id` int PRIMARY KEY,
  `asker_id` int,
  `responder_id` int,
  `question` text,
  `answer` text,
  `status` varchar(255),
  `timestamp` datetime
);

CREATE TABLE `combined_product` (
  `id` int PRIMARY KEY,
  `type` varchar(255),
  `sku` varchar(255),
  `name` varchar(255),
  `short_description` text,
  `description` text,
  `published` boolean,
  `visibility` varchar(255),
  `tax_status` varchar(255),
  `tax_class` varchar(255),
  `in_stock` boolean,
  `stock` int,
  `low_stock_amount` int,
  `backorders_allowed` boolean,
  `sold_individually` boolean,
  `weight_kg` float,
  `length_cm` float,
  `width_cm` float,
  `height_cm` float,
  `categories` varchar(255),
  `shipping_class` varchar(255),
  `images` text,
  `download_limit` int,
  `download_expiry_days` int,
  `parent` int,
  `reviews_allowed` boolean,
  `purchase_note` text,
  `menu_order` int,
  `attribute1_name` varchar(255),
  `attribute1_value` varchar(255),
  `attribute1_visible` boolean,
  `attribute1_global` boolean,
  `attribute1_default` varchar(255),
  `attribute2_name` varchar(255),
  `attribute2_value` varchar(255),
  `attribute2_visible` boolean,
  `attribute2_global` boolean,
  `attribute3_name` varchar(255),
  `attribute3_value` varchar(255),
  `attribute3_visible` boolean,
  `attribute3_global` boolean,
  `sap_product_code` varchar(255),
  `sap_product_name` varchar(255),
  `sap_product_group` varchar(255),
  `rrp` decimal,
  `t1_platinum` decimal,
  `t2_gold` decimal,
  `t3_silver` decimal
);

CREATE TABLE `category` (
  `category_id` int PRIMARY KEY,
  `category_name` varchar(255)
);

CREATE TABLE `category_relation` (
  `parent_id` int,
  `child_id` int,
  PRIMARY KEY (`parent_id`, `child_id`)
);

CREATE TABLE `product_category` (
  `product_id` int,
  `category_id` int,
  PRIMARY KEY (`product_id`, `category_id`)
);

CREATE TABLE `order` (
  `order_id` int PRIMARY KEY,
  `user_id` int,
  `order_date` datetime,
  `status` varchar(255),
  `total_amount` decimal
);

CREATE TABLE `order_product` (
  `order_id` int,
  `product_id` int,
  `quantity` int,
  `unit_price` decimal,
  PRIMARY KEY (`order_id`, `product_id`)
);

ALTER TABLE `user` COMMENT = 'Stores user details, including customers and staff';

ALTER TABLE `enquire` COMMENT = 'Stores product enquiries and responses between users';

ALTER TABLE `combined_product` COMMENT = 'Combined WooCommerce + SAP product data';

ALTER TABLE `category` COMMENT = 'Product category';

ALTER TABLE `category_relation` COMMENT = 'Defines parent-child relationships between categories';

ALTER TABLE `product_category` COMMENT = 'Associates products with categories (many-to-many)';

ALTER TABLE `order` COMMENT = 'Stores orders placed by users';

ALTER TABLE `order_product` COMMENT = 'Associates orders with products, including quantity and price';

ALTER TABLE `enquire` ADD FOREIGN KEY (`asker_id`) REFERENCES `user` (`user_id`);

ALTER TABLE `enquire` ADD FOREIGN KEY (`responder_id`) REFERENCES `user` (`user_id`);

ALTER TABLE `category_relation` ADD FOREIGN KEY (`parent_id`) REFERENCES `category` (`category_id`);

ALTER TABLE `category_relation` ADD FOREIGN KEY (`child_id`) REFERENCES `category` (`category_id`);

ALTER TABLE `product_category` ADD FOREIGN KEY (`product_id`) REFERENCES `combined_product` (`id`);

ALTER TABLE `product_category` ADD FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`);

ALTER TABLE `order` ADD FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

ALTER TABLE `order_product` ADD FOREIGN KEY (`order_id`) REFERENCES `order` (`order_id`);

ALTER TABLE `order_product` ADD FOREIGN KEY (`product_id`) REFERENCES `combined_product` (`id`);
