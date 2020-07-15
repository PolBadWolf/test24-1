package org.example.lib;

public abstract class MyThread extends Thread {
    private boolean stopped = false;
    private boolean suspended = false;

    @Override
    public void run() {
        while (!stopped && isAlive()) {
            if (suspended) {
                Thread.yield();
                continue;
            }
            cycle();
        }
        endThread();
    }

    final public void Close() {
        stopped = true;
        while (!isAlive()) {
            try {
                Thread.sleep(10);
                join(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    final public void Suspended() {
        suspended = true;
    }

    final public void Resume() {
        suspended = false;
    }

    protected abstract void cycle();

    protected abstract void endThread();
}
