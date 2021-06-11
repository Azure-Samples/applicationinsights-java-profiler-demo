package com.example.demo;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class MemorySink implements Runnable {

    public ArrayList<long[]> sink;

    public MemorySink() {

    }

    @Override
    public void run() {
        sink = new ArrayList<>(125);
        for (int i = 0; i < 0x7ff; i++) {
            sink.add(new long[1024 * 32]);
            try {
                sleep(40);
            } catch (InterruptedException e) {}
        }
        long index = 0;
        for (int i = 0; i < 400000; i++) {
            System.out.println((int) (0x7ff & index));
            sink.set((int) (0x7ff & index++), new long[1024 * 32]);
            try {
                sleep(75);
            } catch (InterruptedException e) {}
        }
    }
}
