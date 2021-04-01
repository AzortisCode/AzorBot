package com.azortis.azorbot.util;

public class ExecutionTimer {
    private long start;
    private long end;

    /**
     * Creates a new execution timer that immediately starts running
     */
    public ExecutionTimer() {
        reset();
        start = System.currentTimeMillis();
    }

    /**
     * Stops the execution timer
     */
    public void end() {
        end = System.currentTimeMillis();
    }

    /**
     * Stops the execution timer
     */
    public void stop() {
        end = System.currentTimeMillis();
    }

    /**
     * Gets the duration
     * @return duration in MS
     */
    public long durationLong() {
        return (end - start);
    }

    /**
     * Build duration string
     * @return String with duration in format
     */
    public String duration() {
        return duration("command");
    }

    /**
     * Build duration string with command name
     * Running `command` took `###`ms
     * @param command Name of the command used in format
     * @return String with duration in format
     */
    public String duration(String command){
        return "Running " + command + " took: `" + durationLong() + "`ms";
    }

    /**
     * Resets the timer
     */
    public void reset() {
        start = 0;
        end = 0;
    }
}
