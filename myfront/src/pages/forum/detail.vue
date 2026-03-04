<template>
  <div :style='{"width":"calc(100% - 40px)","padding":"30px 20px 40px","margin":"20px auto 0","position":"relative","background":"rgba(255,255,255,.3)"}'>
    <div class="section-title" :style='{"padding":"30px 0 30px","margin":"20px auto 20px","borderColor":"#9dcde9","color":"#333","textAlign":"center","borderRadius":"0px","background":"linear-gradient(320deg, rgba(204,204,204,.0) 0%, rgba(255,255,255,.9) 80%, rgba(204,204,204,.0) 100%)","borderWidth":"6px","width":"100%","fontSize":"30px","lineHeight":"30px","borderStyle":"dotted","fontWeight":"600"}'>服装交流</div>
    <div class="section-content">
      <div class="content-title">{{detail.title}}</div>
      <div class="content-sub-title">发布人：{{detail.username}}&nbsp;&nbsp;发布时间：{{detail.addtime}}</div>
      <el-divider></el-divider>
      <div class="content-detail" v-html="detail.content"></div>
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <span style="height: 40px;line-height: 40px;color: #666;font-size: 18px;">评论列表</span>
          <el-button style="float: right;" icon="el-icon-plus" type="success" @click="dialogFormVisible = true">点击评论</el-button>
        </div>
        <span v-for="item in commentList" :key="item.id">
          <div class="header-block">
            <el-avatar v-if="item.avatarurl" :size="50" :src="baseUrl + item.avatarurl"></el-avatar>
            <el-avatar v-if="!item.avatarurl" :size="50" :src="require('@/assets/touxiang.png')"></el-avatar>
            <span class="userinfo">用户：{{item.username}}</span>
          </div>
          <div class="content-block-ask">
            {{item.content}}
          </div>
          <el-divider></el-divider>
        </span>
      </el-card>
    </div>
    <el-dialog title="添加评论" :visible.sync="dialogFormVisible">
      <el-form :model="form" :rules="rules" ref="form">
        <el-form-item label="评论" label-width="60px" prop="content">
          <el-input type="textarea" :rows="5" v-model="form.content" autocomplete="off" placeholder="请输入评�?></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">�?�?/el-button>
        <el-button type="primary" @click="addForum('form')">�?�?/el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    //数据集合
    data() {
      return {
        baseUrl: '',
        detail: {},
        commentList: [],
        dialogFormVisible: false,
        form: {
          content: '',
          parentid: '',
          userid: localStorage.getItem('userid'),
          username: localStorage.getItem('username'),
          avatarurl: '',
        },
        rules: {
          content: [
            { required: true, message: '请输入评�?, trigger: 'blur' }
          ]
        }
      }
    },
    created() {
      this.baseUrl = this.$config.baseUrl;
      this.detail = Object.assign({}, JSON.parse(this.$route.query.detailObj));
      this.getCommentList();
    },
    mounted() {
      this.form.parentid = this.detail.id;
    },
    //方法集合
    methods: {
      getCommentList() {
        this.$http.get(`forum/list/${this.detail.id}`).then(res => {
          if (res.data.code == 0) {
            this.commentList = res.data.data.childs;
          }
        });
      },
      addForum(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.form.avatarurl = localStorage.getItem('headportrait')?localStorage.getItem('headportrait'):'';
            this.$http.post('forum/add', this.form).then(res => {
              if (res.data.code == 0) {
                this.$message({
                  type: 'success',
                  message: '评论成功!',
                  duration: 1500,
                  onClose: () => {
                    this.form.content = '';
                    this.getCommentList();
                    this.dialogFormVisible = false;
                  }
                });
              }
            });
          } else {
            return false;
          }
        });
      }
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .section {
    width: 900px;
    margin: 0 auto;
  }

  .section-content {
      margin-top: 30px;
  }
  .content-title {
      text-align: center;
      font-size: 22px;
      font-weight: bold;
  }
  .content-sub-title {
      text-align: center;
      margin-top: 20px;
      color: #888888;
      font-size: 14px;
  }
  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }
  .clearfix:after {
    clear: both
  }
  .header-block {
    height: 50px;
    line-height: 50px;
    display: flex;
  }
  .userinfo {
    align-self: center;
    margin-left: 15px;
  }
  .content-block-ask {
    margin-left: 65px;
    margin-top: 15px;
  }
  .content-detail img {
    max-width: 900px;
    height: auto;
  }
</style>
