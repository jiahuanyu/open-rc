<template>
  <div>
    <H264Player :deviceId="deviceId" :scale="scale"></H264Player>
  </div>
</template>

<script>
import H264Player from "../components/H264Player";
import Axios from "axios";

export default {
  mounted() {
    const deviceId = this.$route.query.deviceId;
    Axios.get(`http://127.0.0.1:8888/device/${deviceId}`)
      .then(response => {
        console.log(response.data);
        if (response.data) {
          this.deviceId = response.data.deviceId;
          this.scale = response.data.size.height / 720.0;
        }
      })
      .catch(error => {
        console.log(error);
      });
  },
  components: {
    H264Player
  },
  data() {
    return {
      deviceId: null,
      scale: 1
    };
  }
};
</script>
