/*
 Navicat Premium Dump SQL

 Source Server         : database
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : wk_homework

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 11/07/2025 11:33:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for eh_homework_answer_log
-- ----------------------------
DROP TABLE IF EXISTS `eh_homework_answer_log`;
CREATE TABLE `eh_homework_answer_log`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `homework_info_id` int NULL DEFAULT NULL COMMENT '作业信息id',
  `student_id` int NULL DEFAULT NULL COMMENT '学生id',
  `class_id` int NULL DEFAULT NULL COMMENT '班级id',
  `homework_id` int NULL DEFAULT NULL COMMENT '作业id',
  `status` int NULL DEFAULT NULL COMMENT '状态：11:开始、12:提交、21:机器批阅、23:教师批阅、41:作答进度',
  `info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '状态信息',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_homework_info_id`(`homework_info_id` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_class_id`(`class_id` ASC) USING BTREE,
  INDEX `idx_homework_id`(`homework_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2135425026 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作业作答日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of eh_homework_answer_log
-- ----------------------------
INSERT INTO `eh_homework_answer_log` VALUES (-2054569983, 25, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-2050490366, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-2033713150, 25, 118, 26, 9, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-2033602558, 27, 13, 33, 18, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-2029428734, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1966604286, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1958215678, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1933049854, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1924546558, 25, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1916272638, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1844879358, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1840750590, 26, 118, 26, 9, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1819713535, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1798807550, 4, 10, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1719115775, 26, 118, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1526202367, 25, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1521917951, 25, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1509425150, 25, 118, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1509335039, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1450680319, 25, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1408671742, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1379401727, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1349931007, 27, 122, 33, 18, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1345757183, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1316397055, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1270284286, 27, 119, 33, 18, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1232601086, 26, 118, 26, 9, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1219903486, 25, 118, 26, 9, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1106771967, 26, 118, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1035427839, 4, 10, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-1018576895, 25, 118, 26, 9, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-859308031, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-850808830, 27, 119, NULL, NULL, 23, '教师批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-691470335, 27, 121, 33, 18, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-616013822, 26, 118, NULL, NULL, 23, '教师批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-611844094, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-607625215, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-586616831, 27, 120, 33, 18, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-574005247, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-485900286, 25, 118, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-418881534, 25, 118, 26, 9, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-418791423, 25, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-351797246, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-351772671, 25, 118, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-280383487, 27, 122, 33, 18, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-246824958, 25, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-230162430, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-171442175, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-162963454, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-112607231, 25, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-104308734, 26, 118, 26, 9, 11, '补交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-66584575, 26, 118, 26, 9, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-62365694, 26, 118, NULL, NULL, 23, '教师批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (-41304062, 25, 118, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (38273026, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (67633154, 26, 118, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (139026434, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (193462274, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (197746689, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (227082241, 27, 119, 33, 18, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (231325698, 25, 118, 26, 9, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (247988226, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (264765441, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (277438466, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (348676097, 25, 118, 26, 9, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (369733633, 27, 122, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (478699522, 25, 118, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (487174146, 27, 13, 33, 18, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (571039745, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (571039746, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (571064321, 25, 118, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (575143937, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (583647234, 25, 118, 26, 9, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (587751426, 25, 118, NULL, NULL, 23, '教师批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (604504066, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (642342913, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (671674370, 27, 120, 33, 18, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (688431105, 4, 10, NULL, NULL, 23, '教师批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (784973826, 25, 118, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (814284801, 27, 119, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (864612353, 27, 120, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (906493954, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1023934465, 26, 118, 26, 9, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1023934466, 26, 118, 26, 9, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1032413185, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1078550529, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1103687682, 25, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1133010945, 26, 118, NULL, NULL, 23, '教师批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1175019522, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1267265538, 25, 118, 26, 9, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1367867394, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1393033218, 25, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1418199041, 26, 118, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1447559170, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1573388290, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1590276098, 27, 13, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1611137025, 26, 118, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1657298946, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1690869762, 4, 10, 1, 1, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1749573633, 25, 118, NULL, NULL, 23, '教师批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1770545153, 26, 118, 26, 9, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1825071106, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1871183874, 25, 118, 26, 9, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1913151490, 4, 10, 1, 1, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1921540098, 25, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1929904130, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1950916609, 4, 10, 1, 1, 12, '提交作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (1980297218, 25, 118, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (2089377794, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (2097700865, 26, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (2106089473, 26, 118, NULL, NULL, 21, '机器批阅', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (2131320833, 25, 118, 26, 9, 11, '开始作业', NULL, NULL);
INSERT INTO `eh_homework_answer_log` VALUES (2135425025, 25, 118, NULL, NULL, 21, '机器批阅', NULL, NULL);

-- ----------------------------
-- Table structure for eh_homework_answer_result
-- ----------------------------
DROP TABLE IF EXISTS `eh_homework_answer_result`;
CREATE TABLE `eh_homework_answer_result`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NULL DEFAULT NULL COMMENT '学生id',
  `homework_info_id` int NULL DEFAULT NULL COMMENT '作业信息id',
  `question_id` int NULL DEFAULT NULL COMMENT '题目id',
  `option_id` int NULL DEFAULT NULL COMMENT '选项id',
  `answer` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '答案：主观题使用',
  `result_type` int NULL DEFAULT NULL COMMENT '结果类型：对、错、半错',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_homework_info_id`(`homework_info_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 87 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作业作答结果' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of eh_homework_answer_result
-- ----------------------------
INSERT INTO `eh_homework_answer_result` VALUES (1, 10, 4, 101, NULL, '这是一道主观题的回答，学生需要输入文字作为答案。', NULL, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (2, 10, 4, 102, NULL, '这是另一道主观题的回答，可能是简答题或者论述题。', NULL, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (3, 10, 4, 103, NULL, '这是另一道主观题的回答，可能是简答题或者论述题。', NULL, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (4, 118, 26, 944, 2411, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (5, 118, 26, 944, 2412, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (6, 118, 26, 945, 2416, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (7, 118, 26, 945, 2417, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (8, 118, 26, 946, 2420, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (9, 118, 26, 946, 2421, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (10, 118, 26, 947, 2423, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (11, 118, 26, 947, 2424, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (12, 118, 26, 948, 2428, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (13, 118, 26, 948, 2429, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (14, 118, 26, 949, 2432, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (15, 118, 26, 949, 2433, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (16, 118, 26, 950, 2436, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (17, 118, 26, 950, 2437, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (18, 118, 26, 944, 2411, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (19, 118, 26, 944, 2412, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (20, 118, 26, 945, 2416, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (21, 118, 26, 945, 2417, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (22, 118, 26, 946, 2420, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (23, 118, 26, 946, 2421, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (24, 118, 26, 947, 2423, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (25, 118, 26, 947, 2424, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (26, 118, 26, 948, 2428, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (27, 118, 26, 948, 2429, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (28, 118, 26, 949, 2432, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (29, 118, 26, 949, 2433, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (30, 118, 26, 950, 2436, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (31, 118, 26, 950, 2437, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (32, 118, 25, 944, 2412, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (33, 118, 25, 944, 2413, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (34, 118, 25, 945, 2416, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (35, 118, 25, 945, 2417, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (36, 118, 25, 946, 2420, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (37, 118, 25, 946, 2421, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (38, 118, 25, 947, 2424, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (39, 118, 25, 947, 2425, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (40, 118, 25, 948, 2428, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (41, 118, 25, 948, 2429, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (42, 118, 25, 949, 2432, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (43, 118, 25, 949, 2433, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (44, 118, 25, 950, 2436, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (45, 118, 25, 950, 2437, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (46, 118, 25, 944, 2412, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (47, 118, 25, 944, 2413, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (48, 118, 25, 945, 2416, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (49, 118, 25, 945, 2417, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (50, 118, 25, 946, 2420, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (51, 118, 25, 946, 2421, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (52, 118, 25, 947, 2424, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (53, 118, 25, 947, 2425, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (54, 118, 25, 948, 2427, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (55, 118, 25, 948, 2428, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (56, 118, 26, 944, 2411, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (57, 118, 26, 944, 2411, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (58, 118, 26, 944, 2411, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (59, 118, 26, 944, 2411, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (60, 118, 25, 944, 2411, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (61, 118, 25, 944, 2412, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (62, 118, 25, 944, 2411, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (63, 10, 4, 101, NULL, '这是一道主观题的回答，学生需要输入文字作为答案。', NULL, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (64, 10, 4, 102, NULL, '这是另一道主观题的回答，可能是简答题或者论述题。', NULL, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (65, 10, 4, 103, NULL, '这是另一道主观题的回答，可能是简答题或者论述题。', NULL, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (66, 118, 25, 944, 2411, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (67, 118, 25, 944, 2413, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (68, 118, 25, 945, 2415, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (69, 118, 25, 944, 2411, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (70, 118, 25, 944, 2411, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (71, 118, 25, 944, 2411, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (72, 118, 25, 944, 2411, '1', 30, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (73, 120, 27, 1187, 3210, '1', 40, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (74, 120, 27, 1188, 3215, '1', 40, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (75, 120, 27, 1189, 3219, '1', 40, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (76, 120, 27, 1195, 3244, '1', 40, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (77, 120, 27, 1200, 3253, '<p>180</p>', 10, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (78, 120, 27, 1201, 3254, '<p>5</p>', 10, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (79, 119, 27, 1187, 3211, '1', 40, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (80, 119, 27, 1188, 3217, '1', 40, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (81, 119, 27, 1189, 3218, '1', 40, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (82, 119, 27, 1195, 3245, '1', 40, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (83, 119, 27, 1200, 3253, '<p>30</p>', 10, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (84, 119, 27, 1201, 3254, '<p>1</p>', 10, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (85, 13, 27, 1187, 3211, '1', 40, NULL, NULL);
INSERT INTO `eh_homework_answer_result` VALUES (86, 122, 27, 1187, 3211, '1', 40, NULL, NULL);

-- ----------------------------
-- Table structure for eh_homework_class
-- ----------------------------
DROP TABLE IF EXISTS `eh_homework_class`;
CREATE TABLE `eh_homework_class`  (
  `class_id` int NOT NULL COMMENT '班级id',
  `homework_info_id` int NOT NULL COMMENT '作业信息id',
  PRIMARY KEY (`class_id`, `homework_info_id`) USING BTREE,
  INDEX `idx_homework_info_id`(`homework_info_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作业班级关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of eh_homework_class
-- ----------------------------
INSERT INTO `eh_homework_class` VALUES (1, 4);
INSERT INTO `eh_homework_class` VALUES (2, 4);
INSERT INTO `eh_homework_class` VALUES (4, 6);
INSERT INTO `eh_homework_class` VALUES (5, 9);
INSERT INTO `eh_homework_class` VALUES (5, 10);
INSERT INTO `eh_homework_class` VALUES (26, 24);
INSERT INTO `eh_homework_class` VALUES (26, 25);
INSERT INTO `eh_homework_class` VALUES (26, 26);
INSERT INTO `eh_homework_class` VALUES (33, 27);

-- ----------------------------
-- Table structure for eh_homework_info
-- ----------------------------
DROP TABLE IF EXISTS `eh_homework_info`;
CREATE TABLE `eh_homework_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '作业标题',
  `homework_id` int NOT NULL COMMENT '作业id',
  `teacher_id` int NULL DEFAULT NULL COMMENT '老师id',
  `course_id` int NOT NULL COMMENT '课程id',
  `question_disorder` tinyint(1) NULL DEFAULT 0 COMMENT '题目乱序',
  `option_disorder` tinyint(1) NULL DEFAULT 0 COMMENT '选项乱序',
  `end_visible` tinyint(1) NULL DEFAULT 0 COMMENT '结束可见',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '截止时间',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作业说明',
  `allow_late_submit` tinyint(1) NULL DEFAULT 0 COMMENT '是否允许补交',
  `late_end_time` datetime NULL DEFAULT NULL COMMENT '补交截止时间',
  `late_deduction` float NULL DEFAULT 0 COMMENT '补交扣分比例',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_homework_id`(`homework_id` ASC) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作业发布信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of eh_homework_info
-- ----------------------------
INSERT INTO `eh_homework_info` VALUES (4, 'Java编程基础作业', 1, 10, 1, 0, 0, 1, '2025-07-01 10:00:00', '2026-12-31 23:59:59', '完成第5章所有练习题', 1, '2024-01-05 23:59:59', 0.2, NULL, NULL);
INSERT INTO `eh_homework_info` VALUES (6, '点边对价管向日作业', 1, 10, 2, 0, 0, 1, '2025-12-01 10:00:00', '2025-12-25 23:59:59', '完成所有练习题', 1, '2025-12-26 23:59:59', 0.2, NULL, NULL);
INSERT INTO `eh_homework_info` VALUES (7, '格义又成单音务作业', 1, 10, 3, 0, 0, 1, '2025-12-01 10:00:00', '2025-12-25 23:59:59', '完成所有练习题', 1, '2025-12-26 23:59:59', 0.2, NULL, NULL);
INSERT INTO `eh_homework_info` VALUES (8, '格义又成单音务作业', 1, 10, 3, 0, 0, 1, '2025-12-01 10:00:00', '2025-12-25 23:59:59', '完成所有练习题', 1, '2025-12-26 23:59:59', 0.2, NULL, NULL);
INSERT INTO `eh_homework_info` VALUES (9, '格义又成单音务作业', 1, 10, 3, 0, 0, 1, '2025-12-01 10:00:00', '2025-12-25 23:59:59', '完成所有练习题', 1, '2025-12-26 23:59:59', 0.2, NULL, NULL);
INSERT INTO `eh_homework_info` VALUES (10, '格义又成单音务作业', 1, 10, 3, 0, 0, 1, '2025-12-01 10:00:00', '2025-12-25 23:59:59', '完成所有练习题', 1, '2025-12-26 23:59:59', 0.2, NULL, NULL);
INSERT INTO `eh_homework_info` VALUES (15, '格义又成单音务作业', 1, 10, 3, 0, 0, 1, '2025-12-01 10:00:00', '2025-12-25 23:59:59', '完成所有练习题', 1, '2025-12-26 23:59:59', 0.2, NULL, NULL);
INSERT INTO `eh_homework_info` VALUES (16, '格义又成单音务作业', 1, 10, 3, 0, 0, 1, '2025-12-01 10:00:00', '2025-12-25 23:59:59', '完成所有练习题', 1, '2025-12-26 23:59:59', 0.2, NULL, NULL);
INSERT INTO `eh_homework_info` VALUES (17, '格义又成单音务作业', 1, 10, 3, 0, 0, 1, '2025-12-01 10:00:00', '2025-12-25 23:59:59', '完成所有练习题', 1, '2025-12-26 23:59:59', 0.2, NULL, NULL);
INSERT INTO `eh_homework_info` VALUES (18, '格义又成单音务作业', 1, 10, 3, 0, 0, 1, '2025-12-01 10:00:00', '2025-12-25 23:59:59', '完成所有练习题', 1, '2025-12-26 23:59:59', 0.2, NULL, NULL);
INSERT INTO `eh_homework_info` VALUES (24, '作业5', 9, 10, 17, 1, 1, 1, '2025-07-10 21:47:17', '2025-07-12 21:48:09', '作业555', 1, '2025-07-14 21:47:31', 5, NULL, NULL);
INSERT INTO `eh_homework_info` VALUES (25, '作业6', 9, 10, 17, 1, 1, 1, '2025-07-08 21:58:08', '2025-07-12 21:56:20', '作业', 1, '2025-07-13 21:56:24', 5, NULL, NULL);
INSERT INTO `eh_homework_info` VALUES (26, '格义又成单音务作业', 9, 10, 17, 0, 0, 1, '2025-07-08 10:00:00', '2025-07-09 07:00:00', '完成所有练习题', 1, '2025-12-26 23:59:59', 0.2, NULL, NULL);
INSERT INTO `eh_homework_info` VALUES (27, '第一次作业', 18, 117, 23, 0, 0, 0, '2025-07-10 22:25:29', '2025-07-15 22:23:32', '快点交哦', 0, NULL, 5, NULL, NULL);

-- ----------------------------
-- Table structure for eh_homework_paper
-- ----------------------------
DROP TABLE IF EXISTS `eh_homework_paper`;
CREATE TABLE `eh_homework_paper`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '作业标题',
  `introduce` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作业介绍',
  `course_id` int NOT NULL COMMENT '课程id',
  `teacher_id` int NULL DEFAULT NULL COMMENT '教师id',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_course_id`(`course_id` ASC) USING BTREE,
  INDEX `idx_teacher_id`(`teacher_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作业试卷信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of eh_homework_paper
-- ----------------------------
INSERT INTO `eh_homework_paper` VALUES (1, 'Java编程基础作业(更新版)', '本次作业主要考察Java基础语法和面向对象编程概念', 1, 10, NULL, NULL);
INSERT INTO `eh_homework_paper` VALUES (5, 'Java编程基础作业', '本次作业主要考察Java基础语法和面向对象编程概念', 1, 10, NULL, NULL);
INSERT INTO `eh_homework_paper` VALUES (6, 'Java编程基础作业', '本次作业主要考察Java基础语法和面向对象编程概念', 1, 10, NULL, NULL);
INSERT INTO `eh_homework_paper` VALUES (7, 'Java编程基础作业', '本次作业主要考察Java基础语法和面向对象编程概念', 1, 10, NULL, NULL);
INSERT INTO `eh_homework_paper` VALUES (9, '作业1', '作业', 17, 10, NULL, NULL);
INSERT INTO `eh_homework_paper` VALUES (15, '自动组卷作业', '系统自动生成的作业', 6, 10, NULL, NULL);
INSERT INTO `eh_homework_paper` VALUES (17, '作业2', '必须完成', 17, 10, NULL, NULL);
INSERT INTO `eh_homework_paper` VALUES (18, '第一次作业', '快点交哦', 23, 117, NULL, NULL);
INSERT INTO `eh_homework_paper` VALUES (19, '作业1', '必须完成', 16, 10, NULL, NULL);

-- ----------------------------
-- Table structure for eh_homework_question
-- ----------------------------
DROP TABLE IF EXISTS `eh_homework_question`;
CREATE TABLE `eh_homework_question`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `question_id` int NOT NULL COMMENT '题目id',
  `homework_id` int NOT NULL COMMENT '作业id',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_homework_question`(`homework_id` ASC, `question_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 111 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作业试题关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of eh_homework_question
-- ----------------------------
INSERT INTO `eh_homework_question` VALUES (29, 101, 1, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (30, 102, 1, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (31, 103, 1, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (32, 104, 1, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (33, 105, 1, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (34, 101, 5, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (35, 102, 5, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (36, 103, 5, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (37, 104, 5, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (38, 105, 5, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (39, 101, 6, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (40, 102, 6, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (41, 103, 6, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (42, 104, 6, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (43, 105, 6, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (44, 101, 7, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (45, 102, 7, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (46, 103, 7, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (47, 104, 7, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (48, 105, 7, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (86, 944, 9, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (87, 945, 9, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (88, 946, 9, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (89, 947, 9, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (90, 948, 9, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (91, 949, 9, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (92, 950, 9, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (93, 944, 17, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (94, 945, 17, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (95, 946, 17, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (96, 947, 17, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (97, 1200, 18, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (98, 1201, 18, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (99, 1187, 18, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (100, 1188, 18, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (101, 1189, 18, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (102, 1195, 18, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (107, 931, 19, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (108, 932, 19, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (109, 933, 19, NULL, NULL);
INSERT INTO `eh_homework_question` VALUES (110, 934, 19, NULL, NULL);

-- ----------------------------
-- Table structure for eh_homework_score_record
-- ----------------------------
DROP TABLE IF EXISTS `eh_homework_score_record`;
CREATE TABLE `eh_homework_score_record`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NULL DEFAULT NULL COMMENT '用户id',
  `class_id` int NULL DEFAULT NULL COMMENT '班级id',
  `homework_info_id` int NULL DEFAULT NULL COMMENT '作业信息id',
  `question_id` int NULL DEFAULT NULL COMMENT '题目id',
  `score` float NULL DEFAULT NULL COMMENT '得分',
  `comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评语',
  `result_type` int NULL DEFAULT NULL COMMENT '结果类型：对、错、半错',
  `review_type` int NULL DEFAULT NULL COMMENT '评阅类型',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_class_id`(`class_id` ASC) USING BTREE,
  INDEX `idx_homework_info_id`(`homework_info_id` ASC) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 125 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作业得分记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of eh_homework_score_record
-- ----------------------------
INSERT INTO `eh_homework_score_record` VALUES (1, 10, NULL, 4, 101, 10, '答案正确，思路清晰', 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (2, 10, NULL, 4, 102, 8.5, '基本正确，但有小错误', 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (3, 10, NULL, 4, 103, 5, '答案不完整，缺少关键分析', 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (4, 118, 26, 26, 944, 1.5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (5, 118, 26, 26, 945, 2.5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (6, 118, 26, 26, 946, 2.5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (7, 118, 26, 26, 947, 2.5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (8, 118, 26, 26, 948, 2.5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (9, 118, 26, 26, 949, 2.5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (10, 118, 26, 26, 950, 2.5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (11, 118, 26, 26, 944, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (12, 118, 26, 26, 945, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (13, 118, 26, 26, 946, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (14, 118, 26, 26, 947, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (15, 118, 26, 26, 948, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (16, 118, 26, 26, 949, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (17, 118, 26, 26, 950, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (18, 118, 26, 25, 944, 1.5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (19, 118, 26, 25, 945, 2.5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (20, 118, 26, 25, 946, 1.5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (21, 118, 26, 25, 947, 2.5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (22, 118, 26, 25, 948, 2.5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (23, 118, 26, 25, 949, 2.5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (24, 118, 26, 25, 950, 2.5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (25, 118, 26, 25, 944, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (26, 118, 26, 25, 945, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (27, 118, 26, 25, 946, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (28, 118, 26, 25, 947, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (29, 118, 26, 25, 948, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (30, 118, 26, 25, 949, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (31, 118, 26, 25, 950, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (32, 118, 26, 26, 944, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (33, 118, 26, 26, 945, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (34, 118, 26, 26, 946, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (35, 118, 26, 26, 947, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (36, 118, 26, 26, 948, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (37, 118, 26, 26, 949, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (38, 118, 26, 26, 950, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (39, 118, 26, 26, 944, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (40, 118, 26, 26, 945, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (41, 118, 26, 26, 946, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (42, 118, 26, 26, 947, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (43, 118, 26, 26, 948, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (44, 118, 26, 26, 949, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (45, 118, 26, 26, 950, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (46, 118, 26, 26, 944, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (47, 118, 26, 26, 945, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (48, 118, 26, 26, 946, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (49, 118, 26, 26, 947, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (50, 118, 26, 26, 948, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (51, 118, 26, 26, 949, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (52, 118, 26, 26, 950, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (53, 118, 26, 26, 944, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (54, 118, 26, 26, 945, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (55, 118, 26, 26, 946, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (56, 118, 26, 26, 947, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (57, 118, 26, 26, 948, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (58, 118, 26, 26, 949, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (59, 118, 26, 26, 950, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (60, 118, 26, 25, 944, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (61, 118, 26, 25, 945, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (62, 118, 26, 25, 946, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (63, 118, 26, 25, 947, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (64, 118, 26, 25, 948, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (65, 118, 26, 25, 949, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (66, 118, 26, 25, 950, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (67, 118, 26, 25, 944, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (68, 118, 26, 25, 945, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (69, 118, 26, 25, 946, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (70, 118, 26, 25, 947, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (71, 118, 26, 25, 948, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (72, 118, 26, 25, 949, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (73, 118, 26, 25, 950, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (74, 118, 26, 25, 944, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (75, 118, 26, 25, 945, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (76, 118, 26, 25, 946, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (77, 118, 26, 25, 947, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (78, 118, 26, 25, 948, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (79, 118, 26, 25, 949, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (80, 118, 26, 25, 950, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (81, 118, 26, 25, 944, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (82, 118, 26, 25, 945, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (83, 118, 26, 25, 946, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (84, 118, 26, 25, 947, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (85, 118, 26, 25, 948, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (86, 118, 26, 25, 949, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (87, 118, 26, 25, 950, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (88, 118, 26, 25, 944, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (89, 118, 26, 25, 945, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (90, 118, 26, 25, 946, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (91, 118, 26, 25, 947, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (92, 118, 26, 25, 948, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (93, 118, 26, 25, 949, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (94, 118, 26, 25, 950, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (95, 118, 26, 25, 944, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (96, 118, 26, 25, 945, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (97, 118, 26, 25, 946, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (98, 118, 26, 25, 947, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (99, 118, 26, 25, 948, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (100, 118, 26, 25, 949, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (101, 118, 26, 25, 950, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (102, 118, 26, 25, 944, 2.5, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (103, 118, 26, 25, 945, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (104, 118, 26, 25, 946, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (105, 118, 26, 25, 947, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (106, 118, 26, 25, 948, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (107, 118, 26, 25, 949, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (108, 118, 26, 25, 950, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (109, 120, 33, 27, 1187, 5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (110, 120, 33, 27, 1188, 5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (111, 120, 33, 27, 1189, 5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (112, 120, 33, 27, 1195, 5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (113, 119, 33, 27, 1187, 5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (114, 119, 33, 27, 1188, 5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (115, 119, 33, 27, 1189, 5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (116, 119, 33, 27, 1195, 5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (117, 13, 33, 27, 1187, 5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (118, 13, 33, 27, 1188, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (119, 13, 33, 27, 1189, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (120, 13, 33, 27, 1195, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (121, 122, 33, 27, 1187, 5, NULL, 40, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (122, 122, 33, 27, 1188, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (123, 122, 33, 27, 1189, 0, NULL, 30, NULL, NULL, NULL);
INSERT INTO `eh_homework_score_record` VALUES (124, 122, 33, 27, 1195, 0, NULL, 30, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for eh_homework_submit
-- ----------------------------
DROP TABLE IF EXISTS `eh_homework_submit`;
CREATE TABLE `eh_homework_submit`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NULL DEFAULT NULL COMMENT '学生id',
  `homework_info_id` int NULL DEFAULT NULL COMMENT '作业id',
  `status` tinyint NULL DEFAULT 0 COMMENT '提交状态：0-未提交，1-已提交，2-已批改',
  `total_score` float NULL DEFAULT 0 COMMENT '总分',
  `is_late_submit` tinyint(1) NULL DEFAULT 0 COMMENT '是否逾期提交',
  `submit_time` datetime NULL DEFAULT NULL COMMENT '提交时间',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_homework`(`user_id` ASC, `homework_info_id` ASC) USING BTREE,
  INDEX `idx_homework_info_id`(`homework_info_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '作业提交状态表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of eh_homework_submit
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
