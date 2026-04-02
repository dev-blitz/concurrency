package com.blitz.java_concurrency.prod_cons;

import java.util.ArrayList;
import java.util.List;

/**
 * REVISION SUMMARY:
 * 1. JVM Exit: JVM stays alive as long as ONE non-daemon thread is running. 
 * Main is non-daemon. If Main is stuck in join(), the JVM won't stop.
 * 2. Join Logic: Must be Start -> Join. Join blocks the CALLER (Main).
 * 3. Stranded Threads: If a thread finishes its loop and doesn't notify, 
 * the other thread might be stuck in the 'Wait Set' forever.
 */
public class DaemonProducerConsumerDemo {
  public static void main(String[] args) throws Exception {
    final ProducerConsumer obj = new ProducerConsumer(6, 6);
    
    final Thread producer = new Thread(new ProducerRunnable(obj), "producer-thread");
    final Thread consumer = new Thread(() -> {
      obj.consume();
      System.out.println("memory-address of the object: " + obj.hashCode());
    }, "consumer-thread");

    /*
    * REVISION: Even though consumer is a Daemon, Main is NOT.
    * Main will wait at consumer.join() indefinitely if the consumer is stuck
    * in consumer.setDaemon(true);
    */

    consumer.setDaemon(true);
    /// TRAJECTORY: Start first to allow concurrent execution.
    producer.start();
    consumer.start();

    /// TRAJECTORY: Join second to ensure Main waits for completion.
    producer.join(); 
    consumer.join(); 
    
    System.out.println("hence verified, same-resource was being passed...");
  }
}

/**
 * REVISION: The Wrapper class that passes the shared 'obj' to the thread.
 * This ensures both threads operate on the exact same memory address.
 */
class ProducerRunnable implements Runnable {
  ProducerConsumer obj;
  
  /**
   * constructor method to create the producer-runnable
   * @param obj ProducerConsumer object from which the shared resource will be 
   * triggered by method-calls
   */
  public ProducerRunnable(ProducerConsumer obj) {
    this.obj = obj;
  }

  @Override
  public void run() {
    obj.produce();
    System.out.println("memory-address of the object: " + obj.hashCode());
  }
}

/**
 * below class provides the class with the shared resource
 * it also provides the methods using which we can 
 * add or remove items from the shared resource
 */
class ProducerConsumer {
  private final List<Integer> container;
  private final int containerSize;
  private final int turnsLimit;
  private int counter;
  private int turnsCounter;
  private final Object LOCK; 

  /**
   * constructor method for ProducerConsumer class
   * where we will have to provide the container-size and 
   * no. of turns we want
   * @param containerSize maximum container-size we need
   * @param turnsLimit maximum number of times this iteration should go on
   */
  public ProducerConsumer(int containerSize, int turnsLimit) {
    LOCK = new Object();
    counter = 0;
    turnsCounter = 0;
    this.containerSize = containerSize;
    this.turnsLimit = turnsLimit;
    container = new ArrayList<>();
  }

  /**
   * produce method to add elemets to the shared-resource
   */
  protected void produce() {
    synchronized(LOCK) {
      while (turnsCounter < turnsLimit) {
        if (container.size() < containerSize) {
          // NOTIFY: Wakes the Consumer if it was waiting for data.
          LOCK.notify(); 
          System.out.println("adding " + ++counter + " to the container");
          container.add(counter);
          System.out.println("produced: " + container.get(container.size() - 1));
          try {
            Thread.sleep(100);
          } catch (InterruptedException exc) {
            Thread.currentThread().interrupt();
          }
        } else {
          System.out.println("container FULL \tpass to consumer...");
          turnsCounter += 1;
          try {
            // WAIT: Producer releases LOCK and enters the 'Wait Set'.
            LOCK.wait(); 
          } catch (InterruptedException exc) {
            Thread.currentThread().interrupt(); 
          }
        }
      }
      // REVISION: If we add notify() here, it ensures the Consumer 
      // isn't left sleeping when the Producer finishes all turns.
      LOCK.notify();
    }
  }

  /**
   * consume method to remove elemets from the shared resource
   */
  protected void consume() {
    synchronized(LOCK) {
      while (turnsCounter < turnsLimit) {
        if (container.size() > 0) {
          // NOTIFY: Wakes the Producer if it was waiting for space.
          LOCK.notify();
          System.out.println("consumed: " + container.remove(0));
          try {
            Thread.sleep(100);
          } catch (InterruptedException exc) {
            Thread.currentThread().interrupt();
          }
        } else {
          System.out.println("container EMPTY \tpass to producer...");
          turnsCounter += 1;
          try {
            LOCK.wait();
          } catch (InterruptedException exc) {
            Thread.currentThread().interrupt();
          }
        }
      }
      // THE SAFETY FLARE: Wakes up anyone (Producer) still in the Wait Set 
      // before this thread finishes and dies.
      LOCK.notify(); 
    }
  }
}

