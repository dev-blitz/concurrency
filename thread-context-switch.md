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

### Atomic Variables: The Optimistic Approach

* Atomic variables use CAS (Compare-And-Swap). This is a hardware-level instruction (like LOCK CMPXCHG on x86).

Instead of locking the door, a thread does the following:

Read: "I see the value is 10."

Calculate: "I want it to be 11."

  * CAS Instruction: "Hey CPU, IF the value is still 10, change it to 11. If it's NOT 10 anymore, don't do anything and tell me I failed."

* Why it's faster:

  * It happens entirely in User Mode. No Kernel transition.

  * The thread never "sleeps." If it fails, it just tries again in a fast loop (spinning). This is called Lock-Free programming.

### real cost

1. Flushing the TLB (Translation Lookaside Buffer)

The TLB is a high-speed cache that stores the mapping between virtual memory addresses (what the program sees) and physical memory addresses (where the data actually lives in RAM).

The Problem: Every process has its own unique virtual-to-physical mapping.

The Cost: When the OS switches to a thread in a different process, the old mappings in the TLB become invalid. The CPU must "flush" (clear) the TLB.

The Result: For a while after the switch, every memory access is slower because the CPU has to manually look up the physical address in the page tables instead of hitting the high-speed TLB.


2. Cache Misses (Cold Cache)

Modern CPUs rely heavily on L1, L2, and L3 caches to keep data close to the execution cores. These caches are populated based on the principle of locality (the data you just used or are about to use).

The Problem: Thread A was running and had filled the L1 cache with its specific "working set" of data.

The Cost: When Thread B takes over, the cache is "cold" for Thread B. As Thread B tries to work, it experiences a wave of cache misses, forcing the CPU to fetch data from the much slower main RAM.

The Result: Even if the context switch itself only takes a few microseconds, the program runs at a "limp" for thousands of cycles afterward until the cache is refilled with the new thread's data.


3. Executing the OS Scheduler

The scheduler is a piece of software within the Operating System kernel. It isn't "magic"—it’s code that needs CPU time to run.

The Problem: To decide which thread should run next, the scheduler has to:

Manage complex data structures (like priority queues).

Check which threads are blocked by locks.

Calculate which thread has been waiting the longest or has the highest priority.

The Cost: This is pure overhead. Every cycle the CPU spends running the scheduler is a cycle it isn't spending on your actual application logic.

Summary Table: The Performance Hit

Factor	Primary Impact	Duration of Impact
Direct Overhead	Saving/Loading Registers	Immediate (Short)
Scheduler Logic	CPU cycles spent on OS code	Immediate (Medium)
TLB Flush	Slower memory address translation	Lingering (until refilled)
Cache Pollution	Massively increased latency for data access	Longest (until "warm")
This is why high-performance systems (like game engines or high-frequency trading platforms) try to be "lock-free". They want to avoid the "Cold Cache" and "TLB Flush" that come with yielding the CPU, as those costs are often much higher than the lock itself.
