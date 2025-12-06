/*
 Navicat Premium Dump SQL

 Source Server         : MySql
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : wk_experiment

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 11/07/2025 11:32:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ep_join_team
-- ----------------------------
DROP TABLE IF EXISTS `ep_join_team`;
CREATE TABLE `ep_join_team`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` int NOT NULL COMMENT '加入者id',
  `team_id` int NOT NULL COMMENT '队伍id',
  `created_at` timestamp NOT NULL COMMENT '创建时间',
  `updated_at` timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ep_join_team
-- ----------------------------
INSERT INTO `ep_join_team` VALUES (4, 10, 4, '2025-07-09 23:14:09', '2025-07-09 23:14:09');
INSERT INTO `ep_join_team` VALUES (6, 13, 4, '2025-07-09 23:15:48', '2025-07-09 23:15:48');
INSERT INTO `ep_join_team` VALUES (7, 10, 6, '2025-07-10 10:53:14', '2025-07-10 10:53:14');
INSERT INTO `ep_join_team` VALUES (8, 13, 6, '2025-07-10 10:53:14', '2025-07-10 10:53:14');
INSERT INTO `ep_join_team` VALUES (9, 120, 7, '2025-07-10 22:41:49', '2025-07-10 22:41:49');
INSERT INTO `ep_join_team` VALUES (10, 10, 7, '2025-07-10 22:43:48', '2025-07-10 22:43:48');
INSERT INTO `ep_join_team` VALUES (11, 13, 7, '2025-07-11 09:37:27', '2025-07-11 09:37:27');
INSERT INTO `ep_join_team` VALUES (12, 122, 7, '2025-07-11 10:39:57', '2025-07-11 10:39:57');

-- ----------------------------
-- Table structure for ep_publish
-- ----------------------------
DROP TABLE IF EXISTS `ep_publish`;
CREATE TABLE `ep_publish`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '实验ID',
  `course_id` int NOT NULL COMMENT '课程ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '实验标题',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '实验详情',
  `ddl` timestamp NULL DEFAULT NULL COMMENT '截止日期',
  `type` int NULL DEFAULT 0 COMMENT '0为实验，1为项目实训',
  `num_file` int NULL DEFAULT NULL COMMENT '文件数量',
  `file1_URL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '附件1URL',
  `file2_URL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '附件2URL',
  `file3_URL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '附件3URL',
  `file4_URL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '附件4URL',
  `file5_URL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '附件5URl',
  `created_at` timestamp NOT NULL COMMENT '创建时间',
  `updated_at` timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ep_publish
-- ----------------------------
INSERT INTO `ep_publish` VALUES (1, 17, '演示一实验', '演示第一次实验', '2026-07-19 09:40:24', 0, 0, NULL, NULL, NULL, NULL, NULL, '2025-07-08 15:26:33', '2025-07-08 15:26:33');
INSERT INTO `ep_publish` VALUES (6, 17, '演示实验2修改版', '演示实验2修改版修改改改', '2026-08-06 09:40:24', 0, 4, 'http://192.168.78.91:10030/static/experiments/17/publish/cad36ff4-7d84-411d-a3c2-0731cfc24ade/b879027a-d58b-43a1-8838-fb04729c2121.pdf', 'http://192.168.78.91:10030/static/experiments/17/publish/cad36ff4-7d84-411d-a3c2-0731cfc24ade/abd40d6b-ae3b-4f75-bca1-22b620197d45.pdf', 'http://192.168.78.91:10030/static/experiments/17/publish/88a8d544-d30c-4d18-9991-4094fa1960de/fca6f097-8baa-49b4-a439-1f7b19425c36.pdf', 'http://192.168.78.91:10030/static/experiments/17/publish/862803e7-09bc-4410-80b8-31b81f573713/20250708233718_ovsduh/华迪实训平台学生使用手册.pdf', NULL, '2025-07-08 18:01:56', '2025-07-08 23:37:19');
INSERT INTO `ep_publish` VALUES (12, 17, '演示实验2修改版', '演示实验2修改版修改改改', '2026-08-06 09:40:24', 0, 3, 'http://192.168.78.91:10030/static/experiments/17/publish/50cd0512-7c34-4567-8b5a-d8ac32195594/20250709010010_n9ia85/Vue讲义.pdf', 'http://192.168.78.91:10030/static/experiments/17/publish/28fc8099-1c00-4923-bff9-bac8c6eb0cff/20250709010045_dzaud6/成绩证明模板.docx', 'http://192.168.78.91:10030/static/experiments/17/publish/47a9f1b9-6e31-4a66-ae43-8428526bfd4b/20250709010117_lrs43j/重大非限课程情况表.xlsx', NULL, NULL, '2025-07-09 01:00:10', '2025-07-09 01:01:17');
INSERT INTO `ep_publish` VALUES (37, 17, '123', NULL, '2025-07-30 00:00:08', 0, 1, 'http://192.168.78.91:10030/static/experiments/17/publish/36611bad-0860-419f-9588-d53f96c76391/20250709111058_nudhv4/Vue讲义.pdf', NULL, NULL, NULL, NULL, '2025-07-09 11:10:59', '2025-07-09 11:12:09');
INSERT INTO `ep_publish` VALUES (43, 10, '123', NULL, '2025-07-30 14:45:07', 0, 1, 'http://192.168.78.91:10030/static/experiments/10/publish/3ba9ecc0-1355-4d26-a55d-68284b1054f6/20250709121610_iu9qmz/华迪实训平台学生使用手册.pdf', NULL, NULL, NULL, NULL, '2025-07-09 12:16:10', '2025-07-09 14:45:13');
INSERT INTO `ep_publish` VALUES (44, 17, '演示实训1修改版', '演示实训1修改版修改改改', '2026-08-06 09:40:24', 1, 2, 'http://192.168.78.91:10030/static/experiments/17/publish/888f17e9-a9ac-49a6-b00a-c283427c0930/20250709195409_99ging/计算机科学与技术专业JavaEE+鸿蒙商业项目开发毕业实习方案（此为初步方案，若后期调整，则以实际执行方案为准）.pdf', 'http://192.168.78.91:10030/static/experiments/17/publish/888f17e9-a9ac-49a6-b00a-c283427c0930/20250709195409_53cdw4/讲义-MybatisPlus编程技术.pdf', NULL, NULL, NULL, '2025-07-09 19:52:02', '2025-07-09 19:54:10');
INSERT INTO `ep_publish` VALUES (45, 18, '18号课程的演示项目实训1', '演示第1次项目', '2026-07-12 09:40:24', 1, 0, NULL, NULL, NULL, NULL, NULL, '2025-07-10 09:18:22', '2025-07-10 09:18:22');
INSERT INTO `ep_publish` VALUES (46, 18, '项目实训3', '实训2的项目详情', '2025-07-23 11:08:10', 1, 0, NULL, NULL, NULL, NULL, NULL, '2025-07-10 10:58:21', '2025-07-10 11:03:27');
INSERT INTO `ep_publish` VALUES (48, 23, '高数第一次实验', '第一次实验的详情', '2025-07-13 22:26:41', 0, 1, 'http://192.168.78.91:10030/static/experiments/23/publish/ae207d86-02b8-409b-9968-13ed6999940d/20250710222708_zhc16u/第5章 SpringBoot JPA Mybatis.pdf', NULL, NULL, NULL, NULL, '2025-07-10 22:27:08', '2025-07-10 22:27:08');
INSERT INTO `ep_publish` VALUES (49, 23, '高数第一次实训', '第一次项目实训哦', '2025-07-13 22:30:33', 1, 1, 'http://192.168.78.91:10030/static/experiments/23/publish/89ebafec-8989-4b36-b71c-a02086b87695/20250710222745_0m2w0m/讲义-SpringBoot3+MyBatisPlus3编程技术(1).pdf', NULL, NULL, NULL, NULL, '2025-07-10 22:27:46', '2025-07-10 22:27:46');

-- ----------------------------
-- Table structure for ep_submit
-- ----------------------------
DROP TABLE IF EXISTS `ep_submit`;
CREATE TABLE `ep_submit`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '提交ID',
  `student_id` int NULL DEFAULT NULL COMMENT '学生ID',
  `experiment_id` int NOT NULL COMMENT '实验ID',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '提交的详情备注',
  `score` int NULL DEFAULT NULL COMMENT '评分',
  `created_at` timestamp NOT NULL COMMENT '创建时间',
  `updated_at` timestamp NOT NULL COMMENT '更新时间',
  `num_file` int NULL DEFAULT NULL COMMENT '文件数量',
  `file1_URL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '附件1URL',
  `file2_URL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '附件2URL',
  `file3_URL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '附件3URL',
  `file4_URL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '附件4URL',
  `file5_URL` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '附件5URl',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ep_submit
-- ----------------------------
INSERT INTO `ep_submit` VALUES (14, 13, 1, '修改第一次的提交', 66, '2025-07-08 18:27:00', '2025-07-08 18:40:06', 4, 'http://192.168.78.91:10030/static/experiments/1/submit/13/8dbfb7fe-1819-4bb6-82f4-bbb327547d83/45262267-af0b-4e02-b50c-dc38e4707468.drawio', 'http://192.168.78.91:10030/static/experiments/1/submit/13/8dbfb7fe-1819-4bb6-82f4-bbb327547d83/958a8f92-3b62-4775-b7e0-c0378b40afcf.drawio', 'http://192.168.78.91:10030/static/experiments/1/submit/13/b2e0eaff-e4c8-4bbb-b100-f1fd9f3290cb/dd7fe689-c8bf-48f9-984f-03d841115c68.docx', 'http://192.168.78.91:10030/static/experiments/1/submit/13/b2e0eaff-e4c8-4bbb-b100-f1fd9f3290cb/0e5f0205-3dae-44d1-9150-684c1cd11eb6.drawio', NULL);
INSERT INTO `ep_submit` VALUES (15, 13, 12, '修改第一次的提交', NULL, '2025-07-09 01:02:28', '2025-07-09 01:04:17', 3, 'http://192.168.78.91:10030/static/experiments/12/submit/13/44488f9d-d2c2-42df-9a0e-e90fbed65541/20250709010228_6zdp6w/程序结构.drawio', 'http://192.168.78.91:10030/static/experiments/12/submit/13/6d43dda7-c362-41dd-ac17-97a185ea5f2a/20250709010417_l7967i/20221381魏志航1.docx', 'http://192.168.78.91:10030/static/experiments/12/submit/13/6d43dda7-c362-41dd-ac17-97a185ea5f2a/20250709010417_ty6pzv/类.drawio', NULL, NULL);
INSERT INTO `ep_submit` VALUES (16, 14, 37, '123', 90, '2025-07-09 14:47:10', '2025-07-09 15:01:10', 1, 'http://192.168.78.91:10030/static/experiments/37/submit/14/fd852024-9c8f-4d61-9c64-56995f128acd/20250709145856_5aeeus/第5章 SpringBoot JPA Mybatis.docx', NULL, NULL, NULL, NULL);
INSERT INTO `ep_submit` VALUES (17, 14, 1, 'finish', 90, '2025-07-09 17:28:21', '2025-07-09 17:29:08', 1, 'http://192.168.78.91:10030/static/experiments/1/submit/14/4fce9f53-b093-45b2-9214-78cae8e45a4b/20250709172821_t3cu7q/产才荟训前评测使用说明.docx', NULL, NULL, NULL, NULL);
INSERT INTO `ep_submit` VALUES (19, 10, 44, '我是第一个提交', 77, '2025-07-09 23:17:52', '2025-07-09 23:19:36', 2, 'http://192.168.78.91:10030/static/experiments/44/submit/10/649c3145-064e-4694-a8df-b7ed9a0d7ade/20250709231752_t3c56m/Solvingtheshepherdingproblemheuristicsforherdingautonomous,interactingagents.pdf', 'http://192.168.78.91:10030/static/experiments/44/submit/10/649c3145-064e-4694-a8df-b7ed9a0d7ade/20250709231752_nw0309/校招现在你可以做什么.docx', NULL, NULL, NULL);
INSERT INTO `ep_submit` VALUES (20, 13, 45, '第2次提交', 90, '2025-07-10 11:25:18', '2025-07-10 11:55:44', 1, 'http://192.168.78.91:10030/static/experiments/45/submit/13/8cb79183-64b8-4a0b-a062-53b8644b9b3b/20250710112518_w80vi7/屏幕截图 2024-08-17 203311.png', NULL, NULL, NULL, NULL);
INSERT INTO `ep_submit` VALUES (21, 120, 48, '第一个交', 80, '2025-07-10 22:42:57', '2025-07-10 22:43:23', 2, 'http://192.168.78.91:10030/static/experiments/48/submit/120/8347fde5-f154-43be-9227-9cbb57869e44/20250710224256_h6tn5f/实训报告.docx', 'http://192.168.78.91:10030/static/experiments/48/submit/120/8347fde5-f154-43be-9227-9cbb57869e44/20250710224256_y8cbvy/22计科03班20221447谢泽川3.doc', NULL, NULL, NULL);
INSERT INTO `ep_submit` VALUES (22, 120, 49, '第一队先交', NULL, '2025-07-10 22:44:29', '2025-07-10 22:44:29', 1, 'http://192.168.78.91:10030/static/experiments/49/submit/120/4c25ff0c-ceb6-48fe-87cd-7954f009092d/20250710224429_euzahn/jmeter_proxy_step_by_step.pdf', NULL, NULL, NULL, NULL);
INSERT INTO `ep_submit` VALUES (23, 119, 48, 'test', NULL, '2025-07-11 01:10:09', '2025-07-11 01:10:09', 1, 'http://192.168.78.91:10030/static/experiments/48/submit/119/346f8c7d-6d87-4421-85c7-8b7c904dbc29/20250711011008_nmf52n/2- ArkTS容器.docx', NULL, NULL, NULL, NULL);
INSERT INTO `ep_submit` VALUES (24, 13, 48, '完成了实验', NULL, '2025-07-11 09:37:18', '2025-07-11 09:37:18', 1, 'http://192.168.78.91:10030/static/experiments/48/submit/13/6179a54a-d8ec-4f19-aa90-716a3543db49/20250711093718_xv88ai/1- 开发工具安装.docx', NULL, NULL, NULL, NULL);
INSERT INTO `ep_submit` VALUES (25, 122, 48, 'test', 90, '2025-07-11 10:39:26', '2025-07-11 10:45:01', 1, 'http://192.168.78.91:10030/static/experiments/48/submit/122/bca2e98b-9e7f-4bc2-93b2-442cf4e59976/20250711103943_1zbqh3/NLP知名老师关系图.BvybbUxx_ZJAtzo.webp', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for ep_team
-- ----------------------------
DROP TABLE IF EXISTS `ep_team`;
CREATE TABLE `ep_team`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `experiment_id` int NOT NULL COMMENT '实训ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '队伍名字',
  `header_id` int NOT NULL COMMENT '队长ID（user_id）',
  `created_at` timestamp NOT NULL COMMENT '创建时间',
  `updated_at` timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ep_team
-- ----------------------------
INSERT INTO `ep_team` VALUES (4, 44, '第1实训组', 10, '2025-07-09 23:14:09', '2025-07-09 23:14:09');
INSERT INTO `ep_team` VALUES (6, 45, '第2实训组', 13, '2025-07-10 10:53:14', '2025-07-10 10:53:14');
INSERT INTO `ep_team` VALUES (7, 49, '一队', 120, '2025-07-10 22:41:49', '2025-07-10 22:41:49');

SET FOREIGN_KEY_CHECKS = 1;
