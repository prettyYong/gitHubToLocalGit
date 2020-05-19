package com.wy.thread;

public class MyRunnable implements  Runnable {
    @Override
    public void run() {
        System.out.println("我是实现runnable接口的");
    }

    public static void main(String[] args) {
        MyRunnable myrun = new MyRunnable();
        Thread thread1=new Thread(myrun);
        Thread thread2=new Thread(myrun);
        Thread thread3=new Thread(myrun);
        Thread thread4=new Thread(myrun);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }
}
