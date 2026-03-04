<template>
	<div>

	<div class="container" :style='{"minHeight":"100vh","width":"100%","alignItems":"center","background":"url(http://codegen.caihongy.cn/20221231/7a3226bd0dfc41549fbee764c9f6552d.jpg) no-repeat center top / 100% 100%","justifyContent":"center","display":"flex"}'>
		<el-form class='rgs-form' v-if="pageFlag=='register'" :style='{"padding":"20px","boxShadow":"0px 0px 0px #9cd8da,inset 0px 0px 0px 0px #e0f8e8","margin":"0","borderColor":"#00adb5  #00adb5 #00adb5 #00adb5","borderRadius":"30px","background":"rgba(255,255,255,.0)","borderWidth":"0px","width":"800px","minWidth":"800px","borderStyle":"solid double solid double","height":"auto"}' ref="registerForm" :model="registerForm" :rules="rules">
			<div v-if="true" :style='{"width":"100%","margin":"0 0 4px 0","fontSize":"20px","color":"#3086b9","textAlign":"center","fontWeight":"500"}'>USER / REGISTER</div>
			<div v-if="true" :style='{"margin":"0 auto 20px","borderColor":"#c4deee","color":"#3086b9","textAlign":"center","borderWidth":"4px 0px","width":"80%","fontSize":"22px","lineHeight":"44px","borderStyle":"dotted dashed","fontWeight":"500"}'>私人西服定制注册</p></div>
			<el-form-item :style='{"width":"80%","padding":"0","margin":"0 auto 20px","height":"auto"}' v-if="tableName=='yonghu'" prop="yonghuzhanghao">
				<div v-if="false" :style='{"width":"64px","lineHeight":"44px","fontSize":"14px","color":"rgba(64, 158, 255, 1)"}'>用户账号�?/div>
				<el-input v-model="registerForm.yonghuzhanghao"  placeholder="请输入用户账�? />
			</el-form-item>
			<el-form-item :style='{"width":"80%","padding":"0","margin":"0 auto 20px","height":"auto"}' v-if="tableName=='yonghu'" prop="mima">
				<div v-if="false" :style='{"width":"64px","lineHeight":"44px","fontSize":"14px","color":"rgba(64, 158, 255, 1)"}'>密码�?/div>
				<el-input v-model="registerForm.mima" type="password" placeholder="请输入密�? />
			</el-form-item>
			<el-form-item :style='{"width":"80%","padding":"0","margin":"0 auto 20px","height":"auto"}' v-if="tableName=='yonghu'" prop="mima">
				<div v-if="false" :style='{"width":"64px","lineHeight":"44px","fontSize":"14px","color":"rgba(64, 158, 255, 1)"}'>确认密码�?/div>
				<el-input v-model="registerForm.mima2" type="password" placeholder="请再次输入密�? />
			</el-form-item>
			<el-form-item :style='{"width":"80%","padding":"0","margin":"0 auto 20px","height":"auto"}' v-if="tableName=='yonghu'" prop="yonghuxingming">
				<div v-if="false" :style='{"width":"64px","lineHeight":"44px","fontSize":"14px","color":"rgba(64, 158, 255, 1)"}'>用户姓名�?/div>
				<el-input v-model="registerForm.yonghuxingming"  placeholder="请输入用户姓�? />
			</el-form-item>
			<el-form-item :style='{"width":"80%","padding":"0","margin":"0 auto 20px","height":"auto"}' v-if="tableName=='yonghu'" prop="touxiang">
				<div v-if="false" :style='{"width":"64px","lineHeight":"44px","fontSize":"14px","color":"rgba(64, 158, 255, 1)"}'>头像�?/div>
                <file-upload
					tip="点击上传头像"
					action="file/upload"
					:limit="1"
					:multiple="true"
					:fileUrls="registerForm.touxiang?registerForm.touxiang:''"
					@change="yonghutouxiangUploadChange"
				></file-upload>
			</el-form-item>
			<el-form-item :style='{"width":"80%","padding":"0","margin":"0 auto 20px","height":"auto"}' v-if="tableName=='yonghu'" prop="xingbie">
				<div v-if="false" :style='{"width":"64px","lineHeight":"44px","fontSize":"14px","color":"rgba(64, 158, 255, 1)"}'>性别�?/div>
                <el-select v-model="registerForm.xingbie" placeholder="请选择性别" >
                  <el-option
                      v-for="(item,index) in yonghuxingbieOptions"
                      :key="index"
                      :label="item"
                      :value="item">
                  </el-option>
                </el-select>
			</el-form-item>
			<el-form-item :style='{"width":"80%","padding":"0","margin":"0 auto 20px","height":"auto"}' v-if="tableName=='yonghu'" prop="lianxifangshi">
				<div v-if="false" :style='{"width":"64px","lineHeight":"44px","fontSize":"14px","color":"rgba(64, 158, 255, 1)"}'>联系方式�?/div>
				<el-input v-model="registerForm.lianxifangshi"  placeholder="请输入联系方�? />
			</el-form-item>
			<el-button :style='{"border":"0","cursor":"pointer","padding":"0px","boxShadow":"0px 0px 0px #9cdde0","margin":"12px 16px 10px 240px","color":"#fff","display":"inline-block","outline":"none","borderRadius":"8px","background":"linear-gradient(320deg, rgba(48,134,185,1) 0%, rgba(197,230,250,1) 80%, rgba(48,134,185,1) 100%)","width":"128px","fontSize":"16px","height":"40px"}' type="primary" @click="submitForm('registerForm')">注册</el-button>
			<el-button :style='{"border":"0px solid #bbb","cursor":"pointer","padding":"0px","boxShadow":"1px 2px 3px #ccc","margin":"12px auto 5px","color":"#333","display":"inline-block","outline":"none","borderRadius":"8px","background":"linear-gradient(320deg, rgba(204,204,204,1) 0%, rgba(255,255,255,1) 80%, rgba(204,204,204,1) 100%),#999","width":"128px","fontSize":"16px","height":"40px"}' @click="resetForm('registerForm')">重置</el-button>
			<router-link :style='{"cursor":"pointer","padding":"0 10%","color":"#333","display":"inline-block","width":"100%","lineHeight":"1","fontSize":"12px","textDecoration":"none"}' to="/login">已有账户登录</router-link>
		</el-form>
    </div>
  </div>
</div>
</template>

<script>

export default {
    //数据集合
    data() {
		return {
            pageFlag : '',
			tableName: '',
			registerForm: {
                xingbie: '',
            },
			rules: {},
            yonghuxingbieOptions: [],
		}
    },
	mounted() {
	},
    created() {
      this.pageFlag = this.$route.query.pageFlag;
      this.tableName = this.$route.query.role;
      if ('yonghu' == this.tableName) {
        this.rules.mima = [{ required: true, message: '请输入密�?, trigger: 'blur' }];
      }
        this.yonghuxingbieOptions = "�?�?.split(',');
      if ('yonghu' == this.tableName) {
        this.rules.lianxifangshi = [{ required: true, validator: this.$validate.isMobile, trigger: 'blur' }];
      }
    },
    //方法集合
    methods: {
      // 获取uuid
      getUUID () {
        return new Date().getTime();
      },
        // 下二�?      yonghutouxiangUploadChange(fileUrls) {
          this.registerForm.touxiang = fileUrls.replace(new RegExp(this.$config.baseUrl,"g"),"");
      },

        // 多级联动参数


      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            var url=this.tableName+"/register";
              if(`yonghu` == this.tableName && this.registerForm.yonghuzhanghao.length<3){
                this.$message.error('用户账号长度不能小于3');
                return
              }
              if(`yonghu` == this.tableName && this.registerForm.yonghuzhanghao.length>10){
                this.$message.error('用户账号长度不能大于10');
                return
              }
               if(`yonghu` == this.tableName && this.registerForm.mima!=this.registerForm.mima2) {
                this.$message.error(`两次密码输入不一致`);
                return
               }
              if(`yonghu` == this.tableName && this.registerForm.mima.length<3){
                this.$message.error('密码长度不能小于3');
                return
              }
              if(`yonghu` == this.tableName && this.registerForm.mima.length>10){
                this.$message.error('密码长度不能大于10');
                return
              }
            this.$http.post(url, this.registerForm).then(res => {
              if (res.data.code === 0) {
                this.$message({
                  message: '注册成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.$router.push('/login');
                  }
                });
              } else {
                this.$message.error(res.data.msg);
              }
            });
          } else {
            return false;
          }
        });
      },
      resetForm(formName) {
        this.$refs[formName].resetFields();
      }
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
	.container {
		position: relative;
		background: url(http://codegen.caihongy.cn/20221231/7a3226bd0dfc41549fbee764c9f6552d.jpg) no-repeat center top / 100% 100%;

		.el-date-editor.el-input {
			width: 100%;
		}
		
		.rgs-form .el-input ::v-deep .el-input__inner {
						border-radius: 0px;
						padding: 0 10px;
						box-shadow: 0px 0px 0px #a0d8db;
						outline: none;
						color: #999;
						background: #fff;
						width: 100%;
						font-size: 14px;
						border-color: #c4deee;
						border-width: 0 0 2px;
						border-style: dotted;
						height: 40px;
					}
		
		.rgs-form .el-select ::v-deep .el-input__inner {
						border: 0px solid #eacb96;
						padding: 0 10px;
						color: #999;
						font-size: 14px;
						border-color: #c4deee;
						border-radius: 0px;
						box-shadow: 0px 0px 0px #a0d8db;
						outline: none;
						background: #fff;
						width: 100%;
						border-width: 0 0 2px;
						border-style: dotted;
						height: 40px;
					}
		
		.rgs-form .el-date-editor ::v-deep .el-input__inner {
						border-radius: 0px;
						padding: 0 10px 0 30px;
						box-shadow: 0px 0px 0px #a0d8db;
						outline: none;
						color: #999;
						background: #fff;
						width: 100%;
						font-size: 14px;
						border-color: #c4deee;
						border-width: 0 0 2px;
						border-style: dotted;
						height: 40px;
					}
		
		.rgs-form .el-date-editor ::v-deep .el-input__inner {
						border-radius: 0px;
						padding: 0 10px 0 30px;
						box-shadow: 0px 0px 0px #a0d8db;
						outline: none;
						color: #999;
						background: #fff;
						width: 100%;
						font-size: 14px;
						border-color: #c4deee;
						border-width: 0 0 2px;
						border-style: dotted;
						height: 40px;
					}
		
		.rgs-form ::v-deep .el-upload--picture-card {
			background: transparent;
			border: 0;
			border-radius: 0;
			width: auto;
			height: auto;
			line-height: initial;
			vertical-align: middle;
		}
		
		.rgs-form ::v-deep .upload .upload-img {
		  		  border: 2px dotted #c4deee;
		  		  cursor: pointer;
		  		  border-radius: 0px;
		  		  color: #c4deee;
		  		  background: #Fff;
		  		  width: 160px;
		  		  font-size: 32px;
		  		  line-height: 80px;
		  		  text-align: center;
		  		  height: 80px;
		  		}
		
		.rgs-form ::v-deep .el-upload-list .el-upload-list__item {
		  		  border: 2px dotted #c4deee;
		  		  cursor: pointer;
		  		  border-radius: 0px;
		  		  color: #c4deee;
		  		  background: #Fff;
		  		  width: 160px;
		  		  font-size: 32px;
		  		  line-height: 80px;
		  		  text-align: center;
		  		  height: 80px;
		  		}
		
		.rgs-form ::v-deep .el-upload .el-icon-plus {
		  		  border: 2px dotted #c4deee;
		  		  cursor: pointer;
		  		  border-radius: 0px;
		  		  color: #c4deee;
		  		  background: #Fff;
		  		  width: 160px;
		  		  font-size: 32px;
		  		  line-height: 80px;
		  		  text-align: center;
		  		  height: 80px;
		  		}
	}
</style>
