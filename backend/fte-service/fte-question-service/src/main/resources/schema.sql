-- 创建问题表
CREATE TABLE IF NOT EXISTS `tb_question` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int(11) NOT NULL COMMENT '发布问题的用户ID',
  `course_id` int(11) NOT NULL COMMENT '问题所属课程ID',
  `title` varchar(255) NOT NULL COMMENT '问题标题',
  `content` text NOT NULL COMMENT '问题内容',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '问题状态：0-未解决，1-已解决',
  `views` int(11) NOT NULL DEFAULT '0' COMMENT '浏览次数',
  `likes` int(11) NOT NULL DEFAULT '0' COMMENT '点赞次数',
  `favorites` int(11) NOT NULL DEFAULT '0' COMMENT '收藏次数',
  `answer_count` int(11) NOT NULL DEFAULT '0' COMMENT '回答数量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问题表';

-- 创建回答表
CREATE TABLE IF NOT EXISTS `tb_answer` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int(11) NOT NULL COMMENT '回答者用户ID',
  `question_id` int(11) NOT NULL COMMENT '问题ID',
  `content` text NOT NULL COMMENT '回答内容',
  `is_accepted` int(11) NOT NULL DEFAULT '0' COMMENT '是否被采纳：0-未采纳，1-已采纳',
  `likes` int(11) NOT NULL DEFAULT '0' COMMENT '点赞次数',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_question_id` (`question_id`) COMMENT '问题ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回答表';

-- 创建评论表
CREATE TABLE IF NOT EXISTS `tb_comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int(11) NOT NULL COMMENT '评论者用户ID',
  `type` int(11) NOT NULL COMMENT '评论类型：1-问题评论，2-回答评论',
  `related_id` int(11) NOT NULL COMMENT '关联ID，问题ID或回答ID',
  `parent_id` int(11) NOT NULL DEFAULT '0' COMMENT '父评论ID，0表示一级评论',
  `content` varchar(1000) NOT NULL COMMENT '评论内容',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type_related` (`type`, `related_id`) COMMENT '类型和关联ID联合索引',
  KEY `idx_parent_id` (`parent_id`) COMMENT '父评论ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表'; 