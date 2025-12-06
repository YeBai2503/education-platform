import { defineConfig,loadEnv  } from 'vite'
import vue from '@vitejs/plugin-vue'

// import OptimizationPersist from 'vite-plugin-optimize-persist'
// import PkgConfig from 'vite-plugin-package-config'
// import Components from 'unplugin-vue-components/vite';
// import { ArcoResolver } from 'unplugin-vue-components/resolvers';
import vitePluginForArco from '@arco-plugins/vite-vue'
// import svgLoader from 'vite-svg-loader'
const { resolve } = require('path')
// https://vitejs.dev/config/
export default ({ mode })=>{
  const envConfig = loadEnv(mode, process.cwd())
  const app_ip=envConfig.VITE_API_HOST_IP
  const app_host=`http://${app_ip}`
  const user_path=envConfig.VITE_USER_API_URL
  const exam_path=envConfig.VITE_EXAM_API_URL
  const auth_path=envConfig.VITE_AUTH_API_URL
  const message_path=envConfig.VITE_MESSAGE_API_URL
  const file_path=envConfig.VITE_FILE_API_URL
  const note_path=envConfig.VITE_NOTE_API_URL
  const experiment_path=envConfig.VITE_EXPERIMENT_API_URL
  const question_path=envConfig.VITE_QUESTION_API_URL || '/exam-question' // 使用环境变量或默认值
  const homework_path=envConfig.VITE_HOMEWORK_API_URL
  const video_path=envConfig.VITE_VIDEO_API_URL
  return defineConfig({
    plugins: [
      vue(),
      // PkgConfig(),
      // OptimizationPersist()
      // svgLoader(),
      //按需加载
      vitePluginForArco({
        theme:'@arco-themes/vue-mgo-blog',
        style: 'css',
        // iconPrefix:'icon'
      }),
    ],
    server: {
      open: '/',
      port:3030,
      host: '0.0.0.0'	,
      proxy: {
            //用户微服务
          '/uapi': {
              target: app_host,
              changeOrigin: true,
              rewrite: (path) => path.replace(/^\/uapi/, user_path)
            },
          '/aapi': {
            target: app_host,
            changeOrigin: true,
            rewrite: (path) => path.replace(/^\/aapi/, auth_path)
          },
            //考试微服务
          '/eapi': {
            target: app_host,
            changeOrigin: true,
            rewrite: (path) => path.replace(/^\/eapi/, exam_path)
          },
          '/mapi': {
            target: app_host,
            changeOrigin: true,//是否允许跨域
            rewrite: (path) => path.replace(/^\/mapi/, message_path),
          },
          '/fapi': {
            target: app_host,
            changeOrigin: true,//是否允许跨域
            rewrite: (path) => path.replace(/^\/fapi/, file_path),
          },
          //作业微服务
          '/hapi': {
            target: app_host,
            changeOrigin: true,
            rewrite: (path) => path.replace(/^\/hapi/, homework_path)
          },
          //视频微服务
          '/vapi': {
            target: app_host,
            changeOrigin: true,
            rewrite: (path) => path.replace(/^\/vapi/, video_path)
          },
          // 笔记微服务
          '/napi': {
            target: app_host,
            changeOrigin: true,
            rewrite: (path) => path.replace(/^\/napi/, note_path),
          },
          '/exam-experiment': {
            target: app_host,
            changeOrigin: true,
            rewrite: (path) => path.replace(/^\/exam-experiment/, experiment_path),
          },
          // 问题中心微服务
          '/qapi': {
            target: app_host,
            changeOrigin: true,
            rewrite: (path) => path.replace(/^\/qapi/, question_path),
          },
          // 代理websocket请求
          '/mapi-scoket': {
            target: `ws://${app_ip}`,
            changeOrigin: true,//是否允许跨域
            rewrite: (path) => path.replace(/^\/mapi-scoket/, message_path),
            ws: true //开启wsS
          }
      }
    },
    resolve: {
      alias: [//配置别名
       { find: '@', replacement: resolve(__dirname, 'src') },
       { find: '@views', replacement: resolve(__dirname, 'src/views') }
      ],
      // 情景导出 package.json 配置中的exports字段
      conditions: [],
      // 导入时想要省略的扩展名列表
      // 不建议使用 .vue 影响IDE和类型支持
      extensions:['.mjs','.js','.ts','.jsx','.tsx','.json']  
     },
  })
}
