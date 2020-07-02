<template>
  <div class="h264_player"></div>
</template>

<script>
export default {
  mounted() {
    let player = new Player();
    document
      .getElementsByClassName("h264_player")[0]
      .appendChild(player.canvas);

    let webSocket = new WebSocket(`ws://${this.ip}`);

    webSocket.binaryType = "arraybuffer";
    webSocket.onopen = function() {
      console.log("onopen");
      // onOpen 后的第一个数据表示的是设备信息
      webSocket.send("hello");
    };

    webSocket.onmessage = function(msg) {
      console.log(`onmessage = ${msg}`);
      var messageData = new Uint8Array(msg.data);
      player.decode(messageData);
    };

    webSocket.onclose = function() {
      console.log("onclose");
    };
  },
  props: ["ip"]
};
</script>