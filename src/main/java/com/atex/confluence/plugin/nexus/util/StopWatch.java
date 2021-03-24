package com.atex.confluence.plugin.nexus.util;

/**
 * StopWatch
 *
 * @author mnova
 */
public class StopWatch {

    private long start;
    private long stop;

    public StopWatch() {
        this.start = 0;
        this.stop = 0;
    }

    public StopWatch start() {
        this.start = System.currentTimeMillis();
        this.stop = this.start;
        return this;
    }

    public StopWatch stop() {
        this.stop = System.currentTimeMillis();
        return this;
    }

    public long elapsed() {
        return stop - start;
    }

    public String elapsed(final String msg) {
        return msg + " took " + elapsed() + " ms";
    }

    public static StopWatch started() {
        return new StopWatch().start();
    }

}
