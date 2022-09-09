package com.kcb.pay.common;

import java.io.*;

public class StreamUtil {
    private static final int DEFAULT_SIZE = 8192;

    public static void io(InputStream in , OutputStream out){

    }

    public static void io(InputStream in,OutputStream out , int bufferSize) throws IOException {
        if(bufferSize == -1){
            bufferSize = DEFAULT_SIZE;
        }
        byte[] buffer = new byte[bufferSize];
        int amount;

        while((amount = in.read(buffer)) >= 0){
            out.write(buffer,0,amount);
        }
    }

    public static void io(Reader in , Writer out){

    }

    public static void io(Reader in,Writer out , int bufferSize) throws IOException {
        if(bufferSize == -1){
            bufferSize = DEFAULT_SIZE >> 1;
        }
        char[] buffer = new char[bufferSize];
        int amount;

        while((amount = in.read(buffer)) >= 0){
            out.write(buffer,0,amount);
        }
    }

    public static String readText(InputStream in) throws IOException {
        return readText(in,null,-1);
    }

    public static String readText(InputStream in,String encoding ,int bufferSize) throws IOException {
        Reader reader = (encoding ==null ? new InputStreamReader(in) : new InputStreamReader(in
                ,encoding));
        return readText(reader,-1);
    }

    public static String readText(Reader reader ,int bufferSize) throws IOException {
        StringWriter writer =new StringWriter();

        io(reader,writer,bufferSize);
        return writer.toString();
    }
}
