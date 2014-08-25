package me.pvmac2194.bukkitbungee;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class BukkitBungee extends JavaPlugin implements Listener
  {
    // Instance variables
    String[] playerList;
    public String[] serverList;
    public String serverListString;
    public String currentServer;
    public String plServer;
    public String playerListString;
    public int gPlayerCount;
    public boolean gettingServerList = false;
    Player senderPlayer = null;
    CommandHandler handler;

    @Override
    public void onEnable()
      {
        Bukkit.getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new CommandHandler(this), this);
        getServer().getPluginManager().registerEvents(new MessageListener(this), this);
        handler = new CommandHandler(this);
        getCommand("server").setExecutor(handler);
        getCommand("glist").setExecutor(handler);
        getCommand("bbreload").setExecutor(handler);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener(this));
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
  }