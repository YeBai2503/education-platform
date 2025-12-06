/*
 Navicat Premium Dump SQL

 Source Server         : database
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : wk_note

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 11/07/2025 11:32:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for en_note
-- ----------------------------
DROP TABLE IF EXISTS `en_note`;
CREATE TABLE `en_note`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '笔记ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `course_id` int NULL DEFAULT NULL COMMENT '课程ID',
  `context` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '笔记内容',
  `created_at` timestamp NOT NULL COMMENT '创建时间',
  `updated_at` timestamp NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 57 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of en_note
-- ----------------------------
INSERT INTO `en_note` VALUES (18, 10, 101, '这是我的第一条课程笔记', '2025-07-04 18:34:03', '2025-07-04 18:34:03');
INSERT INTO `en_note` VALUES (19, 10, 101, '这是我的第一条课程笔记', '2025-07-04 18:34:39', '2025-07-04 18:34:39');
INSERT INTO `en_note` VALUES (20, 10, 101, '这是我的第一条课程笔记', '2025-07-04 18:43:19', '2025-07-04 18:43:19');
INSERT INTO `en_note` VALUES (21, 10, 101, '这是我的第一条课程笔记', '2025-07-04 18:43:24', '2025-07-04 18:43:24');
INSERT INTO `en_note` VALUES (22, 1, 101, '这是我的第一条课程笔记', '2025-07-04 18:43:32', '2025-07-04 18:43:32');
INSERT INTO `en_note` VALUES (23, 10, 101, '这是我的第一条课程笔记', '2025-07-04 18:50:06', '2025-07-04 18:50:06');
INSERT INTO `en_note` VALUES (24, 10, 101, 'aa这是更新后的笔记内容', '2025-07-04 20:33:13', '2025-07-05 10:06:12');
INSERT INTO `en_note` VALUES (25, 10, 101, '这是我的第一条课程笔记', '2025-07-04 20:33:18', '2025-07-04 20:33:18');
INSERT INTO `en_note` VALUES (26, 10, 101, '这是我的第2条课程笔记', '2025-07-04 20:36:08', '2025-07-04 20:36:08');
INSERT INTO `en_note` VALUES (27, 10, 101, 'ffff这是我的第2条课程笔记', '2025-07-04 20:36:25', '2025-07-04 20:36:25');
INSERT INTO `en_note` VALUES (28, 10, 101, '3333ffff这是我的第2条课程笔记', '2025-07-04 20:43:22', '2025-07-04 20:43:22');
INSERT INTO `en_note` VALUES (29, 10, 101, '3333ffff这是我的第2条课程笔记', '2025-07-04 20:43:31', '2025-07-04 20:43:31');
INSERT INTO `en_note` VALUES (30, 10, 101, '', '2025-07-04 20:50:16', '2025-07-04 20:50:16');
INSERT INTO `en_note` VALUES (31, 10, 101, '3333ffff这是我的第2条课程笔记', '2025-07-04 20:53:02', '2025-07-04 20:53:02');
INSERT INTO `en_note` VALUES (32, 10, 101, '3333ffff这是我的第2条课程笔记', '2025-07-04 20:58:31', '2025-07-04 20:58:31');
INSERT INTO `en_note` VALUES (34, 10, 21, '3333ffff这是我的第2条课程笔记', '2025-07-04 21:17:09', '2025-07-04 21:17:09');
INSERT INTO `en_note` VALUES (35, 10, NULL, '3333ffff这是我的第2条课程笔记', '2025-07-05 09:36:34', '2025-07-05 09:36:34');
INSERT INTO `en_note` VALUES (36, 10, 21, '3333ffff这是我的第2条课程笔记', '2025-07-05 09:47:21', '2025-07-05 09:47:21');
INSERT INTO `en_note` VALUES (37, 10, 21, '3333ffff这是我的第2条课程笔记', '2025-07-05 09:47:50', '2025-07-05 09:47:50');
INSERT INTO `en_note` VALUES (38, 10, 21, '3333ffff这是我的第2条课程笔记', '2025-07-05 10:04:46', '2025-07-05 10:04:46');
INSERT INTO `en_note` VALUES (39, 10, 21, '3333ffff这是我的第2条课程笔记', '2025-07-05 10:04:49', '2025-07-05 10:04:49');
INSERT INTO `en_note` VALUES (40, 10, 21, '3333ffff这是我的第2条课程笔记', '2025-07-05 10:06:08', '2025-07-05 10:06:08');
INSERT INTO `en_note` VALUES (41, 10, 21, '3333ffff这是我的第2条课程笔记', '2025-07-05 10:40:21', '2025-07-05 10:40:21');
INSERT INTO `en_note` VALUES (43, 10, 20, '**更新了第三条笔记内容**\nNoteDetail.vue?t=1751770223500:140 \n \n```\n#include<iostream>\n```', '2025-07-05 15:55:03', '2025-07-10 21:13:17');
INSERT INTO `en_note` VALUES (44, 10, 11, '3333ffff这是我的第2条课程笔记', '2025-07-05 16:19:19', '2025-07-05 16:19:19');
INSERT INTO `en_note` VALUES (45, 10, 11, '新建了一条c++课程笔记', '2025-07-05 17:18:08', '2025-07-05 17:18:08');
INSERT INTO `en_note` VALUES (46, 10, 20, '3333ffff这是我的第2条课程笔记', '2025-07-06 09:39:25', '2025-07-06 09:39:25');
INSERT INTO `en_note` VALUES (47, 10, 20, '这是我的燕云笔记', '2025-07-06 09:39:38', '2025-07-06 09:39:38');
INSERT INTO `en_note` VALUES (48, 10, 21, '这是我的英语笔记', '2025-07-06 09:41:29', '2025-07-06 09:41:29');
INSERT INTO `en_note` VALUES (49, 10, 21, '**NewNote**', '2025-07-06 11:32:33', '2025-07-06 11:32:33');
INSERT INTO `en_note` VALUES (50, 10, 1, '**结课笔记**', '2025-07-06 11:41:28', '2025-07-06 11:41:28');
INSERT INTO `en_note` VALUES (51, 10, 1, 'gvdsfb', '2025-07-06 15:57:54', '2025-07-06 15:57:54');
INSERT INTO `en_note` VALUES (52, 14, 6, 'sdfsdf\n```\n#include<iostream>\n\n```', '2025-07-08 16:27:26', '2025-07-08 16:27:26');
INSERT INTO `en_note` VALUES (53, 10, 20, '```\nvvcgghgvh\n```', '2025-07-10 20:29:33', '2025-07-10 20:29:33');
INSERT INTO `en_note` VALUES (54, 121, 23, '**高数第一章**\n\n```c++\n    cout<<\"HeloWorld!\";\n\n```\n', '2025-07-10 22:14:53', '2025-07-11 00:59:50');
INSERT INTO `en_note` VALUES (55, 121, 23, '第二条笔记', '2025-07-11 01:00:32', '2025-07-11 01:00:32');
INSERT INTO `en_note` VALUES (56, 122, 23, '```\n#include<iostream>\n```', '2025-07-11 10:59:31', '2025-07-11 10:59:31');

SET FOREIGN_KEY_CHECKS = 1;
