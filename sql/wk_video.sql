/*
 Navicat Premium Dump SQL

 Source Server         : MySql
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : wk_video

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 11/07/2025 11:33:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ev_chapter
-- ----------------------------
DROP TABLE IF EXISTS `ev_chapter`;
CREATE TABLE `ev_chapter`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `course_id` int NOT NULL COMMENT '课程ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '章节题目',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '章节描述',
  `created_at` timestamp NOT NULL COMMENT '创建时间',
  `updated_at` timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ev_chapter
-- ----------------------------
INSERT INTO `ev_chapter` VALUES (1, 21, '第1章 英语入门', '入门入门入门', '2025-07-07 00:44:12', '2025-07-07 00:44:12');
INSERT INTO `ev_chapter` VALUES (2, 21, '第2章 英语进阶', '进阶进阶进阶', '2025-07-07 00:44:37', '2025-07-07 00:44:37');
INSERT INTO `ev_chapter` VALUES (3, 21, '第3章 英语精通', '大师大师精通精通精通', '2025-07-07 00:45:02', '2025-07-07 14:53:48');
INSERT INTO `ev_chapter` VALUES (9, 2, '自动化第一章', '第一章', '2025-07-08 10:49:01', '2025-07-08 10:49:01');
INSERT INTO `ev_chapter` VALUES (10, 2, '自动化第二章', '第二章', '2025-07-08 12:03:08', '2025-07-08 12:03:08');
INSERT INTO `ev_chapter` VALUES (15, 2, '第三章', '', '2025-07-08 15:08:59', '2025-07-08 15:08:59');
INSERT INTO `ev_chapter` VALUES (16, 2, '第四章', '', '2025-07-08 15:30:04', '2025-07-08 15:30:04');
INSERT INTO `ev_chapter` VALUES (17, 2, '第五章', '', '2025-07-08 15:30:09', '2025-07-08 15:30:09');
INSERT INTO `ev_chapter` VALUES (18, 23, '第一章 高数入门', '高数的入门章节', '2025-07-10 22:06:53', '2025-07-10 22:06:53');
INSERT INTO `ev_chapter` VALUES (19, 23, '第二章 高数进阶', '高数第二章', '2025-07-10 22:10:13', '2025-07-11 00:54:49');
INSERT INTO `ev_chapter` VALUES (20, 24, '第一章 程序入门', '入门学习', '2025-07-10 22:16:04', '2025-07-10 22:16:04');
INSERT INTO `ev_chapter` VALUES (21, 23, '第三章 高数大师', '成为大师', '2025-07-11 00:54:43', '2025-07-11 00:54:43');
INSERT INTO `ev_chapter` VALUES (22, 24, '第二章 程序进阶', '进阶学习', '2025-07-11 09:45:59', '2025-07-11 09:45:59');

-- ----------------------------
-- Table structure for ev_progress
-- ----------------------------
DROP TABLE IF EXISTS `ev_progress`;
CREATE TABLE `ev_progress`  (
  `student_id` int NOT NULL COMMENT '学生ID',
  `section_id` int NOT NULL COMMENT '小节ID',
  `progress` int NULL DEFAULT NULL COMMENT '百分制（100就是看完了）',
  `created_at` timestamp NOT NULL COMMENT '创建时间',
  `updated_at` timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`student_id`, `section_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ev_progress
-- ----------------------------
INSERT INTO `ev_progress` VALUES (10, 3, 0, '2025-07-07 14:40:20', '2025-07-10 11:24:12');
INSERT INTO `ev_progress` VALUES (10, 4, 100, '2025-07-08 09:55:27', '2025-07-08 10:38:21');
INSERT INTO `ev_progress` VALUES (10, 5, 100, '2025-07-08 09:51:23', '2025-07-08 10:42:51');
INSERT INTO `ev_progress` VALUES (10, 6, 100, '2025-07-08 10:26:13', '2025-07-08 10:43:05');
INSERT INTO `ev_progress` VALUES (10, 9, 0, '2025-07-08 11:57:16', '2025-07-08 16:30:46');
INSERT INTO `ev_progress` VALUES (10, 10, 25, '2025-07-08 11:02:12', '2025-07-10 17:24:44');
INSERT INTO `ev_progress` VALUES (10, 14, 0, '2025-07-10 22:17:01', '2025-07-11 09:46:35');
INSERT INTO `ev_progress` VALUES (13, 3, 7, '2025-07-10 14:36:03', '2025-07-10 14:36:33');
INSERT INTO `ev_progress` VALUES (117, 11, 23, '2025-07-10 22:34:59', '2025-07-11 10:50:04');
INSERT INTO `ev_progress` VALUES (117, 13, 100, '2025-07-10 22:58:43', '2025-07-10 23:00:41');
INSERT INTO `ev_progress` VALUES (117, 14, 0, '2025-07-10 23:25:51', '2025-07-10 23:25:59');
INSERT INTO `ev_progress` VALUES (118, 9, 100, '2025-07-09 22:36:05', '2025-07-10 10:14:52');
INSERT INTO `ev_progress` VALUES (118, 10, 8, '2025-07-09 22:36:21', '2025-07-10 17:17:09');
INSERT INTO `ev_progress` VALUES (119, 11, 0, '2025-07-11 01:08:02', '2025-07-11 01:08:02');
INSERT INTO `ev_progress` VALUES (119, 12, 0, '2025-07-10 22:30:26', '2025-07-10 22:30:26');
INSERT INTO `ev_progress` VALUES (120, 11, 47, '2025-07-10 22:35:12', '2025-07-10 22:39:07');
INSERT INTO `ev_progress` VALUES (122, 11, 0, '2025-07-11 10:37:50', '2025-07-11 10:37:50');

-- ----------------------------
-- Table structure for ev_section
-- ----------------------------
DROP TABLE IF EXISTS `ev_section`;
CREATE TABLE `ev_section`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '小节ID',
  `chapter_id` int NOT NULL COMMENT '章节ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '小节标题',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '小节描述',
  `rank` int NULL DEFAULT NULL COMMENT '在当前章节的所有小节中的排序号',
  `video_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '视频URL',
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面URL',
  `duration` int NULL DEFAULT NULL COMMENT '视频时长',
  `created_at` timestamp NOT NULL COMMENT '创建时间',
  `updated_at` timestamp NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ev_section
-- ----------------------------
INSERT INTO `ev_section` VALUES (3, 1, '1. 1English1.1', '1.1 小节的描述', 1, 'http://192.168.78.91:10030/static/videos/21/1/2c9c4929-b030-440d-8d24-8ac85e3bf9b7/59dc1182-9cc4-4b2f-b1df-8b8bcd77539f.mp4', 'http://192.168.73.91:10030/static/covers/10/f313833e-0854-4bbd-964c-e65014a4b8cd.png', 234, '2025-07-07 11:58:38', '2025-07-07 11:58:38');
INSERT INTO `ev_section` VALUES (4, 1, '1.2 English1.2', '1.2 小节的描述', 2, 'http://192.168.78.91:10030/static/videos/21/1/655b5b2b-74a5-4718-b440-5fbcc16e170b/93084b7d-09f6-4d1d-900f-560700b0d5f6.mp4', 'http://192.168.73.91:10030/static/covers/118/85eefa91-2a57-4ae5-8c83-de63e3e60fe5.png', 1017, '2025-07-07 12:35:22', '2025-07-07 12:35:22');
INSERT INTO `ev_section` VALUES (5, 2, '2.1 English2121', '2.1 小节的2121描述', 1, 'http://192.168.78.91:10030/static/videos/21/2/8617f48d-6b8f-46f6-bb5e-392e17753db2/a85b71ce-97a6-41c8-97ec-eab74e90f33d.mp4', 'http://192.168.73.91:10030/static/covers/118/faeaf983-857b-483a-9b3c-af9957e6da22.png', 62, '2025-07-07 14:35:23', '2025-07-08 10:03:22');
INSERT INTO `ev_section` VALUES (6, 3, '3.1 English3131', '3.1 小节的3131描述', 1, 'http://192.168.78.91:10030/static/videos/21/3/f7d76f6e-ebaa-4636-a10f-e06d1b22cf69/dd8b7f17-e3c8-433c-bf91-25b4b87443f0.mp4', 'http://192.168.73.91:10030/static/covers/10/a95bf283-0414-4bb3-bb89-070627c2b16a.png', 121, '2025-07-07 14:37:08', '2025-07-07 15:04:48');
INSERT INTO `ev_section` VALUES (9, 9, '自动化的定义', '定义', 1, 'http://192.168.78.91:10030/static/videos/2/9/b29ce44f-aa29-4dfd-9630-7ba8df3e57a8/470e3780-e691-4acf-8e2e-94185bd0ce9c.mp4', 'http://192.168.78.91:10030/static/covers/10/41336a7f-32b1-4a35-8cd7-cfea524063dc.png', 89, '2025-07-08 10:49:40', '2025-07-08 11:57:03');
INSERT INTO `ev_section` VALUES (10, 9, '自动化2.22', '2.22222222', 2, 'http://192.168.78.91:10030/static/videos/2/9/aeb2965c-9c3d-4ae9-a9cd-d58e0ccf06ee/30c70bc1-ecbb-4b6c-870a-feb448fa0141.mp4', 'http://192.168.78.91:10030/static/covers/10/0d4db13b-93ec-43bd-95f2-79149ee3a457.png', 49, '2025-07-08 10:55:16', '2025-07-08 11:39:31');
INSERT INTO `ev_section` VALUES (11, 18, '1.1 高数小节1.1', '高数小节1.1，好学好看', 1, 'http://192.168.78.91:10030/static/videos/23/18/73d38257-0165-4afe-9209-43554f018843/e2a34d26-a87b-4b75-8c37-31f9388544d5.mp4', 'http://192.168.78.91:10030/static/covers/117/28c160b9-60d5-4297-8058-3021b7a9b958.png', 228, '2025-07-10 22:07:45', '2025-07-10 22:07:45');
INSERT INTO `ev_section` VALUES (12, 18, '1.2 高数小节1.2', '高数', 2, 'http://192.168.78.91:10030/static/videos/23/18/3e1d837d-165a-45bf-8470-4e5ee888bbc3/66467b7c-4e66-44dd-972c-c969cc9550e5.mp4', 'http://192.168.78.91:10030/static/covers/117/028dfdd2-f113-4759-ad24-358ef69abda0.png', 213, '2025-07-10 22:08:39', '2025-07-10 22:09:11');
INSERT INTO `ev_section` VALUES (13, 18, '1.3 高数', '1.3高数详情', 3, 'http://192.168.78.91:10030/static/videos/23/18/b60d1224-8574-4932-a09d-24bf83cd77f2/ec6064b3-7bac-440e-b0f1-048996ce752b.mp4', 'http://192.168.78.91:10030/static/covers/117/cc76c96e-3863-432f-95ca-0fbbe4e555d1.png', 213, '2025-07-10 22:09:07', '2025-07-10 22:09:07');
INSERT INTO `ev_section` VALUES (14, 20, '1.1 入门1.1', '入门1.1 学基础', 1, 'http://192.168.78.91:10030/static/videos/24/20/e6695755-8017-4feb-88a0-0c4705622869/c4a4603e-6c78-4165-8836-b97f36547fe4.mp4', 'http://192.168.78.91:10030/static/covers/10/839b0349-ecb3-4123-bdbd-c9a357032e06.png', 66, '2025-07-10 22:16:54', '2025-07-10 22:16:54');
INSERT INTO `ev_section` VALUES (15, 19, '1.2 高数小节1.2', '好学好看', 1, 'http://192.168.78.91:10030/static/videos/23/19/89d86f6f-dfa0-477b-9c17-f76bdf9880a1/8ad6e6f4-6a6f-4757-a4c7-c84d260f23a6.mp4', 'http://192.168.78.91:10030/static/covers/117/6f232450-ed23-4ba3-9252-a77ba52ec8d5.png', 121, '2025-07-11 00:52:22', '2025-07-11 00:52:22');
INSERT INTO `ev_section` VALUES (16, 19, '2.2 高数小节2.2', '快来学', 2, 'http://192.168.78.91:10030/static/videos/23/19/5c88f986-b1e4-4183-840c-d541e6125206/5d8aa795-b389-4825-a6c0-93b7dfcb30b8.mp4', 'http://192.168.78.91:10030/static/covers/117/42f694d1-a8ae-4fe4-8171-b00c3b2bbb4f.png', 161, '2025-07-11 00:52:54', '2025-07-11 00:52:54');
INSERT INTO `ev_section` VALUES (17, 19, '2.3 高数小节2.3', '2.3详情', 3, 'http://192.168.78.91:10030/static/videos/23/19/16315855-24d2-49b7-b84b-73f902536006/0e213776-6ed8-438d-bd51-a08d451e558b.mp4', 'http://192.168.78.91:10030/static/covers/117/959a1ccf-f9cc-4bdb-95fe-9bcfa494b299.png', 98, '2025-07-11 00:53:29', '2025-07-11 00:53:29');
INSERT INTO `ev_section` VALUES (18, 19, '2.4 高数', '2.4详情', 4, 'http://192.168.78.91:10030/static/videos/23/19/b916a68c-7fb7-4c55-8112-7d81ec12fb50/4ed70376-71bb-4854-beb4-b4e3e82d91aa.mp4', 'http://192.168.78.91:10030/static/covers/117/e58fe14c-8171-463b-870b-16db19e37264.png', 98, '2025-07-11 00:54:15', '2025-07-11 00:54:15');
INSERT INTO `ev_section` VALUES (19, 21, '3.1 高数3.1', '3.1详情', 1, 'http://192.168.78.91:10030/static/videos/23/21/d6a51d28-0297-428a-8b2b-435b493e5abd/95d2e59c-4eae-4c1f-9a10-6bc1925f87d3.mp4', 'http://192.168.78.91:10030/static/covers/117/6df99c4d-7648-4a53-b6af-f9e56d568933.png', 144, '2025-07-11 00:55:13', '2025-07-11 00:55:13');
INSERT INTO `ev_section` VALUES (20, 21, '3.2 高数3.2', '3.2详情', 2, 'http://192.168.78.91:10030/static/videos/23/21/b762735f-8f44-416f-a6d8-ae17762f0f25/ed129985-8088-4c91-8cc1-cd02e24b5bc9.mp4', 'http://192.168.78.91:10030/static/covers/117/a2c8fe3a-1e16-402b-9718-5516a381f1cf.png', 135, '2025-07-11 00:55:44', '2025-07-11 00:55:44');
INSERT INTO `ev_section` VALUES (21, 22, '2.1 进阶', '进阶', 1, 'http://192.168.78.91:10030/static/videos/24/22/70405278-3b7d-4770-81f5-7a883a53f45b/0b40c594-64b1-4846-9fd2-2f9a13b5d2e8.mp4', 'http://192.168.78.91:10030/static/covers/10/22844496-bc54-4199-97a0-0dba3c7c6398.png', 48, '2025-07-11 09:46:23', '2025-07-11 09:46:23');
INSERT INTO `ev_section` VALUES (22, 19, '2.5 test', 'test', 4, 'http://192.168.78.91:10030/static/videos/23/19/3a2a722f-6c8b-42b1-b965-0e97efa128ab/ebaae692-9b84-4c1d-a429-deacd1e26dc8.mp4', 'http://192.168.78.91:10030/static/covers/117/61841af8-e44e-40cd-81b6-e3d173a8e19e.jpg', 161, '2025-07-11 10:43:22', '2025-07-11 10:43:22');

SET FOREIGN_KEY_CHECKS = 1;
