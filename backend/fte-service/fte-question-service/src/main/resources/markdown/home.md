# 问题中心服务

## 简介
问题中心服务是为课程提供问答功能的微服务，支持学生在课程中提问和回答问题。通过这个服务，课程参与者可以在课程问答区发布问题、回答问题、评论问题和回答，以促进课程学习中的互动和交流。

## 主要功能
- 发布问题：学生可以在课程中发布与课程内容相关的问题
- 回答问题：课程参与者可以回答其他人提出的问题
- 问题评论：可以对问题进行评论讨论
- 回答评论：可以对回答进行评论讨论
- 问题点赞：表达对问题的认同
- 回答点赞：表达对回答的认同
- 采纳回答：问题发布者可以采纳最佳回答
- 问题查询：支持按课程、用户、状态等条件查询问题
- 课程问题管理：课程管理员可以管理课程中的问题

## 数据库表设计

### 问题表(tb_question)
| 字段名 | 类型 | 说明 |
| --- | --- | --- |
| id | int | 主键ID |
| user_id | int | 发布问题的用户ID |
| course_id | int | 问题所属课程ID |
| title | varchar | 问题标题 |
| content | text | 问题内容 |
| status | tinyint | 问题状态：0-未解决，1-已解决 |
| views | int | 浏览次数 |
| likes | int | 点赞次数 |
| favorites | int | 收藏次数 |
| answer_count | int | 回答数量 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

### 回答表(tb_answer)
| 字段名 | 类型 | 说明 |
| --- | --- | --- |
| id | int | 主键ID |
| user_id | int | 回答者用户ID |
| question_id | int | 问题ID |
| content | text | 回答内容 |
| is_accepted | tinyint | 是否被采纳：0-未采纳，1-已采纳 |
| likes | int | 点赞次数 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

### 评论表(tb_comment)
| 字段名 | 类型 | 说明 |
| --- | --- | --- |
| id | int | 主键ID |
| user_id | int | 评论者用户ID |
| type | tinyint | 评论类型：1-问题评论，2-回答评论 |
| related_id | int | 关联ID，问题ID或回答ID |
| parent_id | int | 父评论ID，0表示一级评论 |
| content | varchar | 评论内容 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

## 接口文档
本文档提供了问题中心服务所有接口的详细说明，包括请求参数、返回结果等。请通过Swagger或Knife4j查看完整API文档。 