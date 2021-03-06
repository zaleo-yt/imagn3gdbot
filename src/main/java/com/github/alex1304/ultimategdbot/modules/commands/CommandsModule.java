package com.github.alex1304.ultimategdbot.modules.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.alex1304.ultimategdbot.core.UltimateGDBot;
import com.github.alex1304.ultimategdbot.exceptions.CommandFailedException;
import com.github.alex1304.ultimategdbot.exceptions.ModuleUnavailableException;
import com.github.alex1304.ultimategdbot.modules.Module;
import com.github.alex1304.ultimategdbot.modules.commands.impl.about.AboutCommand;
import com.github.alex1304.ultimategdbot.modules.commands.impl.account.AccountCommand;
import com.github.alex1304.ultimategdbot.modules.commands.impl.changelog.ChangelogCommand;
import com.github.alex1304.ultimategdbot.modules.commands.impl.checkmod.CheckModCommand;
import com.github.alex1304.ultimategdbot.modules.commands.impl.daily_weekly.DailyWeeklyCommand;
import com.github.alex1304.ultimategdbot.modules.commands.impl.featuredinfo.FeaturedInfoCommand;
import com.github.alex1304.ultimategdbot.modules.commands.impl.gdevents.GDEventsCommand;
import com.github.alex1304.ultimategdbot.modules.commands.impl.help.HelpCommand;
import com.github.alex1304.ultimategdbot.modules.commands.impl.leaderboard.LeaderboardMenu;
import com.github.alex1304.ultimategdbot.modules.commands.impl.level.LevelCommand;
import com.github.alex1304.ultimategdbot.modules.commands.impl.levelsby.LevelsByCommand;
import com.github.alex1304.ultimategdbot.modules.commands.impl.modlist.ModListCommand;
import com.github.alex1304.ultimategdbot.modules.commands.impl.modules.ModulesCommand;
import com.github.alex1304.ultimategdbot.modules.commands.impl.profile.ProfileCommand;
import com.github.alex1304.ultimategdbot.modules.commands.impl.pushevent.PushEventCommand;
import com.github.alex1304.ultimategdbot.modules.commands.impl.restart.RestartCommand;
import com.github.alex1304.ultimategdbot.modules.commands.impl.setup.SetupCommand;
import com.github.alex1304.ultimategdbot.modules.commands.impl.shutdown.ShutdownCommand;
import com.github.alex1304.ultimategdbot.modules.commands.impl.system.SystemCommand;
import com.github.alex1304.ultimategdbot.modules.reply.Reply;
import com.github.alex1304.ultimategdbot.modules.reply.ReplyModule;
import com.github.alex1304.ultimategdbot.utils.BotRoles;
import com.github.alex1304.ultimategdbot.utils.BotUtils;
import com.github.alex1304.ultimategdbot.utils.Emojis;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.PermissionUtils;

/**
 * Module that manages and handles bot commands
 *
 * @author Alex1304
 */
public class CommandsModule implements Module {

	private Map<String, Command> commandMap;
	
	private boolean isEnabled;

	public CommandsModule() {
		this.isEnabled = false;
		this.commandMap = new TreeMap<>();
		registerCommands();
	}

	@Override
	public void start() {
		isEnabled = true;
	}

	@Override
	public void stop() {
		isEnabled = false;
	}

	/**
	 * Puts a command into the map, associated by name
	 * @param cmd
	 */
	public void registerCommand(String name, Command cmd) {
		commandMap.put(name, cmd);
	}
	
	/**
	 * This is where the command map is loaded with command instances
	 */
	private void registerCommands() {
		registerCommand("ping", (event, args) -> {
			BotUtils.sendMessage(event.getChannel(), "Pong! :ping_pong:");
		});
		
		registerCommand("help", new HelpCommand());
		registerCommand("modules", new ModulesCommand());
		registerCommand("level", new LevelCommand());
		registerCommand("profile", new ProfileCommand());
		registerCommand("account", new AccountCommand());
		registerCommand("leaderboard", (event, args) -> executeCommand(new LeaderboardMenu(), event, args));
		registerCommand("setup", new SetupCommand());
		registerCommand("daily", new DailyWeeklyCommand(false));
		registerCommand("weekly", new DailyWeeklyCommand(true));
		registerCommand("pushevent", new PushEventCommand());
		registerCommand("checkmod", new CheckModCommand());
		registerCommand("modlist", new ModListCommand());
		registerCommand("about", new AboutCommand());
		registerCommand("gdevents", new GDEventsCommand());
		registerCommand("restart", new RestartCommand());
		registerCommand("shutdown", new ShutdownCommand());
		registerCommand("system", new SystemCommand());
		registerCommand("levelsby", new LevelsByCommand());
		registerCommand("featuredinfo", new FeaturedInfoCommand());
		registerCommand("changelog", new ChangelogCommand());
	}
	
	/**
	 * Executes a command asynchronously. Works even if the module is stopped.
	 * 
	 * @param cmd - The command instance
	 * @param event - The message received event containing context info of the command
	 * @param args - The arguments of the command
	 */
	public static void executeCommand(Command cmd, MessageReceivedEvent event, List<String> args) {
		new Thread(() -> {
			try {
				if (!BotRoles.isGrantedAll(event.getAuthor(), event.getChannel(), cmd.getRolesRequired()))
					throw new CommandFailedException("You don't have permission to use this command");
				else if (!PermissionUtils.hasPermissions(event.getChannel(), UltimateGDBot.client().getOurUser(), cmd.getPermissionsRequired())) {
					StringBuilder sb = new StringBuilder();
					for (Permissions perm : cmd.getPermissionsRequired())
						sb.append("- " + perm.toString().replaceAll("_", " ") + "\n");
					throw new CommandFailedException("I don't have the necessary permissions in your server to run this command. This command requires all of the following permissions to work:\n" + sb.toString());
				} else
					cmd.runCommand(event, args);
			} catch (CommandFailedException e) {
				BotUtils.sendMessage(event.getChannel(), Emojis.CROSS + " " + e.getMessage());
			} catch (DiscordException e) {
				BotUtils.sendMessage(event.getChannel(), Emojis.CROSS + " Sorry, an error occured"
						+ " while running the command.\n```\n" + e.getErrorMessage() + "\n```");
				System.err.println(e.getErrorMessage());
			} catch (Exception e) {
				BotUtils.sendMessage(event.getChannel(), Emojis.CROSS + " An internal error occured while running the command."
						+ " Please try again later.");
				UltimateGDBot.logException(e);
			}
		}).start();
	}
	
	/**
	 * Handles the message received event from Discord and runs the command if
	 * prefix and user permissions match
	 * 
	 * @param event - Contains context of the message received
	 */
	public void onMessageReceived(MessageReceivedEvent event) {
		if (!isEnabled && !BotRoles.isGranted(event.getAuthor(), event.getChannel(), BotRoles.OWNER))
			return;
		
		if (event.getAuthor().isBot())
			return;
		
		String[] argArray = event.getMessage().getContent().split(" +");

		if (argArray.length == 0)
			return;
		
		String prefixUsed = BotUtils.prefixUsedInMessage(argArray[0]);
		boolean isMentionPrefix = BotUtils.isMentionPrefix(argArray[0]);
		
		if (prefixUsed == null)
			return;

		final String cmdName = isMentionPrefix ?
				argArray[1] : argArray[0].substring(prefixUsed.length()).toLowerCase();
		final List<String> args = new ArrayList<>(Arrays.asList(argArray));
		
		if (isMentionPrefix)
			args.remove(0);
		args.remove(0);
		
		if (commandMap.containsKey(cmdName)) {
			// Before executing the command, cancel any opened reply for the current user/channel
			try {
				ReplyModule rm = (ReplyModule) UltimateGDBot.getModule("reply");
				Reply r = rm.getReply(event.getChannel(), event.getAuthor());
				if (r != null)
					r.cancel();
			} catch (ModuleUnavailableException e) {
			}
			executeCommand(commandMap.get(cmdName), event, args);
		}
	}

	/**
	 * Gets the commandMap
	 *
	 * @return Map&lt;String,Command&gt;
	 */
	public Map<String, Command> getCommandMap() {
		return commandMap;
	}

}
