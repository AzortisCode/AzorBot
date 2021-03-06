package com.azortis.azorbot.cocoUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages files for read, write and creation
 */
public class CocoFiles {
    private final File file;

    /**
     * Creates a FileManager
     * @param file The file that needs to be managed
     */
    public CocoFiles(File file){
        this.file = file;
    }

    /**
     * Creates a FileManager
     * @param path The path to the file that needs to be managed
     */
    public CocoFiles(String path){
        this.file = new File(path);
    }

    /**
     * Creates a FileManager
     * @param path the path to the file
     * @param check toggles whether a check should be made at creation
     */
    public CocoFiles(String path, boolean check){
        this.file = new File(path);
        if (check) checkExists(true, false);
    }

    /** Reads from file
     * @return List of strings found in the file. Can be empty. Null if failed
     */
    public List<String> read(){

        if (!checkExists(true, true)){
            CocoBot.error("Failed reading from file. Failed to create file.");
            return null;
        }

        // Make reader
        Scanner reader;
        try {
            reader = new Scanner(file);
        } catch (FileNotFoundException e){
            CocoBot.error("Failed creating fileScanner");
            e.printStackTrace();
            return null;
        }

        // Read from file
        List<String> file = new ArrayList<>();
        try {
            while (reader.hasNextLine()){
                file.add(reader.nextLine());
            }
        } catch (NoSuchElementException e){
            CocoBot.error("Not found next line to write");
            e.printStackTrace();
            return null;
        } catch (IllegalStateException e){
            CocoBot.error("");
            e.printStackTrace();
            return null;
        }

        // Close reader
        reader.close();

        // Return String
        return file;
    }

    /**
     * Checks if the stored file exists.
     * If it does not and `createIfNot`, it creates a new one.
     * @param createIfNot If true, creates a new file if not yet existent
     * @param returnCreationInfo If true, returns the creation info if a new file was created, instead of simple exists
     *                           Note: This is never used if `createIfNot` is false
     *
     * @return boolean value based on params and file creation / checks
     */
    public boolean checkExists(boolean createIfNot, boolean returnCreationInfo){

        // Check if the file exists
        if (!file.exists()){


            // Create if should create
            if (createIfNot){
                try {

                    if (!file.exists()){

                        CocoBot.info("Creating new file for: " + file.getName());

                        if (file.getParentFile() == null){
                            CocoBot.error("No parent file found for file: " + file.getName());
                            return false;
                        }
                        if (file.getParentFile().mkdirs()){
                            CocoBot.info("Created parent directories");
                        }

                        // If new file was properly created
                        if (file.createNewFile()){
                            CocoBot.info("Created new file");

                            // Return if asked
                            return returnCreationInfo;
                        }
                    }
                } catch (IOException e){
                    CocoBot.error("Exception while creating new file or folders");
                    e.printStackTrace();
                    // Return that file failed to create
                    return false;
                }

            // Return that file didn't exist
            } else {
                return false;
            }
        }

        // File exists, return true
        return true;
    }

    /**
     * Writes the string
     * @param in String to write
     */
    public void write(String in){
        write(Collections.singletonList(in));
    }

    /** Writes the string to file
     * @param in List of strings to write
     * @return boolean value indicating success or failure
     */
    public boolean write(List<String> in){

        // Make sure file exists
        if (!checkExists(true, true)){
            CocoBot.error("Failed to write to file. Failed to create file.");
            return false;
        }

        // Create writer
        FileWriter writer;
        try {
            writer = new FileWriter(file);
        } catch (IOException e){
            CocoBot.error("Failed creating fileWriter");
            e.printStackTrace();
            return false;
        }

        // Write to file
        AtomicBoolean success = new AtomicBoolean(true);
        in.forEach(line -> {
            try {
                writer.write(line + "\n");
            } catch (IOException e){
                CocoBot.error("Failed writing a line to file");
                e.printStackTrace();
                success.set(false);
            }
        });

        // Close writer
        try {
            writer.close();
        } catch (IOException e){
            CocoBot.error("Failed closing writer");
            e.printStackTrace();
            return false;
        }

        // Return successful
        return success.get();
    }

    /**
     * Deletes the managed file
     */
    public boolean delete(){
        return file.delete();
    }

    /**
     * Get when the managed file was last edited
     * @return LocalDateTime
     */
    public LocalDateTime getLastEdited(){
        return LocalDateTime.ofEpochSecond(this.file.lastModified(), 0, ZoneOffset.of(TimeZone.getDefault().getID()));
    }
}
