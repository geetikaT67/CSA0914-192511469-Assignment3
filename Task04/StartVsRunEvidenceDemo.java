package Task04;

/**
 * Task 04 Demonstration: Calling run() vs calling start().
 * Student A Requirement: Explain why calling run() instead of start() does not create a new thread, with evidence.
 */
public class StartVsRunEvidenceDemo {

    public static void main(String[] args) {
        System.out.println("Main Thread Details -> ID: " + Thread.currentThread().threadId()
                + ", Name: '" + Thread.currentThread().getName() + "'");

        Runnable task = () -> {
            Thread current = Thread.currentThread();
            System.out.println("  Inside Task Body -> Thread ID: " + current.threadId()
                    + ", Thread Name: '" + current.getName() + "'"
                    + ", Is Main Thread: " + current.getName().equals("main"));
        };

        // 1. Calling task.run() directly
        System.out.println("\n--- 1. Calling run() directly (NO NEW THREAD CREATED) ---");
        Thread directThread = new Thread(task, "DirectWorkerThread");
        directThread.run(); // Synchronous method call on caller thread!

        // 2. Calling task.start()
        System.out.println("\n--- 2. Calling start() (ASYNC NEW JVM THREAD SPAWNED) ---");
        Thread asyncThread = new Thread(task, "AsyncWorkerThread");
        asyncThread.start(); // Asks JVM to register with OS scheduler and run concurrently
        try {
            asyncThread.join();
        } catch (InterruptedException ignored) {}

        System.out.println("\nEVIDENCE CONCLUSION: directThread.run() executed synchronously inside 'main' thread.");
        System.out.println("Only asyncThread.start() allocated a new OS thread with a distinct thread ID and name.");
    }
}
