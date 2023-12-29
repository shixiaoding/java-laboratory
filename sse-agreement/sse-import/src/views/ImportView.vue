<template>
  <el-upload
    class="upload-demo"
    action="#"
    method="post"
    :http-request="upload"
  >
    <template #trigger>
      <el-button type="primary">select file</el-button>
    </template>

    <!-- <el-button class="ml-3" type="success" @click="submitUpload">
      upload to server
    </el-button> -->

    <template #tip>
      <div class="el-upload__tip">
        jpg/png files with a size less than 500kb
      </div>
    </template>
  </el-upload>
  <div class="demo-progress">
    <el-progress :percentage="uploadPercentage" :format="format"/>
  </div>

</template>


<script lang="ts" setup>
import { onBeforeUnmount, ref } from 'vue'
import type { UploadInstance } from 'element-plus'
import axios from 'axios'

const uploadRef = ref<UploadInstance>()
const format = (percentage) => `${percentage}%`

const uploadPercentage = ref(0)

const upload = (params:any) => {
  console.log("12");
  var aa = "12";
  SSE(aa);

  let formData=new FormData();
  formData.append('file',params.file);
  formData.append('code',aa);
  axios.post('http://localhost:8080/sse-import/import', formData);
}
const source = ref()
const SSE = (hashCode) => {
  if (window.EventSource) {
    // 建立连接
    source.value = new EventSource('http://localhost:8080/sse-import/subscribe?code=' + hashCode)
    /**
     * 连接一旦建立，就会触发open事件
     */
    source.value.onopen = function (e) {
      console.log('建立连接', e)
    }
    /**
     * 客户端收到服务器发来的数据
     */
    source.value.onmessage = function (e) {
      uploadPercentage.value =  parseInt(e.data)
      console.log(e)
      console.log(e.data)
    }
    /**
     * 如果发生通信错误（比如连接中断），就会触发error事件
     */
    source.value.onerror = function (e) {
      if (e.readyState === EventSource.CLOSED) {
        console.log('连接关闭')
        source.value.close = true
      } else {
        console.log(e)
      }
    }
  } else {
    console.log('浏览器不支持SSE')
  }
}
// 卸载
onBeforeUnmount(() => {
    source.value.close() 
  })
</script>


