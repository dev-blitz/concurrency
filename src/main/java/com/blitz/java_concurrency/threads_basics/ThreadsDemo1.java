package com.blitz.java_concurrency.threads_basics;

public class ThreadsDemo1 {
    public static void main(String[] args) {
        /*
         * two ways to create threads:
         * 1. by implementing Runnable interface and create a Thread object with runnable instance
         * 2. by extending Threads class to create a thread object
         */

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    System.out.println("i: " + i);
                    try {
                        Thread.sleep(100);
                    } catch(Exception exp) {
                        System.out.println(exp.getClass().getSimpleName() + " was triggered => " + exp.getMessage());
                    }
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

        Thread custom = new CustomThread();
        custom.start();
    }

    private static class CustomThread extends Thread {
        @Override
        public void run() {        
            for (int i = 0; i < 5; i++) {
                System.out.println("custom-i: " + (i + 1));
                try {
                    Thread.sleep(100);
                } catch(Exception exp) {
                    System.out.println(exp.getClass().getSimpleName() + " was triggered => " + exp.getMessage());
                }
            }
        }
    }
}
