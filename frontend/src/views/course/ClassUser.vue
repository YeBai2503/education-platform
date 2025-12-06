<template>
    <a-page-header title="班级成员"  @back="$router.back">
    </a-page-header>
    <a-spin :loading="loading" style="width: 100%;">
        <div v-if="userList.length!=0">
            <ul>
                <li v-for="item in userList" class="ebutton-hover user-item">
                    <Avatar v-loadImg shape="square" :image-url="item.picture" />
                    <span class="user-item-name">{{item.nickname}}</span>
                </li>
            </ul>
            <a-pagination style="justify-content: center;margin:10px 0" v-model:current="currentPage" :total="total" @change="getUserList" :current="currentPage" :page-size="9" />
        </div>
        <a-empty v-else />
    </a-spin>
    
</template>
<script setup>
import { Avatar } from '@arco-design/web-vue';
import { ref } from 'vue';
import { useRoute } from 'vue-router';
import { getClassUsersRequest } from '../../apis/course-api';
import { Message } from '@arco-design/web-vue';

const userList = ref([]);
const route = useRoute();
const currentPage = ref(1);
const total = ref(0);
const loading = ref(false);
const classId = route.params['classId'];

const getUserList = (page = currentPage.value) => {
    loading.value = true;
    // 确保使用固定的classId，而不是每次从route.params获取
    getClassUsersRequest(classId, page).then(res => {
        const data = res.data.data;
        userList.value = data.list;
        currentPage.value = data.current;
        total.value = data.total;
        loading.value = false;
    }).catch(err => {
        console.error('获取班级成员失败', err);
        loading.value = false;
        Message.error('获取班级成员失败');
    });
};

// 初始加载
getUserList();

</script>
<style lang="less" scoped>
.user-item{
    padding:10px;
    margin: 5px;
    .user-item-name{
        margin-left: 10px;
        color: var(--color-text-1);
    }
}
</style>