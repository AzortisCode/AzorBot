package com.azortis.azorbot.commands;

import com.azortis.azorbot.commands.wiki.wikiIndex;
import com.azortis.azorbot.util.AzorbotCommand;

public class Wiki extends AzorbotCommand {
    public Wiki() {
        super(
                "Wiki",
                new String[]{"Wikis"},
                "All wiki-related commands",
                new AzorbotCommand[]{
                    new wikiIndex()
                }
        );
    }
}
