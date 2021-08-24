CREATE DATABASE  IF NOT EXISTS `order_system` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `order_system`;
-- MySQL dump 10.13  Distrib 5.6.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: order_system
-- ------------------------------------------------------
-- Server version	5.7.14

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
-- Table structure for table `class`
--

DROP TABLE IF EXISTS `class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `class` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '班级唯一标识id',
  `name` varchar(45) DEFAULT NULL COMMENT '班级名称',
  `major_id` varchar(45) DEFAULT NULL COMMENT '班级所属专业Id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='班级表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class`
--

LOCK TABLES `class` WRITE;
/*!40000 ALTER TABLE `class` DISABLE KEYS */;
INSERT INTO `class` VALUES (1,'软工1902','1');
/*!40000 ALTER TABLE `class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `college`
--

DROP TABLE IF EXISTS `college`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `college` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '学院唯一标识id',
  `name` varchar(45) DEFAULT NULL COMMENT '学院名称',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='学院表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `college`
--

LOCK TABLES `college` WRITE;
/*!40000 ALTER TABLE `college` DISABLE KEYS */;
INSERT INTO `college` VALUES (1,'计算机学院');
/*!40000 ALTER TABLE `college` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flow`
--

DROP TABLE IF EXISTS `flow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `flow` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `description` text COMMENT '流程描述',
  `auth_id` int(11) DEFAULT NULL COMMENT '流程对应的角色的id',
  `name` varchar(50) DEFAULT NULL COMMENT '流程名称',
  `deleted` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='流程表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flow`
--

LOCK TABLES `flow` WRITE;
/*!40000 ALTER TABLE `flow` DISABLE KEYS */;
INSERT INTO `flow` VALUES (1,NULL,NULL,'test flow',1,'test flow',0);
/*!40000 ALTER TABLE `flow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `major`
--

DROP TABLE IF EXISTS `major`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `major` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '专业唯一标识id',
  `name` varchar(45) DEFAULT NULL COMMENT '专业名称',
  `college_id` varchar(45) DEFAULT NULL COMMENT '专业所属学院id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='专业表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `major`
--

LOCK TABLES `major` WRITE;
/*!40000 ALTER TABLE `major` DISABLE KEYS */;
INSERT INTO `major` VALUES (1,'软件工程','1');
/*!40000 ALTER TABLE `major` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='用户角色';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'ADMIN'),(2,'OPERATOR'),(3,'COLLEGE_LEVEL_LEADER'),(4,'DEPT_LEVEL_LEADER'),(5,'TEACHER'),(6,'UNDERGRADUATE'),(7,'POSTGRADUATE');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `secondary_dept`
--

DROP TABLE IF EXISTS `secondary_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `secondary_dept` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '二级部门唯一标识id',
  `name` varchar(45) DEFAULT NULL COMMENT '二级部门名称',
  `college_id` varchar(45) DEFAULT NULL COMMENT '所属学院Id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='二级部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `secondary_dept`
--

LOCK TABLES `secondary_dept` WRITE;
/*!40000 ALTER TABLE `secondary_dept` DISABLE KEYS */;
INSERT INTO `secondary_dept` VALUES (1,'计算机系','1');
/*!40000 ALTER TABLE `secondary_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户表主键',
  `open_id` varchar(28) DEFAULT NULL COMMENT '微信小程序唯一用户标识',
  `session_key` varchar(255) DEFAULT NULL COMMENT '微信给私人服务器颁发的session密钥，用于解密用户私密信息，有时限',
  `role` varchar(45) DEFAULT NULL COMMENT '用户角色，详见代码注释',
  `name` varchar(255) DEFAULT NULL COMMENT '用户真实姓名，需要用户填写',
  `college_id` bigint(20) DEFAULT NULL COMMENT '学院Id',
  `major_id` bigint(20) DEFAULT NULL COMMENT '专业Id',
  `secondary_dept_id` bigint(20) DEFAULT NULL COMMENT '二级部门Id，教师类专有',
  `class_id` int(11) DEFAULT NULL COMMENT '班级号，学生专有',
  `grade` int(11) DEFAULT NULL COMMENT '年级号，学生专有，本科生0~5，研究生0~2',
  `gender` bit(1) DEFAULT NULL COMMENT '用户性别，从微信信息获取',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '用户头像地址，从微信信息获取',
  `nick_name` varchar(255) DEFAULT NULL COMMENT '用户昵称，从微信信息获取',
  `student_id` varchar(45) DEFAULT NULL COMMENT '学号，学生类专有',
  `info_complete` int(11) DEFAULT '0',
  `job_id` varchar(45) DEFAULT NULL COMMENT '工号，教师类专有',
  `deleted` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'ofVnE4tSuvWvRiI2OnSra1kdh-5E','3zwZbNdXM44bvKiv62jB7w==','OPERATOR','文超',1,NULL,1,NULL,NULL,NULL,NULL,NULL,NULL,1,'2830947298',0,'2021-08-16 22:11:33','2021-08-17 16:11:24');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `work_order`
--

DROP TABLE IF EXISTS `work_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `work_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `is_examined` int(11) DEFAULT NULL COMMENT '标识工单是否已经被审批，初始值为0，0为未审批，1为审批',
  `initiator_id` int(11) DEFAULT NULL,
  `flow_id` int(11) DEFAULT NULL COMMENT '工单对应的流程id',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `attachment` mediumblob COMMENT '工单携带的附件，最大16MB',
  `status` int(11) DEFAULT NULL COMMENT '工单对应的状态，0123分别表示：在审，顺利通过，不通过，被撤销',
  `content` text COMMENT '存储工单的描述信息，该信息由申请人填写，比如申请资源使用途径，资源使用时长等',
  `deleted` int(11) DEFAULT NULL,
  `title` varchar(50) DEFAULT NULL,
  `attachment_name` varchar(50) DEFAULT NULL COMMENT '附件名',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work_order`
--

LOCK TABLES `work_order` WRITE;
/*!40000 ALTER TABLE `work_order` DISABLE KEYS */;
INSERT INTO `work_order` VALUES (1,0,1,1,NULL,NULL,NULL,NULL,NULL,0,'测试工单1',NULL),(2,0,1,1,NULL,NULL,NULL,NULL,NULL,0,'测试工单2',NULL),(3,0,1,1,NULL,NULL,NULL,NULL,NULL,0,'测试工单3',NULL);
INSERT INTO `work_order` VALUES (5,0,1,1,NULL,NULL,NULL,NULL,NULL,0,'测试工单5',NULL),(6,NULL,1,1,'2021-08-24 15:53:46',NULL,NULL,NULL,'ceshi',NULL,'ceshi',NULL);
/*!40000 ALTER TABLE `work_order` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-08-24 17:30:19