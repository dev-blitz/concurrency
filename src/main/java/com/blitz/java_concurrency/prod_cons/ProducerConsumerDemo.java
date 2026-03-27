package com.blitz.java_concurrency.prod_cons;

import java.util.ArrayList;
import java.util.List;

public class ProducerConsumerDemo {

  public static void main(String[] args) {
    ProducerConsumerImpl demo = new ProducerConsumerImpl();
    Thread producer = new Thread(() -> {
      demo.produce();
    }, "producer");

    Thread consumer = new Thread(new Runnable() {
      @Override
      public void run() {
        demo.consume();
      }
    }, "consumer");

    // producer.start();
    // consumer.start();

    OptimisedProducerConsumerImpl optimisedDemo = new OptimisedProducerConsumerImpl(3, 2);
    Thread optimisedProducer = new Thread(() -> {
      optimisedDemo.produce();
    }, "optimised-producer");

    Thread optimisedConsumer = new Thread(() -> {
      optimisedDemo.consume();
    }, "optimised-consumer");

    optimisedProducer.start();
    optimisedConsumer.start();
  }
}

class OptimisedProducerConsumerImpl {
  private final List<Integer> container;
  private final int sizeLimit;
  private final Object LOCK;
  private final int turnsLimit;
  private float turnsCounter;

  public OptimisedProducerConsumerImpl(int sizeLimit, int turnsLimit) {
    container = new ArrayList<Integer>();
    this.sizeLimit = sizeLimit;
    LOCK = new Object();
    this.turnsLimit = turnsLimit;
    turnsCounter = 0F;
  }

  protected void produce() {
    synchronized (LOCK) {
      int counter = 0;
      while (turnsCounter < turnsLimit) {
        if (container.size() < sizeLimit) {
          LOCK.notify();
          System.out.println(Thread.currentThread().getName() + "=> produced: " + ++counter);
          container.add(counter);
          try {
            Thread.sleep(100);
          } catch (InterruptedException exc) {
            System.out.println(
                Thread.currentThread().getName() + "=> interrupted-exception was triggered: " + exc.getMessage());
          } catch (Exception exc) {
            System.out.println(Thread.currentThread().getName() + "=> " + exc.getClass().getSimpleName()
                + " was triggered: " + exc.getMessage());
          }
        } else {
          System.out.println(Thread.currentThread().getName() + "=> container full... call consumer... ");
          turnsCounter += 0.5F;
          try {
            LOCK.wait();
          } catch (InterruptedException exc) {
            System.out.println(
                Thread.currentThread().getName() + "=> interrupted-exception was triggered: " + exc.getMessage());
          } catch (Exception exc) {
            System.out.println(Thread.currentThread().getName() + "=> " + exc.getClass().getSimpleName()
                + " was triggered: " + exc.getMessage());
          }
        }
      }
      LOCK.notifyAll();
    }
  }

  protected void consume() {
    synchronized (LOCK) {
      while (turnsCounter < turnsLimit) {
        if (container.size() > 0) {
          LOCK.notify();
          System.out.println(Thread.currentThread().getName() + "=> consumed: " + container.remove(0));
          try {
            Thread.sleep(100);
          } catch (InterruptedException exc) {
            System.out.println(
                Thread.currentThread().getName() + "=> interrupted-exception was triggered: " + exc.getMessage());
          } catch (Exception exc) {
            System.out.println(Thread.currentThread().getName() + "=> " + exc.getClass().getSimpleName()
                + " was triggered: " + exc.getMessage());
          }
        } else {
          System.out.println(Thread.currentThread().getName() + "=> container empty... call producer... ");
          turnsCounter += 0.5F;
          try {
            LOCK.wait();
          } catch (InterruptedException exc) {
            System.out.println(
                Thread.currentThread().getName() + "=> interrupted-exception was triggered: " + exc.getMessage());
          } catch (Exception exc) {
            System.out.println(Thread.currentThread().getName() + "=> " + exc.getClass().getSimpleName()
                + " was triggered: " + exc.getMessage());
          }
        }
      }
      LOCK.notifyAll();
    }
  }
}

class ProducerConsumerImpl {

  private final List<Integer> container;
  private final Object LOCK;
  private int counter;
  private final int sizeLimit;

  public ProducerConsumerImpl() {
    this.container = new ArrayList<Integer>();
    this.LOCK = new Object();
    this.counter = 0;
    this.sizeLimit = 3;
  }

  protected void produce() {
    synchronized (LOCK) {
      while (true) {
        if (container.size() < sizeLimit) {
          LOCK.notify();
          System.out.println("produced: " + ++counter + " . . .");
          container.add(counter);
          try {
            Thread.sleep(100);
          } catch (InterruptedException exc) {
            System.out.println("InterruptedException was triggered: " + exc.getMessage());
          } catch (Exception exc) {
            System.out.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
          }
        } else {
          System.out.println("container full:\tpass to consumer");
          try {
            LOCK.wait();
          } catch (InterruptedException exc) {
            System.out.println("InterruptedException was triggered: " + exc.getMessage());
          } catch (Exception exc) {
            System.out.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
          }
        }
      }
    }
  }

  protected void consume() {
    synchronized (LOCK) {
      while (true) {
        if (container.size() > 0) {
          LOCK.notify();
          System.out.println("consumed: " + container.remove(0) + " . . .");
          try {
            Thread.sleep(100);
          } catch (InterruptedException exc) {
            System.out.println("InterruptedException was triggered: " + exc.getMessage());
          } catch (Exception exc) {
            System.out.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
          }
        } else {
          System.out.println("container empty:\tpass to producer");
          try {
            LOCK.wait();
          } catch (InterruptedException exc) {
            System.out.println("InterruptedException was triggered: " + exc.getMessage());
          } catch (Exception exc) {
            System.out.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
          }
        }
      }
    }
  }
}
