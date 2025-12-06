// src/mock/videoData.js

export const mockChapters = [
    {
      id: 'chapter1',
      title: '第一章：课程介绍',
      description: '本章介绍课程大纲和学习目标',
      videos: [
        {
          id: 'video1',
          title: '1.1 课程概述',
          duration: 5, 
          coverUrl: 'https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__340.jpg',
          videoUrl: 'http://192.168.78.91:10030/static/686a2c539c18ae12f42c3904.mp4', // 示例视频
          progress: 0,
          completed: false,
          chapterId: 'chapter1'
        },
        {
          id: 'video2',
          title: '1.2 环境搭建',
          duration: 10,
          coverUrl: 'https://cdn.pixabay.com/photo/2015/12/01/20/28/road-1072823__340.jpg',
          videoUrl: 'https://samplelib.com/lib/preview/mp4/sample-10s.mp4',
          progress: 0,
          completed: false,
          chapterId: 'chapter1'
        }
      ]
    },
    {
      id: 'chapter2',
      title: '第二章：基础知识',
      description: '本章介绍基本概念和工具使用',
      videos: [
        {
          id: 'video3',
          title: '2.1 基本概念',
          duration: 15, // 12分钟
          coverUrl: 'https://cdn.pixabay.com/photo/2018/01/14/23/12/nature-3082832__340.jpg',
          videoUrl: 'https://samplelib.com/lib/preview/mp4/sample-15s.mp4',
          progress: 0,
          completed: false,
          chapterId: 'chapter2'
        },
        {
          id: 'video4',
          title: '2.2 工具使用',
          duration: 20, // 10分钟
          coverUrl: 'https://cdn.pixabay.com/photo/2016/05/05/02/37/sunset-1373171__340.jpg',
          videoUrl: 'https://samplelib.com/lib/preview/mp4/sample-20s.mp4',
          progress: 0,
          completed: false,
          chapterId: 'chapter2'
        }
      ]
    },
    {
      id: 'chapter3',
      title: '第三章：进阶技巧',
      description: '本章介绍进阶使用技巧和案例分析',
      videos: [
        {
          id: 'video5',
          title: '3.1 高级特性',
          duration: 30, 
          coverUrl: 'https://cdn.pixabay.com/photo/2015/12/01/20/28/forest-1072828__340.jpg',
          videoUrl: 'https://samplelib.com/lib/preview/mp4/sample-30s.mp4',
          progress: 0,
          completed: false,
          chapterId: 'chapter3'
        },
        {
          id: 'video6',
          title: '3.2 案例分析',
          duration: 20, // 15分钟
          coverUrl: 'https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__340.jpg',
          videoUrl: 'https://samplelib.com/lib/preview/mp4/sample-20s.mp4',
          progress: 0,
          completed: false,
          chapterId: 'chapter3'
        }
      ]
    }
  ];
  
  export const mockComments = [
    {
      id: 'comment1',
      content: '<p>这个视频讲解得很清晰，对我理解课程内容很有帮助！</p>',
      createdAt: '2023-11-15 14:30:00',
      user: {
        nickname: '学习达人',
        picture: 'https://cdn.pixabay.com/photo/2016/11/18/23/38/child-1837375__340.jpg',
        isTeacher: false
      }
    },
    {
      id: 'comment2',
      content: '<p>非常感谢这个详细的讲解，期待后续内容！</p>',
      createdAt: '2023-11-14 10:15:00',
      user: {
        nickname: '课程老师',
        picture: 'https://cdn.pixabay.com/photo/2015/01/08/18/29/entrepreneur-593358__340.jpg',
        isTeacher: true
      }
    },
    {
      id: 'comment3',
      content: '<p>请问这个技术在实际项目中如何应用？有没有更多的示例？</p>',
      createdAt: '2023-11-13 16:45:00',
      user: {
        nickname: '技术爱好者',
        picture: 'https://cdn.pixabay.com/photo/2018/01/15/07/51/woman-3083383__340.jpg',
        isTeacher: false
      }
    }
  ];
