import { defineStore } from 'pinia' 
import { userAuthInfoRequest,baseUserInfoRequest } from '../apis/user-api'
import SocketService from '../utils/web-socket-service.js'
import {getImageUrl} from '../utils/image'
import {IconApps} from "@arco-design/web-vue/es/icon";

const useUserStore = defineStore('user',{ 
    state: () => ({ 
        token:null,
        userInfo:null,
        baseUserInfo:null,
        theme:'light'
    }),
    getters:{
        isLogin: (state) => state.token!=null,
        menu:()=>{
            return [
                {
                    name: "个人信息",
                    icon: IconApps,
                    key: "UserInfo",
                    params: {},
                    visble: true,
                },
            ]
        }
    },
    actions:{
        async getUserInfo(){
            const resp=await userAuthInfoRequest()
            const data=resp.data.data;
            data.picture=getImageUrl(data.picture)
            this.userInfo=data
        },
        async getBaseUserInfo(){
            const resp=await baseUserInfoRequest()
            const data=resp.data.data;
            data.picture=getImageUrl(data.picture)
            this.baseUserInfo=data
        },
        updateAvatar(path){
            const pictureUrl = getImageUrl(path);
            // 使用解构赋值创建新对象以确保响应式更新
            this.userInfo = {
                ...this.userInfo,
                picture: pictureUrl
            };
            
            if(this.baseUserInfo) {
                this.baseUserInfo = {
                    ...this.baseUserInfo,
                    picture: pictureUrl
                };
            }
            
            console.log('Store: 头像已更新', pictureUrl);
        },
        toggleTheme(dark){
            if (dark) {
                this.theme = 'dark';
                document.body.setAttribute('arco-theme', 'dark');
              } else {
                this.theme = 'light';
                document.body.removeAttribute('arco-theme');
              }
        },
        logOut(){
            this.userInfo=null
            this.token=null
            this.baseUserInfo=null
            SocketService.instance?.close()
        }
    },
    persist: {
        enabled: true,
    }
}) 
export default useUserStore