package org.main;

public class Sandbox {
    static int number = 0;
    public Sandbox() {
        var t1 = new Thread(increment());
        var t2 = new Thread(increment());

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Final number value: " + number);
    }

    private static Runnable increment() {
        return () -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                number++;
            }
        };
    }

    public static void main(String[] args) {
        new Sandbox();
    }
}
