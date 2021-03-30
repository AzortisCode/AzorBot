package com.azortis.azorbot.util;

import com.azortis.azorbot.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages files for read, write and creation
 */
public class FileManager {
    private final File file;

    /**
     * Creates a FileManager
     * @param file The file that needs to be managed
     */
    public FileManager(File file){
        this.file = file;
    }

    /**
     * Creates a  FileManager
     * @param path The path to the file that needs to be managed
     */
    public FileManager(String path){
        this.file = new File(path);
    }

    /** Reads from file
     * @return List of strings found in the file. Can be empty. Null if failed
     */
    public List<String> read(){

        if (!checkExists(true, true)){
            Main.error("Failed reading from file. Failed to create file.");
            return null;
        }

        // Make reader
        Scanner reader;
        try {
            reader = new Scanner(file);
        } catch (FileNotFoundException e){
            Main.error("Failed creating fileScanner");
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
            Main.error("Not found next line to write");
            e.printStackTrace();
            return null;
        } catch (IllegalStateException e){
            Main.error("");
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
     * @param createIfNot If true, creates a new file if not yet existant
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

                    // If new file was properly created
                    if (file.createNewFile()){
                        Main.info("Created blank definitions file for A2A Watchdog");

                        // Return if asked
                        return returnCreationInfo;
                    }
                } catch (IOException e){
                    Main.error("While creating definitions file for A2A Watchdog");
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


    /** Writes the string `in` to the file
     * @param in List of strings to write
     * @return boolean value indicating success or failure
     */
    public boolean write(List<String> in){

        // Make sure file exists
        if (!checkExists(true, true)){
            Main.error("Failed to write to file. Failed to create file.");
        }

        // Create writer
        FileWriter writer;
        try {
            writer = new FileWriter(file);
        } catch (IOException e){
            Main.error("Failed creating fileWriter");
            e.printStackTrace();
            return false;
        }

        // Write to file
        AtomicBoolean success = new AtomicBoolean(true);
        in.forEach(line -> {
            try {
                writer.write(line + "\n");
            } catch (IOException e){
                Main.error("Failed writing a line to file");
                e.printStackTrace();
                success.set(false);
            }
        });

        // Close writer
        try {
            writer.close();
        } catch (IOException e){
            Main.error("Failed closing writer");
            e.printStackTrace();
            return false;
        }

        // Return successful
        return success.get();
    }
}
