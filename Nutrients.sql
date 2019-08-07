-- MySQL dump 10.13  Distrib 5.7.27, for Linux (x86_64)
--
-- Host: localhost    Database: Nutrients
-- ------------------------------------------------------
-- Server version	5.7.27-0ubuntu0.18.04.1-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Gender`
--

DROP TABLE IF EXISTS `Gender`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Gender` (
  `id` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Gender`
--

LOCK TABLES `Gender` WRITE;
/*!40000 ALTER TABLE `Gender` DISABLE KEYS */;
INSERT INTO `Gender` VALUES (1,'Male'),(2,'Female');
/*!40000 ALTER TABLE `Gender` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Nutrient`
--

DROP TABLE IF EXISTS `Nutrient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Nutrient` (
  `id` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Nutrient`
--

LOCK TABLES `Nutrient` WRITE;
/*!40000 ALTER TABLE `Nutrient` DISABLE KEYS */;
INSERT INTO `Nutrient` VALUES (0,'c'),(1,'b1'),(2,'b2'),(3,'b6'),(4,'b3'),(5,'b12'),(6,'b9'),(7,'b5'),(9,'a'),(10,'beta-carotin'),(11,'e'),(12,'d'),(13,'k'),(14,'calcium'),(15,'phosphorus'),(16,'magnesum'),(17,'kalium'),(18,'natrium'),(19,'chlorids'),(20,'ferrum'),(21,'zincum'),(22,'jodum'),(23,'cuprum'),(24,'manganum'),(25,'selenum'),(26,'chromium'),(27,'molybdaenum'),(28,'fluorum'),(29,'b4');
/*!40000 ALTER TABLE `Nutrient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Nutrient_has_Gender`
--

DROP TABLE IF EXISTS `Nutrient_has_Gender`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Nutrient_has_Gender` (
  `nutrient` int(11) NOT NULL,
  `gender` int(11) NOT NULL,
  `value` float DEFAULT NULL,
  PRIMARY KEY (`nutrient`,`gender`),
  KEY `fk_Nutrient_has_Gender_Gender_idx` (`gender`),
  CONSTRAINT `fk_Nutrient_has_Gender_Gender` FOREIGN KEY (`gender`) REFERENCES `Gender` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Nutrient_has_Gender_Nutrient` FOREIGN KEY (`nutrient`) REFERENCES `Nutrient` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Nutrient_has_Gender`
--

LOCK TABLES `Nutrient_has_Gender` WRITE;
/*!40000 ALTER TABLE `Nutrient_has_Gender` DISABLE KEYS */;
INSERT INTO `Nutrient_has_Gender` VALUES (0,1,90),(0,2,90),(1,1,1.5),(1,2,1.5),(2,1,1.8),(2,2,1.8),(3,1,2),(3,2,2),(4,1,20),(4,2,20),(5,1,3),(5,2,3),(6,1,400),(6,2,400),(7,1,5),(7,2,5),(9,1,900),(9,2,900),(10,1,5000),(10,2,5000),(11,1,15),(11,2,15),(12,1,10),(12,2,10),(13,1,120),(13,2,120),(14,1,1000),(14,2,1000),(15,1,800),(15,2,800),(16,1,400),(16,2,400),(17,1,2500),(17,2,2500),(18,1,1300),(18,2,1300),(19,1,2300),(19,2,2300),(20,1,10),(20,2,18),(21,1,12),(21,2,12),(22,1,0.15),(22,2,0.15),(23,1,1),(23,2,1),(24,1,2),(24,2,2),(25,1,70),(25,2,70),(26,1,50),(26,2,50),(27,1,70),(27,2,70),(28,1,4),(28,2,4),(29,1,500),(29,2,500);
/*!40000 ALTER TABLE `Nutrient_has_Gender` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-08-07 18:50:57
