const reviewType = [
  {
    value: "NONE",
    label: "未批阅",
  },
  {
    value: "ROBOT",
    label: "机器批阅",
  },
  {
    value: "TEACHER",
    label: "老师批阅",
  },
];
const resultType=[
    {
        value: "NONE",
        label: "未批阅",
    }, {
        value: "ERROR",
        label: "错误",
    }, {
        value: "WRONG",
        label: "半错",
    }, {
        value: "CORRECT",
        label: "正确",
    },
]
const getTypeInfo=(value,arr)=>{
    for (const item of arr) {
        if(item.value==value){
            return item;
        }
    }
    // 如果未找到匹配项，返回第一个类型作为默认值
    console.warn(`未找到匹配的类型: ${value}，使用默认值`);
    return arr[0];
}
export const getReviewType=(value)=>getTypeInfo(value,reviewType);
export const getResultType=(value)=>getTypeInfo(value,resultType);
