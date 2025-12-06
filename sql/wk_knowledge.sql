/*
 Navicat Premium Dump SQL

 Source Server         : MySql
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : wk_knowledge

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 11/07/2025 11:33:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ek_knowledge
-- ----------------------------
DROP TABLE IF EXISTS `ek_knowledge`;
CREATE TABLE `ek_knowledge`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '用户ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名称',
  `is_public` int NOT NULL COMMENT '是否公开',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '资源地址',
  `tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件类型',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ek_knowledge
-- ----------------------------
INSERT INTO `ek_knowledge` VALUES (4, 10, 'Solvingtheshepherdingproblemheuristicsforherdingautonomous,interactingagents.pdf', 1, 'http://192.168.78.91:10030/static/knowledge/10/20250709115731_g73lrv/Solvingtheshepherdingproblemheuristicsforherdingautonomous,interactingagents.pdf', '其他', 'pdf文档', '2025-07-09 11:57:31', '2025-07-09 18:08:20');
INSERT INTO `ek_knowledge` VALUES (5, 10, 'Vue讲义.pdf', 1, 'http://192.168.78.91:10030/static/knowledge/10/20250709120036_cbld0x/Vue讲义.pdf', '其他', 'pdf文档', '2025-07-09 12:00:37', '2025-07-09 18:08:26');
INSERT INTO `ek_knowledge` VALUES (8, 14, '123', 1, 'http://192.168.78.91:10030/static/knowledge/14/20250709165041_srd5x6/Vue讲义.pdf', '其他', 'pdf', '2025-07-09 16:50:42', '2025-07-09 18:08:44');
INSERT INTO `ek_knowledge` VALUES (9, 10, '123', 1, 'http://192.168.78.91:10030/static/knowledge/10/20250710213045_ybsa79/6- 鸿蒙网络请求.docx', '其他', 'document', '2025-07-10 21:30:45', '2025-07-10 21:30:45');
INSERT INTO `ek_knowledge` VALUES (10, 10, 'java教程', 1, 'http://192.168.78.91:10030/static/knowledge/10/20250710224918_1azoan/第5章 SpringBoot JPA Mybatis.pdf', '计算机科学', 'pdf', '2025-07-10 22:49:19', '2025-07-10 22:49:19');
INSERT INTO `ek_knowledge` VALUES (11, 117, 'java教学', 1, 'http://192.168.78.91:10030/static/knowledge/117/20250711092210_2dszn3/智能化学习平台.pdf', '课件', 'pdf', '2025-07-11 09:22:10', '2025-07-11 09:22:10');

SET FOREIGN_KEY_CHECKS = 1;
