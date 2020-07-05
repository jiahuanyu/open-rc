<template>
  <div class="h264_player"></div>
</template>

<script>
export default {
  watch: {
    deviceId(newValue, oldValue) {
      if (this.webSocket) {
        this.webSocket.close();
      }
      if (!newValue) {
        return;
      }
      let player = new Player();

      player.canvas.addEventListener("mousedown", this.mousedown);
      player.canvas.addEventListener("mousemove", this.mousemove);
      player.canvas.addEventListener("mouseup", this.mouseup);

      document
        .getElementsByClassName("h264_player")[0]
        .appendChild(player.canvas);

      this.webSocket = new WebSocket(
        `ws://127.0.0.1:8888/ws/client?deviceId=${newValue}`
      );

      this.webSocket.binaryType = "arraybuffer";
      this.webSocket.onopen = function() {};

      this.webSocket.onmessage = function(msg) {
        console.log(`onmessage = ${msg}`);
        var messageData = new Uint8Array(msg.data);
        player.decode(messageData);
      };

      this.webSocket.onclose = function() {
        console.log("onclose");
      };
    }
  },
  methods: {
    webSokcetSend(obj) {
      if (this.webSocket) {
        console.log(`socket 发送 ${JSON.stringify(obj)}`);
        this.webSocket.send(JSON.stringify(obj));
      }
    },
    mousedown(event) {
      this.webSokcetSend({
        name: "input_event",
        data: {
          action: 1,
          description: {
            coordinate: {
              x: event.x * this.scale,
              y: event.y * this.scale
            }
          }
        }
      });
      this.mouseHasDown = true;
    },
    mousemove() {
      if (this.mouseHasDown) {
        this.webSokcetSend({
          name: "input_event",
          data: {
            action: 2,
            description: {
              coordinate: {
                x: event.x * this.scale,
                y: event.y * this.scale
              }
            }
          }
        });
      }
    },
    mouseup(event) {
      this.webSokcetSend({
        name: "input_event",
        data: {
          action: 3,
          description: {
            coordinate: {
              x: event.x * this.scale,
              y: event.y * this.scale
            }
          }
        }
      });
      this.mouseHasDown = false;
    }
  },
  destroyed() {
    if (this.webSocket) {
      this.webSocket.close();
    }
  },
  props: ["deviceId", "scale"],
  data() {
    return {
      // WebSocket 链接
      webSocket: null,
      mouseHasDown: false
    };
  }
};
</script>