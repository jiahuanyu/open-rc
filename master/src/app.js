'use strict'

process.on('uncaughtException', function (err) {
    console.log('Caught exception: ' + err);
});

const Koa = require("koa2");
const Router = require("koa-router");
const WebSocket = require("ws");
const app = new Koa();
const router = new Router();
const webSocketServerDevice = new WebSocket.Server({ port: 8888 });
const webSocketServerBrowser = new WebSocket.Server({ port: 8889 });

// {
//     id: xxxx,
//     deviceInfo: xxxxx,
//     connection: xxxx
// }

const devices = []

var deviceId = 0

webSocketServerDevice.on("connection", conn => {
    console.log("has device connect");

    conn.on("message", msg => {
        console.log(`收到 device 发来的消息 ${msg}`)
        if (typeof (msg) == "string") {
            const msgJson = JSON.parse(msg)
            const name = msgJson.name
            const data = msgJson.data
            if (name == "device_join") {
                const device = devices.find((item) => {
                    return item.connection == conn
                })
                console.log(`device = ${device}`)
                if (device) {
                    // 更新设备信息
                    device.deviceInfo = data
                    device.connection = conn
                } else {
                    // 新增设备
                    devices.push({ id: deviceId++, deviceInfo: data, connection: conn })
                }
                console.log(`当前链接 devices = ${devices.length}}`)
            }
        } else {
        }
    })

    conn.on("close", () => {
        console.log("has deveice close")
        const index = devices.findIndex((item) => {
            return item.connection == conn
        })
        console.log(`index = ${index}`)
        if (index != -1) {
            devices.splice(index, 1)
        }
    });
});

webSocketServerBrowser.on("connection", conn => {
    console.log("has brower connect");

    conn.on("message", msg => {

    })

    conn.on("close", () => {
        console.log("has browser close")
    });
});

router.get("/devices", ctx => {
    ctx.body = devices
})

router.get("/device/:id", ctx => {
    ctx.body = devices.find((item) => {
        return item.deviceInfo.id == ctx.params.id
    })
})

app.use((ctx, next) => {
    // 设置允许跨域
    ctx.set("Access-Control-Allow-Origin", "*");
    ctx.set("Access-Control-Allow-Methods", "PUT, POST, GET, DELETE, OPTIONS");
    // 请求头设置
    ctx.set(
        "Access-Control-Allow-Headers",
        "*"
    );
    if (ctx.method == "OPTIONS") {
        ctx.body = 200;
    } else {
        next();
    }
})
app.use(router.routes());

app.listen(3000);
