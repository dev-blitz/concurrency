package com.blitz.java_concurrency.threads_basics;

/**
 * <p>
 * <em><strong>race condition</strong> only happens if 
 * a <strong>shared-resource</strong> is involved</em>
 * </p>
 * this clas demonstrates the example of race-condition in java
 * @author blitz
 */
public class RaceConditionDemo {
    public static void main(String[] args) {
        // RACE-CONDITION shared resource
        MyRunnable runnable = new MyRunnable();
        Thread thread1 = new Thread(runnable, "first-thread");
        Thread thread2 = new Thread(runnable, "second-thread");

        thread1.start();
        thread2.start();
       
        // perfect scenario, separate resources
        MyRunnable runnable3 = new MyRunnable();
        MyRunnable runnable4 = new MyRunnable();
        Thread thread3 = new Thread(runnable3, "third-thread");
        Thread thread4 = new Thread(runnable4, "fourth-thread");

        thread3.start();
        thread4.start();
    }
}

/**
 * class implementing the Runnable to create threads
 */
class MyRunnable implements Runnable {
    public int count; // this is the shared-resource
    
    /**
     * constructor method for the class
     */
    public MyRunnable() {
        count = 0;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1_000_000; i++) {
            count++;
        }
        System.out.println(Thread.currentThread().getName() + " current-val: " + count +'\n' + this + "\n" + this.getClass().getSimpleName() + '\n');
    }
}
