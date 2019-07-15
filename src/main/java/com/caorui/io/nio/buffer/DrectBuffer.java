package com.caorui.io.nio.buffer;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class DrectBuffer {

    public static void main(String[] args) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            //輸入文件
            File text = new File("test.txt");
            fis = new FileInputStream(text);
            FileChannel channel = fis.getChannel();


            //輸出文件
            File file = new File("test2.txt");
            fos = new FileOutputStream(file);
            FileChannel channel1 = fos.getChannel();

            //声明一个buffer
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
            while (true){
                byteBuffer.clear();
                int read = channel.read(byteBuffer);//读文件到buffer
                if(read==-1){
                    break;
                }
                byteBuffer.flip();
                channel1.write(byteBuffer);
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
