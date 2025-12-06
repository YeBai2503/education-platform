/*
 Navicat Premium Dump SQL

 Source Server         : database
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : wk_question

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 11/07/2025 11:32:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_answer
-- ----------------------------
DROP TABLE IF EXISTS `tb_answer`;
CREATE TABLE `tb_answer`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int NOT NULL COMMENT '回答者用户ID',
  `question_id` int NOT NULL COMMENT '问题ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '回答内容',
  `is_accepted` int NOT NULL DEFAULT 0 COMMENT '是否被采纳：0-未采纳，1-已采纳',
  `likes` int NOT NULL DEFAULT 0 COMMENT '点赞次数',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_question_id`(`question_id` ASC) USING BTREE COMMENT '问题ID索引'
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '回答表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_answer
-- ----------------------------
INSERT INTO `tb_answer` VALUES (1, 10, 1, '这是更新后的回答内容，提供了更详细的解决方案和额外的说明。', 1, 0, NULL, NULL);
INSERT INTO `tb_answer` VALUES (3, 10, 5, '这是对问题的回答内容，详细描述了解决方案。', 1, 0, NULL, NULL);
INSERT INTO `tb_answer` VALUES (4, 10, 1, '更新了针对问题1的第二条回答。', 0, 0, NULL, NULL);
INSERT INTO `tb_answer` VALUES (5, 10, 5, '对java多线程问题进行回答。', 0, 0, NULL, NULL);
INSERT INTO `tb_answer` VALUES (6, 10, 6, '对java微服务问题进行回答，回答1。', 1, 0, NULL, NULL);
INSERT INTO `tb_answer` VALUES (7, 10, 8, '这是对问题的回答内容，详细描述了解决方案。', 0, 0, NULL, NULL);
INSERT INTO `tb_answer` VALUES (8, 10, 5, '回答当前问题。。。。。。。。。。。。。。', 0, 0, NULL, NULL);
INSERT INTO `tb_answer` VALUES (9, 121, 10, '1. 多元函数微积分\n难点：从一元扩展到多元（二重、三重积分，曲线/曲面积分），涉及空间想象力和复杂的计算。\n\n具体内容：\n\n多元函数的极限、连续性（定义更复杂）。\n\n偏导数与全微分（方向导数、梯度、隐函数求导）。\n\n重积分（二重、三重积分的计算与坐标系转换）。\n\n曲线/曲面积分（格林公式、高斯公式、斯托克斯公式的应用）。\n\n2. 无穷级数\n难点：收敛性判断和展开技巧性强，需要灵活运用各种判别法。\n\n具体内容：\n\n常数项级数（正项级数、交错级数的收敛判别法）。\n\n幂级数（收敛半径、和函数求解）。\n\n傅里叶级数（周期函数的展开，狄利克雷条件）。\n\n3. 微分方程\n难点：类型繁多，解法灵活，高阶或非线性方程难度更大。\n\n具体内容：\n\n一阶方程（可分离变量、齐次、线性、伯努利方程）。\n\n高阶线性方程（常系数齐次/非齐次的特解、欧拉方程）。\n\n偏微分方程初步（如波动方程、热传导方程，需结合边界条件）。\n\n4. 向量代数与空间解析几何\n难点：抽象的空间几何关系（尤其对几何直觉弱的学生）。\n\n具体内容：\n\n空间直线、平面的方程。\n\n曲面的参数方程（如旋转曲面、柱面）。\n\n方向导数、切平面与法线。\n\n5. 极限与连续（基础但易忽视）\n难点：ε-δ语言、数列与函数极限的严格证明。\n\n具体内容：\n\n极限的精确定义。\n\n无穷小量与阶的比较。\n\n一致连续性。\n\n6. 泰勒公式与中值定理\n难点：证明题多，需要构造辅助函数。\n\n具体内容：\n\n拉格朗日中值定理、柯西中值定理的应用。\n\n泰勒展开的余项估计。', 1, 0, NULL, NULL);
INSERT INTO `tb_answer` VALUES (10, 120, 10, '说的好！！！！！！！！', 0, 0, NULL, NULL);

-- ----------------------------
-- Table structure for tb_comment
-- ----------------------------
DROP TABLE IF EXISTS `tb_comment`;
CREATE TABLE `tb_comment`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int NOT NULL COMMENT '评论者用户ID',
  `type` int NOT NULL COMMENT '评论类型：1-问题评论，2-回答评论',
  `related_id` int NOT NULL COMMENT '关联ID，问题ID或回答ID',
  `parent_id` int NOT NULL DEFAULT 0 COMMENT '父评论ID，0表示一级评论',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type_related`(`type` ASC, `related_id` ASC) USING BTREE COMMENT '类型和关联ID联合索引',
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE COMMENT '父评论ID索引'
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_comment
-- ----------------------------
INSERT INTO `tb_comment` VALUES (1, 10, 1, 1, 0, '这个问题很有意思，我有一些想法可以分享。', NULL, NULL);
INSERT INTO `tb_comment` VALUES (2, 10, 2, 1, 0, '这个回答很有帮助，谢谢分享！', NULL, NULL);
INSERT INTO `tb_comment` VALUES (4, 10, 1, 5, 0, '这是一条评论', NULL, NULL);
INSERT INTO `tb_comment` VALUES (5, 10, 1, 5, 0, '感谢您的解答。', NULL, NULL);
INSERT INTO `tb_comment` VALUES (7, 10, 1, 5, 5, '不谢', NULL, NULL);
INSERT INTO `tb_comment` VALUES (8, 10, 1, 5, 5, '@用户10 好的', NULL, NULL);
INSERT INTO `tb_comment` VALUES (9, 10, 1, 5, 0, '这道题有深度', NULL, NULL);
INSERT INTO `tb_comment` VALUES (10, 10, 1, 5, 0, '好好好', NULL, NULL);
INSERT INTO `tb_comment` VALUES (11, 10, 1, 5, 0, '这道题好难。', NULL, NULL);
INSERT INTO `tb_comment` VALUES (12, 10, 1, 5, 0, '。。。。。', NULL, NULL);
INSERT INTO `tb_comment` VALUES (13, 10, 1, 11, 1, '我也觉得。', NULL, NULL);
INSERT INTO `tb_comment` VALUES (14, 10, 1, 11, 12, '我也觉得。', NULL, NULL);
INSERT INTO `tb_comment` VALUES (16, 10, 1, 5, 10, '。。', NULL, NULL);
INSERT INTO `tb_comment` VALUES (17, 10, 1, 5, 4, '这是一条评论的评论', NULL, NULL);
INSERT INTO `tb_comment` VALUES (18, 121, 1, 10, 0, '自问自答', NULL, NULL);
INSERT INTO `tb_comment` VALUES (19, 120, 1, 10, 0, '说的好', NULL, NULL);
INSERT INTO `tb_comment` VALUES (20, 13, 1, 10, 0, '学到了', NULL, NULL);
INSERT INTO `tb_comment` VALUES (21, 122, 1, 10, 0, '学到了', NULL, NULL);
INSERT INTO `tb_comment` VALUES (22, 122, 1, 10, 19, '111', NULL, NULL);

-- ----------------------------
-- Table structure for tb_question
-- ----------------------------
DROP TABLE IF EXISTS `tb_question`;
CREATE TABLE `tb_question`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int NOT NULL COMMENT '发布问题的用户ID',
  `course_id` int NOT NULL COMMENT '问题所属课程ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题内容',
  `status` int NOT NULL DEFAULT 0 COMMENT '问题状态：0-未解决，1-已解决',
  `views` int NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `likes` int NOT NULL DEFAULT 0 COMMENT '点赞次数',
  `favorites` int NOT NULL DEFAULT 0 COMMENT '收藏次数',
  `answer_count` int NOT NULL DEFAULT 0 COMMENT '回答数量',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '问题表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_question
-- ----------------------------
INSERT INTO `tb_question` VALUES (1, 10, 1, '更新后的问题标题', '更新后的问题内容，这里可以是详细的问题描述，支持富文本格式。', 1, 0, 0, 0, 2, NULL, NULL);
INSERT INTO `tb_question` VALUES (4, 10, 1, 'Java中如何实现微服务？', '我在学习Java多线程编程，请问有哪些方式可以在Java中创建和启动线程？Thread类和Runnable接口有什么区别？什么情况下应该使用线程池？', 0, 1, 0, 0, 0, NULL, NULL);
INSERT INTO `tb_question` VALUES (5, 10, 20, 'Java中如何实现多线程？', '我在学习Java多线程编程，请问有哪些方式可以在Java中创建和启动线程？Thread类和Runnable接口有什么区别？什么情况下应该使用线程池？', 1, 53, 0, 0, 3, NULL, NULL);
INSERT INTO `tb_question` VALUES (6, 10, 20, 'Java中如何实现微服务？', '我在学习Java多线程编程，请问有哪些方式可以在Java中创建和启动线程？Thread类和Runnable接口有什么区别？什么情况下应该使用线程池？', 1, 9, 0, 0, 1, NULL, NULL);
INSERT INTO `tb_question` VALUES (8, 8, 20, 'Java中如何实现mapper', '学习mapper', 0, 5, 0, 0, 1, NULL, NULL);
INSERT INTO `tb_question` VALUES (9, 10, 20, '不清楚java中的输入输出流', '具体内容待讨论', 0, 3, 0, 0, 0, NULL, NULL);
INSERT INTO `tb_question` VALUES (10, 121, 23, '高数难度', '高数中难的有哪几章', 1, 23, 0, 0, 2, NULL, NULL);
INSERT INTO `tb_question` VALUES (11, 121, 23, '老师布置的作业是啥？', '7.10布置的作业？', 0, 0, 0, 0, 0, NULL, NULL);
INSERT INTO `tb_question` VALUES (12, 122, 23, '什么是极限', '如题', 0, 0, 0, 0, 0, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
