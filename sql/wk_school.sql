/*
 Navicat Premium Dump SQL

 Source Server         : MySql
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : wk_school

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 11/07/2025 11:33:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ec_classes
-- ----------------------------
DROP TABLE IF EXISTS `ec_classes`;
CREATE TABLE `ec_classes`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '班级名称',
  `course_id` int UNSIGNED NOT NULL COMMENT '课程id',
  `teacher_id` int NOT NULL COMMENT '老师id:为了方便查找',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `course_id`(`course_id` ASC) USING BTREE,
  INDEX `teacher_id`(`teacher_id` ASC) USING BTREE,
  CONSTRAINT `ec_classes_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `ec_courses` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ec_classes_ibfk_2` FOREIGN KEY (`teacher_id`) REFERENCES `es_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ec_classes
-- ----------------------------
INSERT INTO `ec_classes` VALUES (1, '91111班', 1, 10, '2022-10-13 17:58:40', '2025-07-05 16:22:34');
INSERT INTO `ec_classes` VALUES (2, '大数据211班', 1, 10, '2022-10-13 17:58:56', '2025-07-05 16:22:20');
INSERT INTO `ec_classes` VALUES (3, '科技进步班', 5, 10, '2022-10-14 22:32:40', '2025-07-05 16:22:14');
INSERT INTO `ec_classes` VALUES (4, '二班', 2, 10, '2022-10-14 22:33:14', '2025-07-05 16:22:07');
INSERT INTO `ec_classes` VALUES (5, '216班', 3, 10, '2022-10-14 22:33:29', '2025-07-05 16:22:02');
INSERT INTO `ec_classes` VALUES (6, '口44班', 5, 10, '2022-10-14 22:33:59', '2025-07-05 16:21:57');
INSERT INTO `ec_classes` VALUES (12, '计科211班', 6, 10, '2022-10-16 16:42:40', '2025-07-05 16:21:51');
INSERT INTO `ec_classes` VALUES (13, '计算10001班', 9, 10, '2022-10-16 15:33:04', '2025-07-05 16:21:48');
INSERT INTO `ec_classes` VALUES (14, 'ff班', 9, 10, '2022-10-16 15:37:45', '2025-07-05 16:21:40');
INSERT INTO `ec_classes` VALUES (15, '机械班', 9, 10, '2022-12-03 20:02:06', '2022-12-03 20:28:03');
INSERT INTO `ec_classes` VALUES (17, '物流321班', 6, 10, '2022-10-16 19:52:04', '2025-07-05 16:21:34');
INSERT INTO `ec_classes` VALUES (18, '毕业班', 7, 10, '2022-10-23 20:18:55', '2022-10-23 20:18:55');
INSERT INTO `ec_classes` VALUES (19, '测试班级', 10, 10, '2023-02-05 19:54:57', '2023-02-05 19:54:57');
INSERT INTO `ec_classes` VALUES (20, 'c++2班', 11, 10, '2023-02-07 14:34:05', '2025-07-05 16:19:58');
INSERT INTO `ec_classes` VALUES (21, '888班', 12, 14, '2023-02-07 15:20:37', '2025-07-05 16:21:30');
INSERT INTO `ec_classes` VALUES (22, '6666班', 13, 10, '2023-02-08 10:25:18', '2025-07-05 16:21:24');
INSERT INTO `ec_classes` VALUES (23, '计算机', 14, 10, '2023-02-08 14:01:20', '2023-02-08 14:01:20');
INSERT INTO `ec_classes` VALUES (24, '计算机211班', 15, 10, '2023-02-08 14:45:53', '2025-07-05 16:20:03');
INSERT INTO `ec_classes` VALUES (25, '计算机300班', 16, 10, '2023-02-08 14:56:25', '2025-07-05 16:20:10');
INSERT INTO `ec_classes` VALUES (26, '演示班级', 17, 10, '2023-02-22 16:37:17', '2023-02-22 16:37:17');
INSERT INTO `ec_classes` VALUES (27, '燕云1班', 20, 13, '2025-07-05 22:27:54', '2025-07-05 22:27:54');
INSERT INTO `ec_classes` VALUES (28, '燕云2班', 20, 13, '2025-07-05 22:28:00', '2025-07-05 22:28:00');
INSERT INTO `ec_classes` VALUES (29, '信息1班', 19, 10, '2025-07-05 22:42:46', '2025-07-05 22:42:46');
INSERT INTO `ec_classes` VALUES (30, '通信100班', 18, 10, '2025-07-05 22:43:03', '2025-07-05 22:43:03');
INSERT INTO `ec_classes` VALUES (31, '大英77777班', 21, 13, '2025-07-06 09:40:35', '2025-07-06 09:40:35');
INSERT INTO `ec_classes` VALUES (32, '退课班级', 22, 10, '2025-07-10 11:21:46', '2025-07-10 11:36:39');
INSERT INTO `ec_classes` VALUES (33, '精品高数1班', 23, 117, '2025-07-10 22:06:29', '2025-07-10 22:06:29');
INSERT INTO `ec_classes` VALUES (34, '精品程序设计1班', 24, 10, '2025-07-10 22:13:53', '2025-07-10 22:13:53');

-- ----------------------------
-- Table structure for ec_course_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `ec_course_evaluation`;
CREATE TABLE `ec_course_evaluation`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NULL DEFAULT NULL COMMENT '学生ID',
  `course_id` int NULL DEFAULT NULL COMMENT '课程ID',
  `video_score` int NOT NULL COMMENT '视频评分（五分制）',
  `exam_score` int NOT NULL COMMENT '考试评分（五分制）',
  `experiment_score` int NOT NULL COMMENT '实验评分（五分制）',
  `project_score` int NOT NULL COMMENT '项目评分（五分制）',
  `homework_score` int NOT NULL COMMENT '作业评分（五分制）',
  `sum_score` float NULL DEFAULT NULL COMMENT '总评分（五分制）',
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价详情',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ec_course_evaluation
-- ----------------------------
INSERT INTO `ec_course_evaluation` VALUES (1, 10, 21, 4, 5, 3, 4, 5, 4.2, '棒棒', '2025-07-10 12:17:31', '2025-07-10 12:17:31');
INSERT INTO `ec_course_evaluation` VALUES (2, 10, 20, 4, 5, 3, 5, 5, 4.4, '好好好，好课啊', '2025-07-10 12:17:31', '2025-07-10 12:17:31');
INSERT INTO `ec_course_evaluation` VALUES (3, 10, 1, 3, 3, 3, 4, 5, 3.6, '强力推荐', '2025-07-10 12:17:31', '2025-07-10 12:17:31');
INSERT INTO `ec_course_evaluation` VALUES (4, 8, 2, 3, 2, 5, 5, 2, 3.4, '还可以', '2025-07-10 12:17:31', '2025-07-10 12:17:31');
INSERT INTO `ec_course_evaluation` VALUES (5, 9, 10, 5, 1, 2, 2, 1, 2.2, '垃圾', '2025-07-10 12:17:31', '2025-07-10 12:17:31');
INSERT INTO `ec_course_evaluation` VALUES (6, 12, 9, 5, 4, 4, 5, 4, 4.4, '还行', '2025-07-10 12:17:31', '2025-07-10 12:17:31');
INSERT INTO `ec_course_evaluation` VALUES (7, 13, 21, 5, 5, 5, 5, 5, 5, '666', '2025-07-10 12:17:31', '2025-07-10 12:17:31');
INSERT INTO `ec_course_evaluation` VALUES (8, 119, 23, 5, 5, 4, 4, 5, 4.6, NULL, '2025-07-10 22:09:47', '2025-07-10 22:09:47');
INSERT INTO `ec_course_evaluation` VALUES (9, 120, 23, 5, 5, 5, 5, 5, 5, NULL, '2025-07-10 22:50:27', '2025-07-10 22:50:27');

-- ----------------------------
-- Table structure for ec_courses
-- ----------------------------
DROP TABLE IF EXISTS `ec_courses`;
CREATE TABLE `ec_courses`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '课程名称',
  `cover` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '课程封面',
  `introduce` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '课程介绍',
  `is_public` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否公开:0：不公开（邀请码）,1公开（搜索）',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '课程状态:0:正常，1：结课',
  `user_id` int NOT NULL COMMENT '创建者',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `ec_courses_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `es_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci COMMENT = '课程信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ec_courses
-- ----------------------------
INSERT INTO `ec_courses` VALUES (1, 'SpringCloud课程设计', 'http://192.168.78.91:10030/static/covers/course/springcloud.png ', 'hello hello ', 1, 1, 10, '2022-10-13 17:12:39', '2025-07-11 02:35:22');
INSERT INTO `ec_courses` VALUES (2, '自动化', 'http://192.168.78.91:10030/static/covers/course/auto.png ', 'sunt Lorem', 1, 0, 10, '2022-10-13 19:22:51', '2025-07-11 02:35:45');
INSERT INTO `ec_courses` VALUES (3, '工程学', 'http://192.168.78.91:10030/static/python.png ', 'cillum Ut', 0, 0, 10, '2022-10-13 19:23:55', '2025-07-09 18:09:24');
INSERT INTO `ec_courses` VALUES (4, '前端开发Vue课程', 'http://192.168.78.91:10030/static/highmath.png ', 'nulla esse Excepteur', 0, 1, 8, '2022-10-14 21:17:08', '2025-07-11 02:23:20');
INSERT INTO `ec_courses` VALUES (5, '文明经典课程', 'http://192.168.78.91:10030/static/covers/course/culture.png ', 'nulla esse Excepteur', 1, 1, 10, '2022-10-14 21:18:55', '2025-07-11 02:36:17');
INSERT INTO `ec_courses` VALUES (6, '前端开发Vue实践：考试系统', 'http://192.168.78.91:10030/static/covers/course/vue.png ', '6666你好spring ', 1, 0, 10, '2022-10-15 18:37:41', '2025-07-11 02:36:52');
INSERT INTO `ec_courses` VALUES (7, '无人机课题教学', 'http://192.168.78.91:10030/static/covers/course/notpeople.png ', '66666666', 0, 0, 10, '2022-10-15 19:05:06', '2025-07-11 02:44:51');
INSERT INTO `ec_courses` VALUES (8, 'mysql课程', 'http://192.168.78.91:10030/static/python.png ', '22222', 0, 1, 10, '2022-10-15 19:05:50', '2025-07-11 02:23:35');
INSERT INTO `ec_courses` VALUES (9, 'Java高级基础', 'http://192.168.78.91:10030/static/covers/course/java.png ', '111111', 1, 0, 10, '2022-10-16 08:52:51', '2025-07-11 02:37:12');
INSERT INTO `ec_courses` VALUES (10, '测试课程', 'http://192.168.78.91:10030/static/covers/course/test.png ', '测试课程', 1, 0, 10, '2023-02-05 19:16:47', '2025-07-11 02:37:22');
INSERT INTO `ec_courses` VALUES (11, '数据分析', 'http://192.168.78.91:10030/static/covers/course/data.png ', '了了了了了了', 1, 1, 10, '2023-02-07 14:33:52', '2025-07-11 02:37:37');
INSERT INTO `ec_courses` VALUES (12, 'Linux命令教学', 'http://192.168.78.91:10030/static/python.png ', 'tttttttttt', 0, 1, 14, '2023-02-07 15:19:03', '2025-07-09 18:09:24');
INSERT INTO `ec_courses` VALUES (13, '英语', 'http://192.168.78.91:10030/static/covers/course/english.png ', '66666666666', 1, 1, 10, '2023-02-08 10:25:08', '2025-07-11 02:37:46');
INSERT INTO `ec_courses` VALUES (14, '人工智能机器学习', 'http://192.168.78.91:10030/static/covers/course/ai.png ', '测试课程', 1, 1, 10, '2023-02-08 14:01:08', '2025-07-11 02:37:57');
INSERT INTO `ec_courses` VALUES (15, '人工智能基础', 'http://192.168.78.91:10030/static/covers/course/aii.png ', '为考测试课程', 1, 1, 10, '2023-02-08 14:45:42', '2025-07-11 02:38:03');
INSERT INTO `ec_courses` VALUES (16, '为考测试课程', 'http://192.168.78.91:10030/static/covers/course/test2.png ', '为考测试课程', 1, 0, 10, '2023-02-08 14:56:15', '2025-07-11 02:45:03');
INSERT INTO `ec_courses` VALUES (17, '演示课程', 'http://192.168.78.91:10030/static/covers/course/show.png ', '数据演示', 1, 0, 10, '2022-10-16 08:52:51', '2025-07-11 02:38:37');
INSERT INTO `ec_courses` VALUES (18, '通信原理', 'http://192.168.78.91:10030/static/covers/course/fiveG.png ', '通信通信', 1, 0, 10, '2025-07-05 15:47:20', '2025-07-11 02:38:57');
INSERT INTO `ec_courses` VALUES (19, '信息挖掘', 'http://192.168.78.91:10030/static/covers/course/data2.png ', '信息哇哇哇', 1, 0, 10, '2025-07-05 15:48:56', '2025-07-11 02:39:05');
INSERT INTO `ec_courses` VALUES (20, '燕云教学课', 'http://192.168.78.91:10030/static/covers/course/cloud.png ', '来玩燕云十六声', 1, 0, 13, '2025-07-05 21:30:01', '2025-07-11 02:39:18');
INSERT INTO `ec_courses` VALUES (21, '大学英语', 'http://192.168.78.91:10030/static/13/course/6869d406038b658923611648.jpg', '大学的超难英语666', 1, 0, 13, '2025-07-06 09:40:24', '2025-07-09 18:09:24');
INSERT INTO `ec_courses` VALUES (22, '退课测试', 'http://192.168.78.91:10030/static/10/course/686f31b082f89d87f719938f.jfif', '退课测试', 1, 0, 10, '2025-07-10 11:21:32', '2025-07-10 11:21:32');
INSERT INTO `ec_courses` VALUES (23, '精品高数课程', 'http://192.168.78.91:10030/static/117/course/686fc8adb68cd07e7f2d7a0d.png', '精品的高数课程，一看就懂，一学就会！！', 1, 0, 117, '2025-07-10 22:05:43', '2025-07-10 22:05:43');
INSERT INTO `ec_courses` VALUES (24, '精品程序设计Python', 'http://192.168.78.91:10030/static/10/course/686fca8bb68cd07e7f2d7a0e.png', '精品的程序设计课程，半小时带你学会建网站', 1, 0, 10, '2025-07-10 22:13:35', '2025-07-11 02:19:31');

-- ----------------------------
-- Table structure for ec_join_class
-- ----------------------------
DROP TABLE IF EXISTS `ec_join_class`;
CREATE TABLE `ec_join_class`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `class_id` int NOT NULL COMMENT '课程id',
  `student_id` int NOT NULL COMMENT '学生id',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`, `class_id`, `student_id`) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  INDEX `class_id`(`class_id` ASC) USING BTREE,
  CONSTRAINT `ec_join_class_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `es_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ec_join_class_ibfk_2` FOREIGN KEY (`class_id`) REFERENCES `ec_classes` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 388 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ec_join_class
-- ----------------------------
INSERT INTO `ec_join_class` VALUES (72, 14, 12, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (73, 13, 13, '2022-12-03 20:32:33', '2023-02-02 11:10:55');
INSERT INTO `ec_join_class` VALUES (74, 13, 14, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (75, 14, 15, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (76, 15, 16, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (77, 13, 17, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (78, 14, 18, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (79, 15, 19, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (80, 13, 20, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (81, 14, 21, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (82, 15, 22, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (83, 13, 23, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (84, 14, 24, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (85, 15, 25, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (86, 13, 26, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (87, 14, 27, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (88, 15, 28, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (89, 13, 29, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (90, 14, 30, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (91, 15, 31, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (92, 13, 32, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (93, 14, 33, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (94, 15, 34, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (95, 13, 35, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (96, 14, 36, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (97, 15, 37, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (98, 13, 38, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (99, 14, 39, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (100, 15, 40, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (101, 13, 41, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (102, 14, 42, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (103, 15, 43, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (104, 13, 44, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (105, 14, 45, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (106, 15, 46, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (107, 13, 47, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (108, 14, 48, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (109, 15, 49, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (110, 13, 50, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (111, 14, 51, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (112, 15, 52, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (113, 13, 53, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (114, 14, 54, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (115, 15, 55, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (116, 13, 56, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (117, 14, 57, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (118, 15, 58, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (119, 13, 59, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (120, 14, 60, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (121, 15, 61, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (122, 13, 62, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (123, 14, 63, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (124, 15, 64, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (125, 13, 65, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (126, 14, 66, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (127, 15, 67, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (128, 13, 68, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (129, 14, 69, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (130, 15, 70, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (131, 13, 71, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (132, 14, 72, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (133, 15, 73, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (134, 13, 74, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (135, 14, 75, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (136, 15, 76, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (137, 13, 77, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (138, 14, 78, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (139, 15, 79, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (140, 13, 80, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (141, 14, 81, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (142, 15, 82, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (143, 13, 83, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (144, 14, 84, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (145, 15, 85, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (146, 13, 86, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (147, 14, 87, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (148, 15, 88, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (149, 13, 89, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (150, 14, 90, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (151, 15, 91, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (152, 13, 92, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (153, 14, 93, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (154, 15, 94, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (155, 13, 95, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (156, 14, 96, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (157, 15, 97, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (158, 13, 98, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (159, 14, 99, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (160, 15, 100, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (161, 13, 101, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (162, 14, 102, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (163, 15, 103, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (164, 13, 104, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (165, 14, 105, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (166, 15, 106, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (167, 13, 107, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (168, 14, 108, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (169, 15, 109, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (170, 13, 110, '2022-12-03 20:32:33', '2022-12-03 20:32:33');
INSERT INTO `ec_join_class` VALUES (171, 18, 14, '2023-02-02 17:06:45', '2023-02-02 17:06:45');
INSERT INTO `ec_join_class` VALUES (172, 18, 13, '2023-02-02 17:08:31', '2023-02-02 17:08:31');
INSERT INTO `ec_join_class` VALUES (173, 19, 14, '2023-02-06 12:44:01', '2023-02-06 12:44:01');
INSERT INTO `ec_join_class` VALUES (174, 20, 14, '2023-02-07 14:34:15', '2023-02-07 14:34:15');
INSERT INTO `ec_join_class` VALUES (175, 23, 14, '2023-02-08 14:01:29', '2023-02-08 14:01:29');
INSERT INTO `ec_join_class` VALUES (176, 24, 14, '2023-02-08 14:46:00', '2023-02-08 14:46:00');
INSERT INTO `ec_join_class` VALUES (177, 25, 14, '2023-02-08 14:56:34', '2023-02-08 14:56:34');
INSERT INTO `ec_join_class` VALUES (178, 13, 113, '2023-02-21 18:02:06', '2023-02-21 18:02:06');
INSERT INTO `ec_join_class` VALUES (299, 26, 11, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (300, 26, 12, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (301, 26, 13, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (302, 26, 14, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (303, 26, 15, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (304, 26, 16, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (305, 26, 17, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (306, 26, 18, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (307, 26, 19, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (308, 26, 20, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (309, 26, 21, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (310, 26, 22, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (311, 26, 23, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (312, 26, 24, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (313, 26, 25, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (314, 26, 26, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (315, 26, 27, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (316, 26, 28, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (317, 26, 29, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (318, 26, 30, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (319, 26, 31, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (320, 26, 32, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (321, 26, 33, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (322, 26, 34, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (323, 26, 35, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (324, 26, 36, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (325, 26, 37, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (326, 26, 38, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (327, 26, 39, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (328, 26, 40, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (329, 26, 41, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (330, 26, 42, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (331, 26, 43, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (332, 26, 44, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (333, 26, 45, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (334, 26, 46, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (335, 26, 47, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (336, 26, 48, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (337, 26, 49, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (338, 26, 50, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (339, 26, 51, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (340, 26, 52, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (341, 26, 53, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (342, 26, 54, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (343, 26, 55, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (344, 26, 56, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (345, 26, 57, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (346, 26, 58, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (347, 26, 59, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (348, 26, 60, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (349, 26, 61, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (350, 26, 62, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (351, 26, 63, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (352, 26, 64, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (353, 26, 65, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (354, 26, 66, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (355, 26, 67, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (356, 26, 68, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (357, 26, 69, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (358, 26, 70, '2023-02-22 16:46:22', '2023-02-22 16:46:22');
INSERT INTO `ec_join_class` VALUES (360, 12, 14, '2023-02-28 10:10:38', '2023-02-28 10:10:38');
INSERT INTO `ec_join_class` VALUES (362, 25, 13, '2025-07-05 16:38:32', '2025-07-05 16:38:32');
INSERT INTO `ec_join_class` VALUES (363, 25, 118, '2025-07-05 17:40:34', '2025-07-05 17:40:34');
INSERT INTO `ec_join_class` VALUES (364, 26, 118, '2025-07-06 09:16:28', '2025-07-06 09:16:28');
INSERT INTO `ec_join_class` VALUES (365, 27, 10, '2025-07-06 09:34:04', '2025-07-06 09:34:04');
INSERT INTO `ec_join_class` VALUES (368, 4, 118, '2025-07-07 10:21:30', '2025-07-07 10:21:30');
INSERT INTO `ec_join_class` VALUES (369, 28, 118, '2025-07-08 11:34:51', '2025-07-08 11:34:51');
INSERT INTO `ec_join_class` VALUES (374, 30, 13, '2025-07-10 11:22:28', '2025-07-10 11:22:28');
INSERT INTO `ec_join_class` VALUES (378, 31, 10, '2025-07-10 21:37:59', '2025-07-10 21:37:59');
INSERT INTO `ec_join_class` VALUES (379, 33, 119, '2025-07-10 22:09:34', '2025-07-10 22:09:34');
INSERT INTO `ec_join_class` VALUES (380, 33, 118, '2025-07-10 22:09:43', '2025-07-10 22:09:43');
INSERT INTO `ec_join_class` VALUES (381, 33, 10, '2025-07-10 22:11:37', '2025-07-10 22:11:37');
INSERT INTO `ec_join_class` VALUES (382, 33, 120, '2025-07-10 22:11:40', '2025-07-10 22:11:40');
INSERT INTO `ec_join_class` VALUES (383, 33, 121, '2025-07-10 22:13:48', '2025-07-10 22:13:48');
INSERT INTO `ec_join_class` VALUES (384, 34, 117, '2025-07-10 23:25:44', '2025-07-10 23:25:44');
INSERT INTO `ec_join_class` VALUES (386, 33, 13, '2025-07-11 09:36:23', '2025-07-11 09:36:23');
INSERT INTO `ec_join_class` VALUES (387, 33, 122, '2025-07-11 10:37:38', '2025-07-11 10:37:38');

-- ----------------------------
-- Table structure for es_login_manger
-- ----------------------------
DROP TABLE IF EXISTS `es_login_manger`;
CREATE TABLE `es_login_manger`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `login_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录类型',
  `login_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录名',
  `enable` timestamp NULL DEFAULT NULL COMMENT '是否启用',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '登录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of es_login_manger
-- ----------------------------
INSERT INTO `es_login_manger` VALUES (2, 116, 'gitee', '7350424', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for es_school
-- ----------------------------
DROP TABLE IF EXISTS `es_school`;
CREATE TABLE `es_school`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `logo` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '学校名称',
  `site` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '学校域名',
  `introduce` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '学校介绍',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci COMMENT = '学校信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of es_school
-- ----------------------------
INSERT INTO `es_school` VALUES (1, NULL, '安徽科技学院', '        \r\n        \r\nhttps://www.ahstu.edu.cn/', '安徽科技学院（Anhui Science And Technology University），主校区位于安徽省滁州市凤阳县，是经国家教育部批准成立的全日制普通本科高等学校，安徽省地方应用型高水平大学建设高校，服务国家特殊需求硕士专业学位人才培养项目试点单位，入选教育部“卓越工程师教育培养计划”、“卓越农林人才教育培养计划”、“新工科研究与实践项目”、国家级大学生创新创业训练计划、安徽省高等教育振兴计划。', '2022-12-03 18:41:04', '2022-12-03 18:41:04');

-- ----------------------------
-- Table structure for es_school_auth
-- ----------------------------
DROP TABLE IF EXISTS `es_school_auth`;
CREATE TABLE `es_school_auth`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '用户id',
  `student_id` int NOT NULL COMMENT '学生id',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci COMMENT = '学生认证表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of es_school_auth
-- ----------------------------
INSERT INTO `es_school_auth` VALUES (1, 10, 3, '2022-12-14 08:49:43', '2022-12-14 08:49:43');

-- ----------------------------
-- Table structure for es_school_department
-- ----------------------------
DROP TABLE IF EXISTS `es_school_department`;
CREATE TABLE `es_school_department`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `logo` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL COMMENT 'logo',
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '班级名称/部门',
  `parent_id` int NULL DEFAULT NULL COMMENT '上级部门',
  `leader_id` int NOT NULL COMMENT '辅导员/部门管理',
  `school_id` int NOT NULL COMMENT '学校id',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `leader_id`(`leader_id` ASC) USING BTREE,
  INDEX `school_id`(`school_id` ASC) USING BTREE,
  CONSTRAINT `es_school_department_ibfk_2` FOREIGN KEY (`leader_id`) REFERENCES `es_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `es_school_department_ibfk_3` FOREIGN KEY (`school_id`) REFERENCES `es_school` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci COMMENT = '学习部门信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of es_school_department
-- ----------------------------
INSERT INTO `es_school_department` VALUES (2, NULL, '计算机专升本', NULL, 10, 1, '2022-12-03 18:42:19', '2022-12-03 18:42:38');

-- ----------------------------
-- Table structure for es_school_student
-- ----------------------------
DROP TABLE IF EXISTS `es_school_student`;
CREATE TABLE `es_school_student`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `job_no` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '工号/学号',
  `real_name` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '姓名',
  `email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL,
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL,
  `department_id` int NOT NULL COMMENT '部门/班级',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `department_id`(`department_id` ASC) USING BTREE,
  CONSTRAINT `es_school_student_ibfk_1` FOREIGN KEY (`department_id`) REFERENCES `es_school_department` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci COMMENT = '学校用户认证信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of es_school_student
-- ----------------------------
INSERT INTO `es_school_student` VALUES (3, '270123456', '**家', NULL, NULL, 2, '2022-12-03 19:11:18', '2022-12-18 13:55:13');

-- ----------------------------
-- Table structure for es_user
-- ----------------------------
DROP TABLE IF EXISTS `es_user`;
CREATE TABLE `es_user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '用户名',
  `nickname` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '别名',
  `password` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '密码',
  `picture` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `bg_picture` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL COMMENT '背景图像',
  `autograph` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL COMMENT '签名',
  `phone` char(11) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `auth_id` int NULL DEFAULT NULL COMMENT '认证id',
  `role` char(1) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '0' COMMENT '0：学生 1：教师',
  `enable` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态，0：启用，>0:封禁',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`, `username`) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  INDEX `id`(`id` ASC) USING BTREE,
  INDEX `auth_id`(`auth_id` ASC) USING BTREE,
  CONSTRAINT `es_user_ibfk_1` FOREIGN KEY (`auth_id`) REFERENCES `es_school_auth` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 123 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci COMMENT = '用户信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of es_user
-- ----------------------------
INSERT INTO `es_user` VALUES (8, '马明111', '刘丽', '$2a$10$XQM1Qu/zNScKUBRmhJYvHO4hZztDiZbJhFG/FE0J5y4nO3iiBnhki', NULL, NULL, NULL, NULL, 'r.dfs@qq.com', NULL, '0', 1, '2022-10-08 18:40:43', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (9, '马明-马明', '刘丽1111', '$2a$10$Qnu0vwIelSnyo1P4L3RBnufZG9EWmcFo0bwHwA4O5NFuWHvl3sZXy', NULL, NULL, NULL, NULL, 'r.nmhyi@qq.com', NULL, '0', 1, '2022-10-09 15:38:04', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (10, 'baymax', 'baymax', '$2a$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://192.168.78.91:10030/static/10/avatar/686a0b27038b65892361164d.jpg', NULL, NULL, NULL, '123456@qq.sjj', 1, '0', 1, '2022-10-11 14:58:23', '2025-07-09 18:09:24');
INSERT INTO `es_user` VALUES (11, '薛军=薛军', '熊刚111', '$2a$10$lVUGnxb39BEOZHAffDJ90.5UITvj9tPme/XUxjorfiNoKHDGA1UTe', '/10/avatar/63e0592e0747b047cea866da.jpg', NULL, NULL, NULL, 'd.lsmgpa@qq.com', NULL, '0', 1, '2022-10-12 19:32:17', '2023-02-22 20:12:44');
INSERT INTO `es_user` VALUES (12, 'testbaymax', 'testbaymax', '$2a$10$4TZQDF.e0yozNroyOstsquKM0J56lH1rxYt7g.qkkW/3Vxiij3rcy', NULL, NULL, NULL, NULL, 'n.yxmj@qq.com', NULL, '0', 1, '2022-10-19 20:38:17', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (13, 'Steven', 'Michael Wilson', '$2a$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://192.168.78.91:10030/static/13/avatar/68706f2800d6441c5daa2305.jpg', NULL, NULL, NULL, 'h.rlnpwclmj@nampbc.pa', NULL, '0', 1, '2022-12-03 19:58:55', '2025-07-11 09:55:52');
INSERT INTO `es_user` VALUES (14, 'Scott', 'John Rodriguez', '$2a$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://localhost:10030//14/avatar/686910cd5e27d9a250d44bd5.jpg', NULL, NULL, NULL, 'u.jsktbv@sieokbirx.bd', NULL, '0', 1, '2022-12-03 19:58:55', '2025-07-05 19:47:25');
INSERT INTO `es_user` VALUES (15, 'Angela Perez', 'Christopher Anderson', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/b5f279&text=Carol Gonzalez', NULL, NULL, NULL, 'w.lcimmo@awfryfkcjb.az', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (16, 'Michael Gonzalez', 'Helen Gonzalez', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f279d9&text=Laura Perez', NULL, NULL, NULL, 'h.enmgwwsop@lgomkkxux.sg', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (17, 'Karen Thompson', 'Anna Jackson', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79f2e7&text=Maria Johnson', NULL, NULL, NULL, 'h.sudqfkk@iufxujjt.fk', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (18, 'John Thompson', 'William Martinez', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f2c479&text=Sarah Garcia', NULL, NULL, NULL, 'o.gidx@rrkqhg.gu', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (19, 'Michelle Garcia', 'Elizabeth Martinez', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/a179f2&text=Timothy Jones', NULL, NULL, NULL, 'x.uafc@fhexi.th', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (20, 'Mark Perez', 'Gary Rodriguez', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79f27d&text=Joseph Jones', NULL, NULL, NULL, 'k.xldlsgsv@ymy.al', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (21, 'Gary Davis', 'Anthony Garcia', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f27997&text=Mary Hernandez', NULL, NULL, NULL, 'q.pxda@wyjtv.mo', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (22, 'Susan Lee', 'Michael Smith', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79bbf2&text=Shirley Anderson', NULL, NULL, NULL, 'r.fntgui@qqtoe.se', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (23, 'Gary Brown', 'Laura White', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/def279&text=Elizabeth Jackson', NULL, NULL, NULL, 'e.fhk@mbunjqdqt.sn', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (24, 'Sandra Martin', 'Edward Williams', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/e279f2&text=Patricia Walker', NULL, NULL, NULL, 'i.vrmqgiee@nlkskbgrm.it', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (25, 'Charles Jones', 'James Davis', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79f2bf&text=Mary Johnson', NULL, NULL, NULL, 'j.qpbht@xwsr.gov.cn', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (26, 'Margaret Williams', 'Timothy White', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f29c79&text=Carol Allen', NULL, NULL, NULL, 'o.bppbucn@djyogjhmt.中国互联.网络', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (27, 'Sarah Rodriguez', 'Elizabeth Williams', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/7979f2&text=Shirley Thomas', NULL, NULL, NULL, 't.jven@uiiehhgvq.nu', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (28, 'Joseph Harris', 'Joseph Martinez', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/9cf279&text=James Harris', NULL, NULL, NULL, 'g.njnqnfhue@tpm.pe', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (29, 'John Thomas', 'Jennifer Garcia', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f279c0&text=Susan Harris', NULL, NULL, NULL, 'v.nyqq@fubqdx.zm', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (30, 'Jeffrey White', 'Linda Martin', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79e3f2&text=Lisa Miller', NULL, NULL, NULL, 'p.sqb@xcibntvhq.bb', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (31, 'Matthew Thomas', 'Mark Thomas', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f2dd79&text=Shirley Jackson', NULL, NULL, NULL, 's.ekwguj@ebypbgiqok.qa', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (32, 'Michael Moore', 'Brenda Lewis', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/ba79f2&text=Deborah Walker', NULL, NULL, NULL, 'c.nnxyn@sicsk.kn', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (33, 'Charles Young', 'Angela Young', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79f296&text=Helen Moore', NULL, NULL, NULL, 'j.qqkrffz@rieco.do', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (34, 'Karen Davis', 'Deborah Jackson', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f2797e&text=Jessica Gonzalez', NULL, NULL, NULL, 'n.epjltteor@yqnzg.id', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (35, 'Gary Thomas', 'Dorothy Moore', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79a2f2&text=Mary Thompson', NULL, NULL, NULL, 'b.jryty@gkljwyk.dj', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (36, 'Susan Hall', 'Donna Lewis', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/c5f279&text=Donald Perez', NULL, NULL, NULL, 'o.tlq@gxkrwn.hn', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (37, 'Scott Wilson', 'Gary Thomas', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f279e8&text=Shirley Moore', NULL, NULL, NULL, 'o.civlrtyrr@samdfnd.ec', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (38, 'Frank Garcia', 'Jeffrey Thomas', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79f2d8&text=Mary Jackson', NULL, NULL, NULL, 'o.ajuttde@cttml.tv', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (39, 'Brian Robinson', 'Melissa Moore', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f2b579&text=Charles Lee', NULL, NULL, NULL, 'l.gcguunmzlt@skgrkymnq.hm', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (40, 'Gary Clark', 'Steven Garcia', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/9179f2&text=Kevin Hernandez', NULL, NULL, NULL, 'c.nqpbvfh@uqhrols.ye', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (41, 'Ruth Wilson', 'Brian Thomas', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/83f279&text=Dorothy Lopez', NULL, NULL, NULL, 'p.kzxhmujxc@bbtecksj.bm', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (42, 'Donald White', 'Robert Hall', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f279a7&text=Dorothy Taylor', NULL, NULL, NULL, 'r.rmx@ieipjkld.bi', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (43, 'Dorothy Williams', 'Edward Hall', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79caf2&text=Betty Brown', NULL, NULL, NULL, 'w.knwsmr@leyg.ba', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (44, 'Jose Harris', 'Lisa Davis', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/edf279&text=Donald Williams', NULL, NULL, NULL, 'f.oykjht@jmeecnlkx.ga', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (45, 'Christopher Taylor', 'John Jackson', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/d379f2&text=Barbara Lewis', NULL, NULL, NULL, 'k.qxrlulyi@lmkdk.cg', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (46, 'Nancy Rodriguez', 'Patricia Perez', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79f2af&text=Michael Lewis', NULL, NULL, NULL, 'y.eqvlcpc@uwv.et', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (47, 'Sharon Rodriguez', 'Carol Clark', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f28c79&text=Kevin Hall', NULL, NULL, NULL, 'l.seujkmkni@xnhajcte.ae', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (48, 'Shirley Lee', 'Frank Clark', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/7989f2&text=Thomas Gonzalez', NULL, NULL, NULL, 'p.jghlutge@ktyqf.tt', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (49, 'Elizabeth Anderson', 'Steven Walker', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/acf279&text=Michael Robinson', NULL, NULL, NULL, 'r.fpwieulqd@qtbje.pk', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (50, 'Shirley Young', 'Matthew Young', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f279cf&text=Michelle Allen', NULL, NULL, NULL, 'z.rtiwharo@izfp.cn', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (51, 'Linda Brown', 'Margaret Garcia', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79f2f1&text=Sharon Moore', NULL, NULL, NULL, 'g.wzjqsowd@bfeblbkpa.al', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (52, 'Kimberly Hall', 'Brian Lewis', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f2ce79&text=Barbara Hall', NULL, NULL, NULL, 'p.qozwyqxmwg@auqcfeku.rw', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (53, 'Helen Thomas', 'Richard Jones', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/aa79f2&text=Thomas Anderson', NULL, NULL, NULL, 'd.dtzlo@rgumuwcg.sd', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (54, 'Karen Williams', 'Michael Lewis', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79f287&text=Jeffrey Johnson', NULL, NULL, NULL, 'k.cbzx@djjpjt.cv', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (55, 'Eric Thompson', 'Helen Jones', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f2798e&text=Christopher Johnson', NULL, NULL, NULL, 'w.fbodl@vapcyi.ki', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (56, 'Carol Walker', 'Elizabeth Taylor', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79b1f2&text=Kimberly Jones', NULL, NULL, NULL, 'q.rbwjouxkq@pgerbtqwh.rw', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (57, 'Karen Moore', 'Edward Robinson', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/d4f279&text=Linda Perez', NULL, NULL, NULL, 'k.lowohfv@tjyk.ng', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (58, 'Linda White', 'Barbara Rodriguez', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/ec79f2&text=Nancy Clark', NULL, NULL, NULL, 'u.lxlmuf@frcepyk.fk', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (59, 'Donna Garcia', 'Donald Lee', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79f2c8&text=Daniel Rodriguez', NULL, NULL, NULL, 'c.iarbtmt@qroigvl.mp', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (60, 'Larry White', 'Michael Anderson', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f2a579&text=Susan Lewis', NULL, NULL, NULL, 'l.centb@ogvhtt.vc', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (61, 'Jose Anderson', 'Edward Anderson', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/8279f2&text=Elizabeth Walker', NULL, NULL, NULL, 'c.qqo@dtkn.uk', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (62, 'Jason Miller', 'Donna Williams', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/93f279&text=Maria Harris', NULL, NULL, NULL, 'e.jncyjnb@jgo.gd', NULL, '0', 1, '2022-12-03 19:58:55', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (63, 'Ruth Perez', 'Gary Clark', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f279b6&text=Richard Lee', NULL, NULL, NULL, 'c.lpus@vwsxihmmy.pt', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (64, 'Anthony Robinson', 'Donald Perez', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79daf2&text=Carol Walker', NULL, NULL, NULL, 't.uuffvoxy@mqateqa.dk', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (65, 'Charles Johnson', 'Nancy Lee', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f2e779&text=Deborah Hall', NULL, NULL, NULL, 'y.xkwynt@vfxqv.sb', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (66, 'Kevin Martin', 'Donald Wilson', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/c379f2&text=Carol Walker', NULL, NULL, NULL, 't.ryhvl@vgyqurarek.ge', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (67, 'John Wilson', 'Lisa Jackson', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79f2a0&text=Robert Walker', NULL, NULL, NULL, 'j.iqadotdkl@zijs.re', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (68, 'Deborah Anderson', 'Helen Garcia', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f27d79&text=Richard Clark', NULL, NULL, NULL, 'r.syfqgih@yiih.br', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (69, 'Anna Rodriguez', 'Anthony Lewis', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/7998f2&text=Laura Garcia', NULL, NULL, NULL, 't.bysf@prjviqvet.ve', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (70, 'James Robinson', 'Robert Johnson', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/bbf279&text=Brenda Hernandez', NULL, NULL, NULL, 'u.igqy@dqdge.ky', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (71, 'Eric Moore', 'George Thompson', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f279df&text=Kimberly Brown', NULL, NULL, NULL, 'k.lxwxfv@enjgbh.中国互联.公司', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (72, 'Margaret Davis', 'Steven Garcia', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79f2e1&text=Jessica Jackson', NULL, NULL, NULL, 'r.nvslye@hydm.pf', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (73, 'David Johnson', 'Gary White', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f2be79&text=David Lewis', NULL, NULL, NULL, 'x.lossqqqz@avilsh.kz', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (74, 'Michael Davis', 'Dorothy Jones', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/9b79f2&text=Kevin Moore', NULL, NULL, NULL, 'q.hhgiwfsw@aewwps.hk', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (75, 'Michael Anderson', 'Kenneth Perez', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/7af279&text=Ruth Davis', NULL, NULL, NULL, 'u.xlcuqejj@lncaju.sn', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (76, 'David Anderson', 'Gary Smith', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f2799d&text=Larry Moore', NULL, NULL, NULL, 'b.jfnkshq@bwjaz.hn', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (77, 'Kimberly Hall', 'John Jackson', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79c0f2&text=Brian Robinson', NULL, NULL, NULL, 'r.kedybeo@wdzmjdiyr.mv', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (78, 'Ronald Garcia', 'Brenda Taylor', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/e4f279&text=Edward Young', NULL, NULL, NULL, 'n.uleatase@dnipql.fr', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (79, 'Michael Davis', 'George Thomas', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/dc79f2&text=William Wilson', NULL, NULL, NULL, 'c.brpsj@qdlbmdh.ma', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (80, 'Edward Moore', 'Jose Allen', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79f2b9&text=Brenda Martin', NULL, NULL, NULL, 'j.ddympxlj@ftfrjsp.gp', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (81, 'Patricia Garcia', 'Timothy Smith', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f29679&text=Shirley Williams', NULL, NULL, NULL, 'y.mxjokewl@umnogquly.az', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (82, 'Jennifer White', 'Scott Harris', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/797ff2&text=Gary Anderson', NULL, NULL, NULL, 'x.fvz@khab.cc', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (83, 'Gary Gonzalez', 'Sandra Thompson', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/a2f279&text=Jessica Hernandez', NULL, NULL, NULL, 'e.ikxn@yfmjnw.af', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (84, 'Kenneth Davis', 'Michelle Smith', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f279c6&text=Michelle Wilson', NULL, NULL, NULL, 'g.wrvezncd@xhmswiwu.gb', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (85, 'Margaret Thomas', 'Christopher Hernandez', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79e9f2&text=Barbara Thomas', NULL, NULL, NULL, 'q.nqgo@ztdh.gh', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (86, 'Gary Harris', 'Anthony Thomas', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f2d779&text=Scott Garcia', NULL, NULL, NULL, 'y.obouiis@sfiwgrn.中国互联.公司', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (87, 'Laura Jackson', 'Sandra Hernandez', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/b479f2&text=Donna Thompson', NULL, NULL, NULL, 'b.qewijlg@mreavlnhe.com', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (88, 'Betty Smith', 'Jessica Clark', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79f290&text=Daniel Miller', NULL, NULL, NULL, 'q.mfjdedjqt@epflq.lb', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (89, 'Sharon Allen', 'Mark Walker', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f27984&text=Melissa Martin', NULL, NULL, NULL, 'x.eevfqmk@olwslf.my', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (90, 'Sharon Rodriguez', 'George Martin', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79a7f2&text=Steven Thompson', NULL, NULL, NULL, 'u.quyrdkqx@tumlkmbj.ge', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (91, 'Jessica Perez', 'Edward Lopez', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/cbf279&text=Steven Moore', NULL, NULL, NULL, 'v.muwrud@xmrvlwjwf.sy', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (92, 'Timothy Thompson', 'Carol Hernandez', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f279ee&text=Betty Brown', NULL, NULL, NULL, 'u.vfgpr@bkoet.coop', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (93, 'Edward Smith', 'Christopher Williams', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79f2d2&text=Shirley Jones', NULL, NULL, NULL, 'j.ebk@telocemv.vu', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (94, 'Angela White', 'Nancy Taylor', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f2af79&text=Jessica Hall', NULL, NULL, NULL, 'q.bcfqn@lhcodl.ie', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (95, 'William Young', 'David Garcia', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/8b79f2&text=Lisa Jones', NULL, NULL, NULL, 'p.njsfs@jhxnwanog.org', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (96, 'Christopher Hernandez', 'Michelle Thompson', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/89f279&text=Sandra White', NULL, NULL, NULL, 'c.erpikvee@hgniwcrk.ke', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (97, 'Sandra Johnson', 'Gary Jones', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f279ad&text=Frank Moore', NULL, NULL, NULL, 's.jvpndv@ctdxmf.ca', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (98, 'George Martin', 'Eric Jackson', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79d0f2&text=Shirley Jones', NULL, NULL, NULL, 'f.kjbjstbtk@fmwyjrtzx.gu', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (99, 'Ronald Walker', 'Jose Allen', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f2f079&text=Melissa Thomas', NULL, NULL, NULL, 'e.upxgfudqwn@scmknivb.ba', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (100, 'Laura Thompson', 'Linda Jones', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/cd79f2&text=Scott Thompson', NULL, NULL, NULL, 'u.zxtxpmuq@trnflagbd.museum', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (101, 'Donald Johnson', 'Jennifer Johnson', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79f2aa&text=Susan Miller', NULL, NULL, NULL, 'y.scgoexgd@vdjsbprx.lc', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (102, 'Cynthia Young', 'Gary Smith', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f28679&text=Richard Perez', NULL, NULL, NULL, 'b.mwbyefxoer@ocjqjmglgx.rw', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (103, 'Melissa Brown', 'Karen Young', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/798ef2&text=Deborah Rodriguez', NULL, NULL, NULL, 'q.mwkbks@wmjhte.hm', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (104, 'Larry Robinson', 'Susan Hall', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/b2f279&text=Sarah Perez', NULL, NULL, NULL, 's.tygsekuof@ywuokyt.tg', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (105, 'Anthony Davis', 'Sandra Allen', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f279d5&text=Kenneth Wilson', NULL, NULL, NULL, 'e.kqhhu@cfebkpqurp.ve', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (106, 'Elizabeth Jackson', 'David Martin', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79f2eb&text=Dorothy Martin', NULL, NULL, NULL, 'l.nikttj@wrojltz.mv', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (107, 'Joseph Martinez', 'Jason Smith', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f2c879&text=Paul Brown', NULL, NULL, NULL, 'z.xvhz@sjgqwmzsn.ph', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (108, 'Karen Harris', 'Angela Young', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/a479f2&text=Michael Perez', NULL, NULL, NULL, 'd.xewinfs@iwooylld.bo', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (109, 'Sharon Young', 'Eric Williams', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79f281&text=Gary Miller', NULL, NULL, NULL, 't.hqsd@sqxbdbsyep.nf', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (110, 'George Williams', 'Sarah Robinson', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/f27994&text=Jason Allen', NULL, NULL, NULL, 'u.iyyyw@qikrc.ao', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (111, 'Michael Miller', 'Cynthia Wilson', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/79b7f2&text=Joseph Clark', NULL, NULL, NULL, 'u.pxi@mdwidcsor.cd', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (112, 'Karen Garcia', 'Elizabeth Young', '$10$EgY/iP1EGtX80SJpM9qUYuX7IAb1UOwIH596o.FZaOQ5wF5vDRPyK', 'http://dummyimage.com/200x200/daf279&text=Joseph Taylor', NULL, NULL, NULL, 'w.fbtrnkus@xvi.ms', NULL, '0', 1, '2022-12-03 19:59:11', '2022-12-07 17:02:00');
INSERT INTO `es_user` VALUES (113, 'baymax1', '你好中国啊', '$2a$10$mi1/TU3uImlYlqt5AkUmues8ggytTYyoDmGva11oA6xDgbaRa7O/.', '/113/avatar/63f49e0097005d24651af658.jpeg', NULL, NULL, NULL, '1936955617@qq.com', NULL, '0', 1, '2023-02-06 19:18:30', '2023-02-21 18:33:36');
INSERT INTO `es_user` VALUES (117, 'yb123456', 'yb123456', '$2a$10$G5i44drRtbgLJckOZwDrHeD1xM4RoemT2Z9WuLjDzVZ9iR4F09pV2', 'http://192.168.78.91:10030/static/117/avatar/686fc84cb68cd07e7f2d7a0b.jpg', NULL, NULL, NULL, 'yb3179174149@163.com', NULL, '0', 1, '2025-07-03 11:52:13', '2025-07-10 22:03:56');
INSERT INTO `es_user` VALUES (118, 'lty666', '兰学生测试', '$2a$10$2BlNVzRsZIIPdAvHPn.iqu36PM2o.HepSjP2bWJJdIlo6TruM3ZV2', 'http://192.168.78.91:10030/static/118/avatar/686a2a519c18ae12f42c3903.jfif', NULL, NULL, NULL, '1520163083@qq.com', NULL, '0', 1, '2025-07-05 17:37:40', '2025-07-09 18:09:24');
INSERT INTO `es_user` VALUES (119, 'wanan', '睡个好觉晚安', '$2a$10$XafZLyiiclFsDpyOx.BuR.B.slq9VLbNOh0X3OSQznej3ZGLycN4S', 'http://192.168.78.91:10030/static/119/avatar/686fcfedb68cd07e7f2d7a10.png', NULL, NULL, NULL, '2039452789@qq.com', NULL, '0', 1, '2025-07-10 22:07:01', '2025-07-10 22:36:29');
INSERT INTO `es_user` VALUES (120, 'xzc007312', 'fkbattel', '$2a$10$LnYLNnglG1eu6k6birQdwumIIsFFTzLV5bKpgZjU1JlNFEtbG6IH6', 'http://192.168.78.91:10030/static/120/avatar/686fcaf3b68cd07e7f2d7a0f.png', NULL, NULL, NULL, '1955161832@qq.com', NULL, '0', 1, '2025-07-10 22:11:16', '2025-07-10 22:15:15');
INSERT INTO `es_user` VALUES (121, 'PengYinyao', 'PengYinyao', '$2a$10$ePkhx9cFz1Uvodvn6phFGuDYKPr1cLtVy74JAHeUKMAYpixfrB83.', 'http://192.168.78.91:10030/static/121/avatar/68706f4600d6441c5daa2306.png', NULL, NULL, NULL, '3096143705@qq.com', NULL, '0', 1, '2025-07-10 22:13:24', '2025-07-11 09:56:22');
INSERT INTO `es_user` VALUES (122, 'test123', 'test123', '$2a$10$PkaS8BStlK20WjIVwW.2U.vefG0cFjZIvNtXmwVAjUCJrbldI1Wm2', NULL, NULL, NULL, NULL, 'weizhihang2026@163.com', NULL, '0', 1, '2025-07-11 10:37:22', '2025-07-11 10:37:22');

-- ----------------------------
-- View structure for es_user_auth_info
-- ----------------------------
DROP VIEW IF EXISTS `es_user_auth_info`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `es_user_auth_info` AS select `es_user`.`id` AS `user_id`,`es_user`.`username` AS `username`,`es_user`.`nickname` AS `nickname`,`es_user`.`picture` AS `picture`,`es_school_student`.`job_no` AS `job_no`,`es_school_student`.`real_name` AS `real_name`,`es_school_student`.`email` AS `email`,`es_school_student`.`id` AS `student_id`,`es_school_student`.`phone` AS `phone`,`es_school_student`.`department_id` AS `department_id`,`es_school_department`.`logo` AS `department_logo`,`es_school_department`.`name` AS `department_name`,`es_school_department`.`parent_id` AS `parent_id`,`es_school_department`.`leader_id` AS `leader_id`,`es_school_department`.`school_id` AS `school_id`,`es_school`.`logo` AS `school_logo`,`es_school`.`name` AS `school_name`,`es_school`.`site` AS `school_site`,`es_school_auth`.`created_at` AS `auth_time` from ((((`es_user` left join `es_school_auth` on((`es_user`.`auth_id` = `es_school_auth`.`id`))) left join `es_school_student` on((`es_school_auth`.`student_id` = `es_school_student`.`id`))) left join `es_school_department` on((`es_school_student`.`department_id` = `es_school_department`.`id`))) left join `es_school` on((`es_school_department`.`school_id` = `es_school`.`id`)));

SET FOREIGN_KEY_CHECKS = 1;
