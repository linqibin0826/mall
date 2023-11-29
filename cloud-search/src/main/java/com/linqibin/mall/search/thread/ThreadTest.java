package com.linqibin.mall.search.thread;


public class ThreadTest {


    public static void main(String[] args) {
        System.out.println("main thread start...");
        new Thread01().start();
        System.out.println("main thread end...");
    }

    public static class Thread01 extends Thread {

        @Override
        public void run() {
            System.out.println("new Thread start");
            System.out.println("1 + 1 = " + 1 + 1);
            System.out.println("new Thread end");
        }
    }


}
