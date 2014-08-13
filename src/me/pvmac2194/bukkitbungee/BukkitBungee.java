package me.pvmac2194.bukkitbungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class BukkitBungee extends JavaPlugin implements PluginMessageListener, Listener
  {
    // Instance variables
    String[] playerList;
    public static String[] serverList;
    public static String serverListString;
    public static String currentServer;
    public static String plServer;
    public static String playerListString;
    public static int gPlayerCount;
    public boolean gettingServerList = false;
    Player senderPlayer = null;

    @Override
    public void onEnable()
      {
        Bukkit.getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
      }

    /**
     * Handles bungeecord messages.
     */
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message)
      {
        if (!channel.equals("BungeeCord"))
          {
            return;
          }

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));

        try
          {
            String subchannel = in.readUTF();
            if (subchannel.equals("GetServer"))
              {
                currentServer = in.readUTF();
              }
            else if (subchannel.equals("GetServers"))
              {
                serverListString = "";
                serverList = in.readUTF().split(", ");
                serverListString = "";
                boolean firstServer = true;
                for (String s : serverList)
                  {
                    if (firstServer)
                      firstServer = false;
                    else
                      serverListString += colorString(getConfig().getString("commacolor"));
                    serverListString += s;
                  }
              }
            else if (subchannel.equals("PlayerList"))
              {
                playerListString = "";
                plServer = in.readUTF();
                String originalPlayerList = in.readUTF();
                playerList = originalPlayerList.split(", ");
                boolean firstPlayer = true;
                for (String s : playerList)
                  {
                    if (firstPlayer)
                      firstPlayer = false;
                    else
                      playerListString += ", ";
                    playerListString += s;
                  }

                senderPlayer.sendMessage(colorString(getConfig().getString("gliststyle").replace("{SERVERNAME}", plServer)
                    .replace("{SERVERCOUNT}", (playerListString.length() > 0 ? playerList.length + "" : playerListString.length() + ""))
                    .replace("{PLAYERLIST}", playerListString)));
              }
            else if (subchannel.equals("PlayerCount"))
              {
                in.readUTF(); // Not needed server variable
                gPlayerCount = in.readInt();
                if (!(getConfig().getString("beforetotal").equals("none")))
                  {
                    senderPlayer.sendMessage(colorString(getConfig().getString("beforetotal")));
                  }

                senderPlayer.sendMessage(colorString(getConfig().getString("totalonline")).replace("{TOTAL}", gPlayerCount + ""));

                if (!(getConfig().getString("aftertotal").equals("none")))
                  {
                    senderPlayer.sendMessage(colorString(getConfig().getString("aftertotal")));
                  }
              }
          }
        catch (IOException e)
          {
            e.printStackTrace();
          }
      }

    /**
     * Colors a string from & symbols.
     */
    public String colorString(String s)
      {
        return ChatColor.translateAlternateColorCodes('&', s);
      }

    /**
     * Get all players online (int).
     */
    public void getAllPlayersInCount()
      {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerCount");
        out.writeUTF("ALL");
        try
          {
            Player player = Iterables.getFirst(getServer().getOnlinePlayers(), null);
            player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
          }
        catch (Exception e)
          {
            e.printStackTrace();
          }
      }

    /**
     * Gets all players on the network.
     */
    public void getAllPlayers()
      {
        for (String string : serverList)
          {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("PlayerList");
            out.writeUTF(string);
            try
              {
                Player player = Iterables.getFirst(getServer().getOnlinePlayers(), null);
                player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
              }
            catch (Exception e)
              {
                e.printStackTrace();
              }
          }
      }

    /**
     * Get the current server this plugin is on.
     */
    public void getCurrentServer()
      {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");
        try
          {
            Player player = Iterables.getFirst(getServer().getOnlinePlayers(), null);
            player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
          }
        catch (Exception e)
          {
            e.printStackTrace();
          }
      }

    /**
     * Get all servers online.
     */
    public void getAllServers()
      {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServers");
        try
          {
            Player player = Iterables.getFirst(getServer().getOnlinePlayers(), null);
            player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
          }
        catch (Exception e)
          {
            e.printStackTrace();
          }
      }

    /**
     * Handles commands.
     */
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
      {
        if (commandLabel.equalsIgnoreCase("server"))
          {
            senderPlayer = (Player) sender;
            if (args.length == 0)
              {
                gettingServerList = false;
                getCurrentServer();
                getAllServers();

                getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
                  {
                    public void run()
                      {
                        senderPlayer.sendMessage(colorString(getConfig().getString("currentserver")).replace("{CURRENTSERVER}", currentServer));
                        senderPlayer.sendMessage(colorString(getConfig().getString("serverlist")).replace("{SERVERLIST}", serverListString));
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
                senderPlayer.sendPluginMessage(this, "BungeeCord", b.toByteArray());
              }
          }
        else if (commandLabel.equalsIgnoreCase("glist"))
          {
            senderPlayer = (Player) sender;
            gettingServerList = true;
            if (!(getConfig().getString("beforeglist").equals("none")))
              {
                sender.sendMessage(colorString(getConfig().getString("beforeglist")));
              }
            getAllServers();
            getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
              {
                public void run()
                  {
                    getAllPlayers();
                    getAllPlayersInCount();
                  }
              }, 2L);
          }
        else if (cmd.getName().equalsIgnoreCase("bbreload"))
          {
            if (sender.hasPermission("bukkitbungee.reload"))
              {
                this.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "The configuration has been reloaded.");
              }
          }

        return false;
      }
  }
