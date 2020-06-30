'use strict'

const Koa = require("koa2");
const Router = require("koa-router");
const WebSocket = require('ws');
const app = new Koa();
const router = new Router();
const ws = new WebSocket.Server({ port: 8888 });

ws.on('connection', ws => {
    console.log('server connection');

    ws.on('message', msg => {
        console.log('server receive msg：', msg);
    });

    ws.send('Information from the server');
});


router.get('/', ctx => {
    ctx.body = "哈哈哈"
})
app.use(router.routes());

app.listen(3000);