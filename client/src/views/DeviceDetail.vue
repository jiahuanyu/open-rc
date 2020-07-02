<template>
  <div>
    <H264Player :ip="ip"></H264Player>
  </div>
</template>

<script>
import H264Player from "../components/H264Player";
import Axios from "axios";

export default {
  mounted() {
    Axios.get("http://127.0.0.1:3000/device/1")
      .then(response => {
        console.log(response.data);
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
      ip: "10.10.1.228:8887"
    };
  }
};
</script>
