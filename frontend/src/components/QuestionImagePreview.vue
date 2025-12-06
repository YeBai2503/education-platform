<template>
     <!-- 一个组件及够了，没有必要每个都 -->
     <a-image-preview-group v-model:visible="visible" :current="0"
            :srcList="srcList" />
</template>
<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

const props = defineProps({
    clickArea: String,
})
const visible = ref(false)
const srcList = ref([])

// 存储元素引用，以便在组件卸载时移除事件监听器
let clickAreaElement = null;

const imageChange = (e) => {
    const src = e.target.currentSrc
    if (src) {
        srcList.value = [src]
        setTimeout(() => {
            visible.value = true
        }, 100);
    }
}

// 在mounted钩子中设置点击事件
onMounted(() => {
    if (props.clickArea) {
        try {
            // 获取点击区域元素
            clickAreaElement = document.querySelector(props.clickArea);
            
            if (clickAreaElement) {
                // 添加事件监听器
                clickAreaElement.addEventListener('click', imageChange);
                console.log(`QuestionImagePreview: 成功设置点击区域 ${props.clickArea}`);
            } else {
                console.warn(`QuestionImagePreview: 未找到点击区域 ${props.clickArea}`);
            }
        } catch (error) {
            console.error(`QuestionImagePreview: 设置点击区域时出错`, error);
        }
    }
});

// 在组件卸载前移除事件监听器
onBeforeUnmount(() => {
    if (clickAreaElement) {
        clickAreaElement.removeEventListener('click', imageChange);
        console.log(`QuestionImagePreview: 已移除点击区域事件监听器`);
    }
});
</script>