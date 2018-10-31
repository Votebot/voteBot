package me.schlaubi.votebot.commands.general;

import me.schlaubi.votebot.core.command.Command;
import me.schlaubi.votebot.core.command.CommandCategory;
import me.schlaubi.votebot.core.command.CommandEvent;
import me.schlaubi.votebot.core.command.Result;
import me.schlaubi.votebot.core.command.permission.Permissions;
import me.schlaubi.votebot.core.graphics.PieChart;
import me.schlaubi.votebot.core.graphics.PieTile;
import net.dv8tion.jda.core.MessageBuilder;

import java.awt.*;
import java.io.IOException;

public class ChartCommand extends Command {

    public ChartCommand() {
        super(new String[] {"chart"}, CommandCategory.GENERAL, Permissions.ownerOnly(), "", "");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        PieTile[] tiles = {new PieTile("Heyyyy", 50), new PieTile("Huiiii", 50)};
        try {
            PieChart chart = new PieChart("Test chart", tiles);
            sendFile(event.getChannel(), new MessageBuilder().setContent("file").build(), chart.toInputStream());
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}
