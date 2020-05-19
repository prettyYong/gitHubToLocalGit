package com.wy.thread;

import com.wy.Main;

public class GTThread implements Runnable {
    int g = 0;
    int t = 0;
    String who;
    private static String gui = "乌龟";
    private static String tu = "兔子";

    public GTThread(String who) {
        this.who = who;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            if (who.equals(tu)) {
                t++;
                System.out.println(tu + "跑到了：" + t);
                if (t == 10) {
                    System.out.println("兔子赢了---------");
                    break;
                }
            } else {
                g++;
                System.out.println(gui + "跑到了：" + g);
                if (g == 10) {
                    System.out.println("乌龟赢了---------");
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        new Thread(new GTThread(gui)).start();
        new Thread(new GTThread(tu)).start();


    }


}
