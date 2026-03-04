<template>
  <div class="home-progress">
    <div class="title">月访问量</div>
    <div class="tip">同上期增�?/div>
    <el-progress
      class="progress"
      :text-inside="true"
      :stroke-width="24"
      :percentage="20"
      status="success"
    ></el-progress>
    <div class="title">月用户量</div>
    <div class="tip">同上期增�?/div>
    <el-progress
      class="progress"
      :text-inside="true"
      :stroke-width="24"
      :percentage="50"
      status="success"
    ></el-progress>
    <div class="title">月收�?/div>
    <div class="tip">同上期减�?/div>
    <el-progress
      class="progress"
      :text-inside="true"
      :stroke-width="24"
      :percentage="28"
      status="exception"
    ></el-progress>
  </div>
</template>
<script>
export default {};
</script>
<style lang="scss">
.home-progress {
  background: #ffffff;
  height: 400px;
  padding: 20px;
  .title {
    color: #666666;
    font-weight: bold;
    font-size: 20px;
    margin-top: 10px;
  }
  .tip {
    color: #888888;
    font-size: 16px;
    margin-top: 10px;
  }
  .progress {
    margin-top: 10px;
  }
}
</style>
