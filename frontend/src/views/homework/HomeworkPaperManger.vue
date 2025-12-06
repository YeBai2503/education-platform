<template>
    <div class="paper">
        <a-page-header title="作业管理" @back="$router.back" v-if="!selectMode">
            <template #extra>
                <a-button type="primary" @click="visible=true">创建作业</a-button>
            </template>
        </a-page-header>
        <div class="manage">
            <a-table v-model:selected-keys="selectKey" :row-selection="rowSelection" @selection-change="selectChange"
                row-key="id" :columns="columns" :data="homeworkList" page-position="bottom">
                <template #edit="{ record }">
                    <a-button status="danger" @click="delHomeworkPaper(record.id)" style="margin-right: 10px;">
                        <template #icon>
                            <icon-delete />
                        </template>
                    </a-button>
                    <a-button style="margin-right: 10px;" @click="paperPreview(record.id,record.title)">
                        <template #icon>
                            <icon-search />
                        </template>
                    </a-button>
                    <a-button type="primary" @click="toHomeworkPaper(record.id)" style="margin-right: 10px;">
                        <template #icon>
                            <icon-edit />
                        </template>
                    </a-button>
                    <a-button type="primary" @click="toHomeworkPaper(record.id)">
                        <template #icon>
                            <icon-export />
                        </template>
                    </a-button>
                </template>
            </a-table>
        </div>
    </div>
    <a-modal v-model:visible="visible" title="创建作业" :footer="false">
        <HomeworkPaper/>
    </a-modal>
</template>
<script setup>
import { getHomeworkPaperListRequest, deleteHomeworkPaperRequest } from '../../apis/homework-api.js'
import { useRoute, useRouter } from 'vue-router';
import { ref, watch } from 'vue';
import HomeworkPaper from './HomeworkPaper.vue'
import { IconDelete, IconSearch, IconEdit, IconExport } from '@arco-design/web-vue/es/icon';
const props = defineProps({
    selectMode: {
        type: Boolean,
        default: false
    },
    selectKey: {
        type: Array,
        default: ()=>[]
    }
})
const visible=ref(false)
const emit = defineEmits(['selectData'])
const selectKey = ref([])
watch(() => props.selectKey, (key) => {
    selectKey.value = key
})
const rowSelection = {
    type: props.selectMode ? 'radio' : 'checkbox'
};

const selectChange = (keys) => {
    const data=homeworkList.value.filter(item=>keys.includes(item.id))
    console.log(data)
    emit('selectData', keys,data)
}

const route = useRoute();
const router = useRouter();

const courseId = route.params['courseId']
const homeworkList = ref([])
const currpage = ref(1);
const total = ref(1)
const pageSize = ref(10)
//为创建作业提供试卷的组件


// 获取作业列表
const getHomeworkPaperList = () => {
    getHomeworkPaperListRequest(courseId, currpage.value, pageSize.value).then(res => {
        const data = res.data.data
        homeworkList.value = data.list
        currpage.value = data.current
        total.value = data.total
    })
}
const delHomeworkPaper = (id) => {
    deleteHomeworkPaperRequest(id).then(() => {
        getHomeworkPaperList()
    })
}
const paperPreview = (homeworkId,title) => {
    router.push({
        name: 'CourseHomeworkPaperPreView',
        params: {
            homeworkId,
            courseId
        },
        query:{
            title
        }
    })
}
const toHomeworkPaper = (homeworkId) => {
    const homeworkPaperPage = {
        name: 'CourseHomeworkPaper',
        params: {
            courseId
        }
    }
    if (homeworkId) {
        homeworkPaperPage.params.homeworkId = homeworkId;
    }
    console.log(homeworkPaperPage)
    router.push(homeworkPaperPage)

}
getHomeworkPaperList()
const columns = [
    {
        title: '作业名称',
        dataIndex: 'title',
        ellipsis: true,
        slotName: 'title',
    },

    {
        title: '作业介绍',
        dataIndex: 'introduce',
        slotName: 'introduce',
    },
];
if (!props.selectMode) {
    columns.push({
        title: '创建时间',
        dataIndex: 'createdAt',
        slotName: 'createdAt',
    },
    {
        title: '修改时间',
        dataIndex: 'updatedAt',
        slotName: 'updatedAt',
    },
    {
        title: '编辑',
        slotName: 'edit',
        width:250
    })
}
</script>
<style lang="less" scoped>
:deep(.arco-modal-body) {
    overflow: hidden;
}

.paper {
    .operate {
        display: flex;
        align-items: center;
    }

    .manage {
        display: flex;

        .exam-paper {
            flex: 1;
        }
    }
}
</style> 