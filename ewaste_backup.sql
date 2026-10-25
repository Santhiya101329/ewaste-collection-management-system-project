-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: ewaste
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKt8o6pivur7nn124jehx7cygw5` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (2,'printer');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `collectors`
--

DROP TABLE IF EXISTS `collectors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `collectors` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `available` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `location_updated_at` datetime(6) DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `collectors`
--

LOCK TABLES `collectors` WRITE;
/*!40000 ALTER TABLE `collectors` DISABLE KEYS */;
INSERT INTO `collectors` VALUES (1,_binary '\0','ravi','9090808070',NULL,NULL,NULL),(2,_binary '','banu','9056545250',12.950398250864664,'2026-09-15 22:02:53.715369',80.14178677763086),(3,_binary '','aishwarya','9080706050',NULL,NULL,NULL);
/*!40000 ALTER TABLE `collectors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'chennai','santhiya101329@gmail.com','santhiya','2008','9345112636',NULL),(4,'chennai','santhiyanalini8@gmail.com','nalini','1982','9944663334',NULL),(5,'chennai','santhiyasub123@gmail.com','SANTHIYA S','8888','9345112636',NULL),(6,'Head Office','admin@ewaste.com','Admin','admin123','0000000000','ADMIN'),(7,'bangalore','valliprithika05@gmail.com','prithika','0052','9988998899','USER'),(11,'puduchery','santhnalsub@gmail.com','santhiya','4000','9080908767','USER'),(12,'chennai','nalsub@gmail.com','prithi','6767','9809786756','USER'),(13,'chennai','sanjana123@gmail.com','sanjana','sanjana123','9988776655','USER'),(14,'puduchery','manisha123@gmail.com','manisha','manisha@123','9876545678','USER'),(15,NULL,'ravi123@gmail.com','ravi','admin123','9090808070','COLLECTOR'),(17,'chennai','banu1234@gmail.com','banu','banu123','9056545250','USER'),(18,NULL,'banu123@gmail.com','banu','banu123','9056545250','COLLECTOR'),(19,'chennai','deepa123@gmail.com','deepa','deepa123','7090809070','USER'),(20,NULL,'aishwarya123@gmail.com','aishwarya','aishu123','9080706050','COLLECTOR');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `waste_requests`
--

DROP TABLE IF EXISTS `waste_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `waste_requests` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `preferred_date` date DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `collector_id` bigint DEFAULT NULL,
  `collector_latitude` double DEFAULT NULL,
  `collector_location_captured_at` datetime(6) DEFAULT NULL,
  `collector_longitude` double DEFAULT NULL,
  `latitude` double DEFAULT NULL,
  `location_distance` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `pickup_photo_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKh87br3mjnhucs7vgv866gag9i` (`user_id`),
  KEY `FKdwlwy9uuib27f80qgkp3i5t86` (`collector_id`),
  CONSTRAINT `FKdwlwy9uuib27f80qgkp3i5t86` FOREIGN KEY (`collector_id`) REFERENCES `collectors` (`id`),
  CONSTRAINT `FKh87br3mjnhucs7vgv866gag9i` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `waste_requests`
--

LOCK TABLES `waste_requests` WRITE;
/*!40000 ALTER TABLE `waste_requests` DISABLE KEYS */;
INSERT INTO `waste_requests` VALUES (1,'chennai','Laptop','2026-08-30 11:21:31.261248','2026-08-31','CLOSED',5,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,'puduchery','Mobile Phone','2026-08-30 15:51:58.848404','2026-08-31','SCHEDULED',11,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,'chennai','Battery','2026-08-30 21:04:55.897675','2026-09-03','PENDING',11,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(4,'AnnaUniversity Mit,chrompet,chennai','Charger','2026-08-30 21:19:18.812103','2026-09-04','SCHEDULED',12,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(5,'mit campus anna university','Mobile Phone','2026-09-15 18:27:17.402499','2026-09-19','SCHEDULED',12,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(6,'chennai','Mobile Phone','2026-09-15 19:02:07.821454','2026-09-19','FLAGGED',13,2,12.950619748539369,'2026-09-15 22:23:30.619201',80.14209254041401,NULL,NULL,NULL,'uploads\\pickups\\6_1789491210624_Screenshot 2026-09-15 222250.png'),(7,'chennai','Battery','2026-09-15 20:09:49.598708','2026-09-26','SCHEDULED',13,NULL,NULL,NULL,NULL,12.950769909302743,NULL,80.14191634959205,NULL),(8,'madras Institute of technology ,chrompet ','Laptop','2026-09-15 22:55:17.215062','2026-09-25','COMPLETED',19,2,12.949777725395432,'2026-09-15 22:59:24.599569',80.14105767560046,12.949828492977366,0.009286432058519565,80.14112571897853,'uploads\\pickups\\8_1789493364599_Screenshot 2026-09-15 225837.png');
/*!40000 ALTER TABLE `waste_requests` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-15 23:31:10
