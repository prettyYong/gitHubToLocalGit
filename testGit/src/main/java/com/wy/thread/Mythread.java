package com.wy.thread;

import org.hibernate.validator.internal.util.stereotypes.ThreadSafe;

public class Mythread extends Thread {
    static int uu = 0;
    private String msg;

    public Mythread(String msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        synchronized (Mythread.class) {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.currentThread().sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                uu++;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Object obj=null;

        Thread thread1 = new Mythread("我是线程1");
        Thread thread2 = new Mythread("我是线程2");
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        //Thread.currentThread().sleep(1000);
        System.out.println("uu==========================" + uu);
    }
}
