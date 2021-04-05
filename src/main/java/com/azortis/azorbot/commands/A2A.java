package com.azortis.azorbot.commands;

import com.azortis.azorbot.commands.a2a.*;
import com.azortis.azorbot.cocoUtil.CocoCommand;

public class A2A extends CocoCommand {
    public A2A(){
        super(
                "A2a",
                new String[]{"aa"},
                new String[]{"Admin", "Developer", "Moderator"},
                "A2A definitions command category",
                new CocoCommand[]{
                        new A2AAdd(),
                        new A2AThreshold(),
                        new A2ADelete(),
                        new A2AList()
                }
        );
    }
}
