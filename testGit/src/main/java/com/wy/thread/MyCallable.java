package com.wy.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class MyCallable implements Callable<String> {
    @Override
    public String call() throws Exception {
        return "我是callable线程";
    }

    public static void main(String[] args) {
        MyCallable mycal=new MyCallable();
        FutureTask<String> futureTask=new FutureTask<>(mycal);
        Thread thread=new Thread(futureTask);
        thread.start();
        String result;
        try {
            result=futureTask.get();
            System.out.println(result);
        }catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
