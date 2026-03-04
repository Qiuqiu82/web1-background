<template>
  <div id="home-chart" style="width:100%;height:400px;"></div>
</template>
<script>
export default {
  mounted() {
    this.homeChart();
  },
  methods: {
    homeChart() {
      // 基于准备好的dom，初始化echarts实例
      var myChart = this.$echarts.init(document.getElementById("home-chart"));
      // 指定图表的配置项和数�?
      var option = {
        tooltip: {
          trigger: "axis"
        },
        legend: {
          data: ["访问�?, "用户�?, "收入"]
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true
        },
        xAxis: {
          type: "category",
          boundaryGap: false,
          data: [
            "1�?,
            "2�?,
            "3�?,
            "4�?,
            "5�?,
            "6�?,
            "7�?,
            "8�?,
            "9�?,
            "10�?,
            "11�?,
            "12�?
          ]
        },
        yAxis: {
          type: "value"
        },
        series: [
          {
            name: "访问�?,
            type: "line",
            stack: "总量",
            data: [
              120,
              132,
              101,
              134,
              90,
              230,
              210,
              120,
              132,
              101,
              134,
              90,
              230
            ]
          },
          {
            name: "用户�?,
            type: "line",
            stack: "总量",
            data: [
              220,
              182,
              191,
              234,
              290,
              330,
              310,
              182,
              191,
              234,
              290,
              330,
              310
            ]
          },
          {
            name: "收入",
            type: "line",
            stack: "总量",
            data: [
              150,
              232,
              201,
              154,
              190,
              330,
              410,
              232,
              201,
              154,
              190,
              330,
              410
            ]
          }
        ]
      };
      // // 使用刚指定的配置项和数据显示图表�?
      myChart.setOption(option);
      //根据窗口的大小变动图�?
      window.onresize = function() {
        myChart.resize();
      };
    }
  }
};
</script>
<style lang="scss" scoped>
#home-chart {
  background: #ffffff;
  padding: 20px 0;
}
</style>
