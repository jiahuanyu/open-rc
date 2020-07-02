<template>
  <el-table :data="devicesData" border>
    <el-table-column prop="id" label="ID"></el-table-column>
    <el-table-column prop="model" label="手机型号"></el-table-column>
    <el-table-column prop="manufacturer" label="制造厂商"></el-table-column>
    <el-table-column prop="os" label="操作系统"></el-table-column>
    <el-table-column prop="osVersion" label="操作系统版本"></el-table-column>
    <el-table-column fixed="right" label="操作" width="100">
      <template slot-scope="scope">
        <el-button @click="remoteControlClicked(scope.row)" type="text" size="small">查看</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
import Axios from "axios";

export default {
  mounted() {
    Axios.get("http://127.0.0.1:3000/devices")
      .then(response => {
        console.log(response.data);
        this.devicesData = response.data.map(item => {
          return Object.assign(item.deviceInfo, { id: item.id });
        });
      })
      .catch(error => {
        console.log(error);
      });
  },
  methods: {
    remoteControlClicked(data) {
      console.log(data);
      this.$router.push({
        name: "DeviceDetail",
        query: { id: data.id }
      });
    }
  },
  data() {
    return {
      devicesData: []
    };
  }
};
</script>