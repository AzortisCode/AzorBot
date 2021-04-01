package com.azortis.azorbot.commands;

import com.azortis.azorbot.commands.wiki.*;
import com.azortis.azorbot.util.AzorbotCommand;

public class Wiki extends AzorbotCommand {
    public Wiki(){
        super(
                "Wiki",
                new String[]{"Wikis", "w"},
                "All wiki-related commands",
                new AzorbotCommand[]{
                        new wikiIndex(),
                        new wikiCreate(),
                        new wikiUpdate(),
                        new wikiList(),
                        new wikiInfo()
                }
        );
    }
}
