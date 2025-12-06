<template>
  <div class="tags">
    <!-- <div class="tags-bg" :style="bgStyle"></div> -->
    <ul
      class="tags-wrap"
      :style="{
        'flex-direction': direction == 'horizontal' ? 'row' : 'column',
      }"
    >
      <li class="tags-bg" :style="bgStyle"></li>
      <li
        v-for="(item, index) of tagList"
        :key="index"
        :class="{ active: tag == index }"
        @click="taggleTag(index)"
      >
        {{ item.name }}
      </li>
    </ul>
  </div>
</template>
<script setup>
import { computed } from "vue";

const props = defineProps({
  tag: {
    type: Number,
    default: 0,
  },
  tagList: Array,
  direction: {
    type: String,
    default: "horizontal",
  },
});
const emits = defineEmits(["update:tag", "tab-click"]);
const taggleTag = (index) => {
  emits("update:tag", index);
  emits("tab-click", index);
};

const bgStyle = computed(() => {
  let perc = 100 / props.tagList.length;
  let posi = props.tag * perc;
  if (props.direction == "horizontal") {
    return {
      left: posi + "%",
      height: "100%",
      width: perc + "%",
    };
  } else {
    return {
      top: posi + "%",
      height: perc + "%",
      width: "100%",
    };
  }
});
</script>
<style lang="less" scoped>
.tags {
  height: 44px;
  background: #f8f9fa;
  border-radius: 10px;
  padding: 4px;
  font-size: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

  .tags-wrap {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    position: relative;
    left: 0;
    
    .tags-bg {
      position: absolute;
      background: white;
      border-radius: 8px;
      left: 0;
      top: 0;
      transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
      box-shadow: 0 2px 10px rgba(102, 126, 234, 0.15);
    }
    
    li {
      z-index: 10;
      flex: 1;
      height: 100%;
      width: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
      color: #666;
      cursor: pointer;
      transition: all 0.3s ease;
      font-weight: 500;
      
      &:hover {
        color: #667eea;
      }
    }
    
    .active {
      color: #667eea;
      font-weight: 600;
    }
  }
}
</style>
