package com.blitz.java_concurrency.threads_basics;

/**
 * Demo for the Join method and Daemon threads
 * Threads are of 2 types:
 * 1] User Threads
 * 2] Daemon Threads
 * JVM waits for the User threads to complete execution
 * JVM doesn't wait for the Daemon threads to complete execution and shuts down 
 * Daemon threads kills immediately when User threads exit
 */
public class JoinThreadDemo {
    public static void main(String[] args) {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    System.out.println(Thread.currentThread().getName() + ": " + (i + 1));
                    try {
                        Thread.sleep(100);
                    } catch(Exception exc) {
                        System.out.println(exc.getClass().getSimpleName() + " was triggered: " + exc.getMessage());
                    }
                }
            }
        }, "thread-1");
        
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    System.out.println(Thread.currentThread().getName() + ": " + (i + 1));
                    try {
                        Thread.sleep(100);
                    } catch(Exception exc) {
                        System.out.println(exc.getClass().getSimpleName() + " was triggered: " + exc.getMessage());
                    }
                }
            }
        }, "thread-2");

        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 25; i++) {
                    System.out.println(Thread.currentThread().getName() + ": " + (i + 1));
                    try {
                        Thread.sleep(100);
                    } catch(Exception exc) {
                        System.out.println(exc.getClass().getSimpleName() + " was triggered: " + exc.getMessage());
                    }
                }
            }
        }, "thread-3");
        
        Thread thread4 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 15; i++) {
                    System.out.println(Thread.currentThread().getName() + ": " + (i + 1));
                    try {
                        Thread.sleep(100);
                    } catch(Exception exc) {
                        System.out.println(exc.getClass().getSimpleName() + " was triggered: " + exc.getMessage());
                    }
                }
            }
        }, "thread-4");

            thread3.setDaemon(true);
            thread4.setDaemon(true);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        System.out.println(thread1.getName());
        System.out.println(thread1.getPriority());
        System.out.println(thread1.getState());
        System.out.println(thread1.isDaemon());
        try {
            thread1.join();
        } catch(Exception exc) {
            System.out.println(exc.getMessage());
        }

        System.out.println("completed execution");
    }
}
