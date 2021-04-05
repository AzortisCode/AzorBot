package com.azortis.azorbot.cocoUtil;

public class AnExampleCategory extends CocoCommand {
    // Constructor
    public AnExampleCategory(){
        super(
                "ExampleCategory",
                new String[]{"CategoryAlias1", "Alias2"},
                new String[]{"RequiredRole1", "OrRole2"},
                "ExampleCategory command category",
                new CocoCommand[]{
                        new AnExampleCommand(),
                        new AnExampleCommand()
                }
        );
    }
}