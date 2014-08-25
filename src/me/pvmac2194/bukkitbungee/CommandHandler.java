package me.pvmac2194.bukkitbungee;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class CommandHandler implements Listener, CommandExecutor
  {
    BukkitBungee plugin;

    public CommandHandler(BukkitBungee plugin)
      {
        this.plugin = plugin;
      }

    /**
     * Colors a string from & symbols.
     */
    public String colorString(String s)
      {
        return ChatColor.translateAlternateColorCodes('&', s);
      }

    /**
     * Handles commands.
     */
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
      {
        if (commandLabel.equalsIgnoreCase("server"))
          {
            plugin.senderPlayer = (Player) sender;
            if (args.length == 0)
              {
                plugin.gettingServerList = false;
                plugin.getCurrentServer();
                plugin.getAllServers();

                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
                  {
                    public void run()
                      {
                        plugin.senderPlayer.sendMessage(colorString(plugin.getConfig().getString("currentserver")).replace("{CURRENTSERVER}",
                            plugin.currentServer));
                        plugin.senderPlayer.sendMessage(colorString(plugin.getConfig().getString("serverlist")).replace("{SERVERLIST}",
                            plugin.serverListString));
                      }
                  }, 2L);

              }
            else if (args.length == 1)
              {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                try
                  {
                    // Gets current server
                    out.writeUTF("Connect");
                    out.writeUTF(args[0]);
                  }
                catch (Exception e)
                  {
                    // ignore
                  }
                plugin.senderPlayer.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
              }
          }
        else if (commandLabel.equalsIgnoreCase("glist"))
          {
            plugin.senderPlayer = (Player) sender;
            plugin.gettingServerList = true;
            if (!(plugin.getConfig().getString("beforeglist").equals("none")))
              {
                sender.sendMessage(colorString(plugin.getConfig().getString("beforeglist")));
              }
            plugin.getAllServers();
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
              {
                public void run()
                  {
                    plugin.getAllPlayers();
                    plugin.getAllPlayersInCount();
                  }
              }, 2L);
          }
        else if (cmd.getName().equalsIgnoreCase("bbreload"))
          {
            if (sender.hasPermission("bukkitbungee.reload"))
              {
                plugin.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "The configuration has been reloaded.");
              }
          }

        return false;
      }
  }