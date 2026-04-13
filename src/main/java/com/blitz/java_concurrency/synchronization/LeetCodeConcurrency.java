package com.blitz.java_concurrency.synchronization;

public class LeetCodeConcurrency {
  public static void main(String[] args) {
    SharedResource resource = new SharedResource();

    Thread first = new Thread(()->{
      resource.printFirst();
    }, "first-thread");
    
    Thread second = new Thread(()->{
      resource.printFirst();
    }, "second-thread");
    
    Thread third = new Thread(()->{
      resource.printFirst();
    }, "third-thread");

    try {
      first.join();
      second.join();
      third.join();
    } catch (Exception exc) {
      System.out.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
    }

    first.start();
    third.start();
    second.start();
    
    System.out.println(resource.builder.toString());
  }
}

final class SharedResource {
  protected volatile StringBuilder builder;
  private final Object LOCK;
  private boolean one;
  private boolean two;

  public SharedResource() {
    builder = new StringBuilder("");
    one = false;
    two = false;
    LOCK = new Object();
  }

  protected void printFirst() {
    synchronized(LOCK) {
      LOCK.notifyAll();
      builder.append("first");
      one = true;
      try {
        LOCK.wait();
      } catch (Exception exc) {
        System.out.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
      }
    }
  }

  protected void printSecond() {
    synchronized(LOCK) {
      while (one) {
        LOCK.notifyAll();
        builder.append("second");
        two = true;
        try {
          LOCK.wait();
        } catch (Exception exc) {
          System.out.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
        }      
      }
    }
  }
  
  protected void printThird() {
    synchronized(LOCK) {
      while (two) {
        LOCK.notifyAll();
        builder.append("third");
        try {
          LOCK.wait();
        } catch (Exception exc) {
          System.out.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
        }      
      }
    }
  }
}
