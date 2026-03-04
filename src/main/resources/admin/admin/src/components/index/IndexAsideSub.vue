<template>
  <el-submenu v-if="menu.list && menu.list.length >= 1" :index="menu.menuId + ''">
    <template slot="title">
      <span>{{ menu.name }}</span>
    </template>
    <sub-menu
      v-for="item in menu.list"
      :key="item.menuId"
      :menu="item"
      :dynamicMenuRoutes="dynamicMenuRoutes"
    ></sub-menu>
  </el-submenu>
  <el-menu-item v-else :index="menu.menuId + ''" @click="gotoRouteHandle(menu)">
    <span>{{ menu.name }}</span>
  </el-menu-item>
</template>

<script>
import SubMenu from "./IndexAsideSub";
export default {
  name: "sub-menu",
  props: {
    menu: {
      type: Object,
      required: true
    },
    dynamicMenuRoutes: {
      type: Array,
      required: true
    }
  },
  components: {
    SubMenu
  },
  methods: {
    // 通过menuId与动�?菜单)路由进行匹配跳转至指定路�?
    gotoRouteHandle(menu) {
      var route = this.dynamicMenuRoutes.filter(
        item => item.meta.menuId === menu.menuId
      );
      if (route.length >= 1) {
        if (route[0].component != null) {
          this.$router.replace({ name: route[0].name });
        } else {
          this.$router.push({ name: "404" });
        }
      }
    }
  }
};
</script>
