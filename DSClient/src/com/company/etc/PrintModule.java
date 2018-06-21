package com.company.etc;

/**
 * Created by SangwooSong on 5/9/18.
 */
public class PrintModule {
    public static boolean isPrinting = true;
    public static void print(String msg){
        if(isPrinting) System.out.println(msg);
    }
    public static void print2(String msg){
        System.out.println(msg);
    }
}
