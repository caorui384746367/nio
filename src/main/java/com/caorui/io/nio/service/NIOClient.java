package com.caorui.io.nio.service;

import com.sun.org.apache.bcel.internal.generic.InstructionConstants;

import java.io.IOException;
import java.net.ContentHandler;
import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.security.Key;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

/**
 * nio客户端
 */
public class NIOClient {

    private int port = 8011;

    private Selector selector = null;

    private SocketChannel client;

    private Charset charset = Charset.forName("UTF-8");
    public NIOClient() throws IOException {
        //
        client  = SocketChannel.open(new InetSocketAddress("localhost",port));
        client.configureBlocking(false);
        selector = Selector.open();

        client.register(selector, SelectionKey.OP_READ);
        System.out.println("客户端启动完成:"+port);
    }

    public void listen(){
        Thread wThread = new Thread(){
            public void run(){
                while (true) {
                    try {
                        int select = selector.select();
                        if(select==0) continue;
                        Set<SelectionKey> selectionKeys = selector.selectedKeys();
                        Iterator<SelectionKey> iterator = selectionKeys.iterator();
                        while(iterator.hasNext()){
                            SelectionKey selectionKey = (SelectionKey)iterator.next();
                            iterator.remove();
                            pross(selectionKey);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        Thread rThread = new Thread(){
          public void run(){
              Scanner can = new Scanner(System.in);
              System.out.println("交流开始");
              while (true){
                  String content = can.nextLine();
                  try {
                      client.write(charset.encode(content));
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              }
          }
        };
        wThread.start();
        rThread.start();
    }



    public void pross(SelectionKey key){
        if(key.isReadable()){
            System.out.println("有消息请您接受");
        }
    }

    public static void main(String[] args) {
        try {
            new NIOClient().listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
