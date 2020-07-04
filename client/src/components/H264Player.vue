<template>
  <div
    class="h264_player"
    style="float:left;position:relative;"
    @mousedown="mousedown"
    @mousemove="mousemove"
    @mouseup="mouseup"
  ></div>
</template>

<script>
export default {
  watch: {
    deviceId(newValue, oldValue) {
      console.log(newValue);
      if (!newValue) {
        return;
      }
      let player = new Player();
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
      this.mouseLastCoordinate.x = event.x;
      this.mouseLastCoordinate.y = event.y;
    },
    mousemove() {
      // if (
      //   this.mouseHasDown &&
      //   !this.swipeActionHappen &&
      //   (Math.abs(event.x - this.mouseLastCoordinate.x) > 8 ||
      //     Math.abs(event.y - this.mouseLastCoordinate.y) > 8)
      // ) {
      //   this.swipeActionHappen = true;
      // }
      // if (
      //   this.swipeActionHappen &&
      //   (Math.abs(event.x - this.mouseLastCoordinate.x) > 10 ||
      //     Math.abs(event.y - this.mouseLastCoordinate.y) > 10)
      // ) {
      //   this.webSokcetSend({
      //     name: "input_event",
      //     data: {
      //       type: 2,
      //       description: {
      //         swipeStartX: this.mouseLastCoordinate.x * this.scale,
      //         swipeStartY: this.mouseLastCoordinate.y * this.scale,
      //         swipeStopX: event.x * this.scale,
      //         swipeStopY: event.y * this.scale
      //       }
      //     }
      //   });
      //   this.mouseLastCoordinate.x = event.x;
      //   this.mouseLastCoordinate.y = event.y;
      // }
    },
    mouseup(event) {
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
      this.mouseHasDown = false;
      this.mouseLastCoordinate.x = 0;
      this.mouseLastCoordinate.y = 0;
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
      mouseHasDown: false,
      //
      mouseLastCoordinate: { x: 0, y: 0 },
      swipeActionHappen: false
    };
  }
};
</script>