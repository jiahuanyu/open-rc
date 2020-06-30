<template>
  <div>
    <H264Player :ip="ip"></H264Player>
  </div>
</template>

<script>
import H264Player from "../components/H264Player";
// "ws://172.16.11.71:8887"
export default {
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
