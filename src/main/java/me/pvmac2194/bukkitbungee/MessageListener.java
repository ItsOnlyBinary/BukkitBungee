package me.pvmac2194.bukkitbungee;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class MessageListener implements PluginMessageListener, Listener
  {
    BukkitBungee plugin;

    public MessageListener(BukkitBungee plugin)
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
     * Handles bungeecord messages.
     */
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
                plugin.currentServer = in.readUTF();
              }
            else if (subchannel.equals("GetServers"))
              {
                plugin.serverListString = "";
                plugin.serverList = in.readUTF().split(", ");
                plugin.serverListString = "";
                boolean firstServer = true;
                for (String s : plugin.serverList)
                  {
                    if (firstServer)
                      firstServer = false;
                    else
                      plugin.serverListString += colorString(plugin.getConfig().getString("commacolor"));
                    plugin.serverListString += s;
                  }
              }
            else if (subchannel.equals("PlayerList"))
              {
                plugin.playerListString = "";
                plugin.plServer = in.readUTF();
                String originalPlayerList = in.readUTF();
                plugin.playerList = originalPlayerList.split(", ");
                boolean firstPlayer = true;
                for (String s : plugin.playerList)
                  {
                    if (firstPlayer)
                      firstPlayer = false;
                    else
                      plugin.playerListString += ", ";
                    plugin.playerListString += s;
                  }

                plugin.senderPlayer.sendMessage(colorString(plugin
                    .getConfig()
                    .getString("gliststyle")
                    .replace("{SERVERNAME}", plugin.plServer)
                    .replace("{SERVERCOUNT}",
                        (plugin.playerListString.length() > 0 ? plugin.playerList.length + "" : plugin.playerListString.length() + ""))
                    .replace("{PLAYERLIST}", plugin.playerListString)));
              }
            else if (subchannel.equals("PlayerCount"))
              {
                in.readUTF(); // Not needed server variable
                plugin.gPlayerCount = in.readInt();
                if (!(plugin.getConfig().getString("beforetotal").equals("none")))
                  {
                    plugin.senderPlayer.sendMessage(colorString(plugin.getConfig().getString("beforetotal")));
                  }

                plugin.senderPlayer
                    .sendMessage(colorString(plugin.getConfig().getString("totalonline")).replace("{TOTAL}", plugin.gPlayerCount + ""));

                if (!(plugin.getConfig().getString("aftertotal").equals("none")))
                  {
                    plugin.senderPlayer.sendMessage(colorString(plugin.getConfig().getString("aftertotal")));
                  }
              }
          }
        catch (IOException e)
          {
            e.printStackTrace();
          }
      }

  }
