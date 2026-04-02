package com.blitz.java_concurrency.prod_cons;

import java.util.ArrayList;
import java.util.List;

/**
 * below class demonstrates Deadlock situation in the Producer Consumer problem
 * Deadlock situation is created where all the threads are waiting and there is none active
 * which results the JVM to be stuck infinenitely
 */
public class DeadLockProducerConsumerDemo {
  public static void main(String[] args) {
    final SharedResource resource = new SharedResource(3, 3);
    final MyProducerRunnable myRunnable = new MyProducerRunnable(resource);
    Thread producer = new Thread(myRunnable, "producer-thread");
    Thread consumer = new Thread(()->{
      try {
        resource.consume();
      } catch (InterruptedException exc) {
        System.out.println("interrupted-exception was triggered due to: " + exc.getMessage());
      } catch (Exception exc) {
        System.out.println(exc.getClass().getSimpleName() + " was triggered: " + exc.getMessage());
      }
    }, "consumer-thread");

    producer.setDaemon(true);
    producer.start();
    consumer.start();

    try {
      producer.join();
      consumer.join();
    } catch (InterruptedException exc) {
      System.out.println("interrupted-exception was triggered due to: " + exc.getMessage());
    } catch (Exception exc) {
      System.out.println(exc.getClass().getSimpleName() + " was triggered: " + exc.getMessage());
    }
    System.out.println("demo successfully completed");
  }
}

/**
 * this class in the implementation of <em>Runnable</em> interface
 */
class MyProducerRunnable implements Runnable {
  SharedResource resource;
  /**
   * constructer method for MyProducerRunnable class
   * @param resource SharedResource class Object
   */
  public MyProducerRunnable(SharedResource resource) {
    this.resource = resource;
  }

  @Override
  public void run() {
    try {
      resource.produce();
    } catch (InterruptedException exc) {
      System.out.println("interrupted-exception was triggered due to: " + exc.getMessage());
    } catch (Exception exc) {
      System.out.println(exc.getClass().getSimpleName() + " was triggered: " + exc.getMessage());
    }
  }
}

/**
 * this class is shared across both the threads, it will provide the object which is
 * actually acting as the shared-resource to re-create the Producer Consumer problem
 */
class SharedResource {
  private final List<Integer> container;
  private int count;
  private final Object LOCK;
  private final int size;
  private final int turns;
  private int turnsCounter;

  /**
   * constructer call to initialize the SharedResource objects
   * @param size threshold size of the container
   * @param turns number of turns we want to see the producer consumer threads to co-ordinate
   */
  public SharedResource(int size, int turns) {
    container = new ArrayList<Integer>();
    count = 0;
    LOCK = new Object();
    this.size = size;
    this.turns = turns;
    turnsCounter = 0;
  }

  /**
   * constructer call to initialize the SharedResource objects
   */
  public SharedResource() {
    container = new ArrayList<Integer>();
    count = 0;
    LOCK = new Object();
    size = 6;
    turns = 3;
    turnsCounter = 0;
  }

  /**
   * produce method to be utilised by the producer-thread to add elements to the container
   * @throws InterruptedException
   */
  protected void produce() throws InterruptedException {
    synchronized(LOCK) {
      while (turnsCounter < turns) {
        if (container.size() < size) {
          System.out.println(Thread.currentThread().getName() + "=> adding to the container: " + ++count);
          container.add(count);
          Thread.sleep(100);
          LOCK.notify();
        } else {
          LOCK.wait();
          turnsCounter += 1;
          System.out.println("container full...\npass to consumer");
        }
      }
    }
  }

  /**
   * consume method to be utilised by the consumer-thread to remove elements to the container
   * @throws InterruptedException
   */
  protected void consume() throws InterruptedException {
    synchronized(LOCK) {
      while (turnsCounter < turns) {
        if (container.size() > 0) {
          System.out.println(Thread.currentThread().getName() + "=> consumed from the container: " + container.remove(0));
          Thread.sleep(100);
          LOCK.notify();
        } else {
          LOCK.wait();
          turnsCounter += 1;
          System.out.println("container empty...\npass to producer");
        }
      }
    }
  }
}

