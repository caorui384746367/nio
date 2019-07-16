package com.caorui.io.nio.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * nio服务端
 */
public class NIOServer {

    //服务的控制
    private Selector selector = null;
    //服务的端口
    private int port;
    //编码工具
    private Charset charset = Charset.forName("UTF-8");

    public NIOServer(int port) throws IOException {
        this.port = port;
        //打开channel通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //绑定端口
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);

        //控制器
        selector = Selector.open();

        //
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("服务已经启动，监听端口是：" + port);
    }

    /**
     * 监听
     */
    public void listen() throws IOException {
        while (true){
            //判断selector中是否雨哦对象
            int select = selector.select();
            if(select==0){
                continue;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while(iterator.hasNext()){
               SelectionKey key = iterator.next();
              //从集合中删除
                iterator.remove();
                pross(key);
            }
        }
    }

    public void pross(SelectionKey key) throws IOException {
        //判断key的类型
        if(key.isAcceptable()){//连接类型
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel client = serverSocketChannel.accept();
            client.configureBlocking(false);
            client.register(selector,SelectionKey.OP_READ);

            key.interestOps(SelectionKey.OP_ACCEPT);
            client.write(charset.encode("您好啊"));

        }else if(key.isReadable()){//读
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buf = ByteBuffer.allocate(1024);

            StringBuilder sb = new StringBuilder();
            try {
                while(channel.read(buf)>0){
                    buf.flip();
                    sb.append(charset.decode(buf));
                }
                //
                key.interestOps(SelectionKey.OP_READ);
            } catch (IOException e) {
                key.cancel();
                if(key.channel()!=  null){
                    key.channel().close();
                }
            }
            String content = sb.toString();
            if(content.length()>0){
                System.out.println(content);
            }

        }
    }

    public static void main(String[] args) {
        try {
            new NIOServer(8011).listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
