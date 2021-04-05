package com.azortis.azorbot.util;

public class ExampleCategory extends CocoCommand {
    // Constructor
    public ExampleCategory(){
        super(
                "ExampleCategory",
                new String[]{"CategoryAlias1", "Alias2"},
                new String[]{"RequiredRole1", "OrRole2"},
                "ExampleCategory command category",
                new CocoCommand[]{
                        new ExampleCommand(),
                        new ExampleCommand()
                }
        );
    }
}