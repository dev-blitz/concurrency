# thread-context-switching

1. Thread Context Switching: Why is it expensive?

When a thread cannot acquire a lock, the OS puts it to sleep so another thread can use the CPU. This is the "Context Switch."

The State Save: The CPU must take a "snapshot" of the current thread's registers, program counter, and stack pointer and save them to memory.

The Kernel Transition: Switching threads requires the CPU to move from User Mode to Kernel Mode. Crossing this boundary is slow.

The Cache Pollution (The Real Killer): This is the most important point for HFT. When a new thread starts, the CPU's L1/L2 caches are full of the old thread's data. The new thread faces "cold" caches and suffers a massive wave of Cache Misses as it fetches its own data from slow RAM.

2. How Locks Work (The Pessimistic Approach)

Locks are pessimistic. They assume that a collision will happen, so they "lock the door" before doing any work.

Synchronized/ReentrantLock: If the lock is held, the requesting thread is often blocked. It enters a waiting queue managed by the OS.

The Cost: Because the OS is involved, a blocked thread might stay unscheduled for milliseconds—an eternity in a low-latency system.

## Atomic Variables: The Optimistic Approach

* Atomic variables use CAS (Compare-And-Swap). This is a hardware-level instruction (like LOCK CMPXCHG on x86).

Instead of locking the door, a thread does the following:

Read: "I see the value is 10."

Calculate: "I want it to be 11."

  * CAS Instruction: "Hey CPU, IF the value is still 10, change it to 11. If it's NOT 10 anymore, don't do anything and tell me I failed."

* Why it's faster:

  * It happens entirely in User Mode. No Kernel transition.

  * The thread never "sleeps." If it fails, it just tries again in a fast loop (spinning). This is called Lock-Free programming.
