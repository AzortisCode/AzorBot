package com.azortis.azorbot.commands.wiki;

import com.azortis.azorbot.util.AzorbotCommand;

public class wikiIndex extends AzorbotCommand {
    public wikiIndex(){
        super(
                "Index",
                new String[]{"ind", "i", "list"},
                null,
                "Prints the index of the entered wiki",
                true,
                "!wiki index Orbis"
        );
    }
}
