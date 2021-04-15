package com.azortis.azorbot.cocoUtil;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CocoTimer {
    private static final List<CocoTimer> timers = new ArrayList<>();
    private long start;
    private long end;
    @Getter
    private long ID;

    /**
     * Creates a new execution timer that immediately starts running
     * <p>This is not added to the list of timers stored</p>
     */
    public CocoTimer(){
        reset();
        start = System.currentTimeMillis();
    }

    /**
     * Creates a new execution timer that immediately starts running
     * @param ID The ID used for querying timers
     */
    public CocoTimer(long ID){
        reset();
        start = System.currentTimeMillis();
        this.ID = ID;
        timers.add(this);
    }

    /**
     * Stops the execution timer
     */
    public void stop(){
        end = System.currentTimeMillis();
    }

    /**
     * Stops the execution timer
     * @param ID ID to query for timer
     */
    public void stop(long ID){
        Objects.requireNonNull(findTimer(ID)).stop();
    }

    /**
     * Gets the duration
     * @return duration in MS
     */
    public long durationLong(){
        return (end - start);
    }

    /**
     * Gets the duration
     * @param ID ID to query for timer
     * @return duration in MS
     */
    public static long durationLong(long ID){
        return Objects.requireNonNull(findTimer(ID)).durationLong();
    }

    /**
     * Build duration string with command name
     * <p>Running `command` took `###`ms</p>
     * @return String with duration in format
     */
    public String duration(){
        return duration("command");
    }

    /**
     * Build duration string with command name
     * <p>Running `command` took `###`ms</p>
     * @param ID ID to query for timer
     * @return String with duration in format
     */
    public static String duration(long ID){
        return Objects.requireNonNull(findTimer(ID)).duration();
    }

    /**
     * Build duration string with command name
     * <p>Running `command` took `###`ms</p>
     * @param command Name of the command used in format
     * @return String with duration in format
     */
    public String duration(String command){
        return "Running " + command + " took: `" + durationLong() + "`ms";
    }

    /**
     * Build duration string with command name
     * <p>Running `command` took `###`ms</p>
     * @param command Name of the command used in format
     * @param ID ID to query for timer
     * @return String with duration in format
     */
    public static String duration(String command, long ID){
        return Objects.requireNonNull(findTimer(ID)).duration(command);
    }

    /**
     * Resets the timer
     */
    private void reset(){
        start = 0;
        end = 0;
    }

    /**
     * Deletes a timer
     * @param ID ID to query for timer
     */
    public static void deleteTimer(long ID){
        timers.remove(findTimer(ID));
    }

    /**
     * Finds a timer by ID
     * @param ID ID to query for timer
     * @return found timer (null if none found)
     */
    public static CocoTimer findTimer(long ID){
        for (CocoTimer timer : timers){
            if (timer.getID() == ID) return timer;
        }
        return null;
    }
}
