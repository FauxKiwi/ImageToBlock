package visar.plugins.ImagetoBlockPlugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.api.awt.AWTFrameGrab;
import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.io.NIOUtils;
import org.jetbrains.annotations.NotNull;
import visar.plugins.ImagetoBlockPlugin.Main;
import visar.plugins.ImagetoBlockPlugin.renderers.VideoRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VideoCommand implements CommandExecutor {
    private final Main plugin = Main.getPlugin();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if (args.length > 3) {
            player.sendMessage("§cUse /video <File Path> [width] [height] or /video [width] [height] ");
            return false;
        }
        String path = player.getUniqueId().toString() + ".video";
        String filepath = plugin.getConfig().getString(path);
        assert filepath != null;
        if(args.length == 0) {
            try {
		        File video = new File(filepath);
		        BufferedImage img = AWTFrameGrab.getFrame(video,1);
		        renderVideo(filepath,img.getWidth(),img.getHeight(),player);
            }catch(IOException | JCodecException e) {
                e.printStackTrace();
		        player.sendMessage("Width and height need to be numbers");
	        }
        }
        if (isInteger(args[0]) && args.length == 2) {
            if(plugin.getConfig().getString(path) == null) {
                player.sendMessage("§cYou need to first set a default video, to do that type /setdefaultvideo <File Path>");
                return false;
            }
            try {
		        renderVideo(filepath, Double.parseDouble(args[0]),Double.parseDouble(args[1]),player);
            }catch(NumberFormatException e) {
                e.printStackTrace();
		        player.sendMessage("Width and height need to be numbers");
            }
        } else if(!isInteger(args[0]) && args.length == 1){

            try {
		        File video = new File(args[0]);
		        BufferedImage img = AWTFrameGrab.getFrame(video,1);
		        renderVideo(args[0],img.getWidth(),img.getHeight(),player);
            }catch(IOException | JCodecException e) {
		        e.printStackTrace();
		        player.sendMessage("Width and height need to be numbers");
	    } 

        }else if(!isInteger(args[0]) && args.length == 3) {
            try {
		        renderVideo(args[0],Double.parseDouble(args[1]),Double.parseDouble(args[2]),player);
            }catch(NumberFormatException e) {
		        e.printStackTrace();
		        player.sendMessage("Width and height need to be numbers");
	        }
        }

        return false;

    }
    private void renderVideo(String filepath, double width, double height, Player player) {
        if(!plugin.getConfig().contains(player.getUniqueId().toString()+".togglevertwarning")) plugin.getConfig().set(player.getUniqueId().toString()+".togglevertwarning",false);
        if(!plugin.getConfig().contains(player.getUniqueId().toString()+".vertical")) plugin.getConfig().set(player.getUniqueId().toString()+".vertical",false);

        try {

                File video = new File(filepath);
                BufferedImage img = AWTFrameGrab.getFrame(video,1);
                boolean resize = true;
                if(width == img.getWidth() && height == img.getHeight()) resize = false;
                FrameGrab grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(video));
                DemuxerTrack vt = grab.getVideoTrack();
                if(plugin.getConfig().getBoolean(player.getUniqueId().toString()+".vertical")) {
                    VideoRenderer.renderVideo(player.getLocation(), player.getLocation().clone().add(width, height,0), filepath, vt.getMeta().getTotalFrames(), resize, player);
                    if(!plugin.getConfig().getBoolean(player.getUniqueId().toString()+".togglevertwarning")) {
                        player.sendMessage("§aImage displayed vertically");
                        player.sendMessage("§cIf you want to display the image horizontally use §6/vertical off§r");
                        player.sendMessage("§cIf you don't want to see this warning anymore use §6/togglevertwarning");
                    }
                }else {
                    VideoRenderer.renderVideo(player.getLocation(), player.getLocation().clone().add(width, 0,height), filepath, vt.getMeta().getTotalFrames(), resize, player);
                    if(!plugin.getConfig().getBoolean(player.getUniqueId().toString()+".togglevertwarning")) {
                        player.sendMessage("§aImage displayed horizontally");
                        player.sendMessage("§cIf you want to display the image vertically use §6/vertical on§r");
                        player.sendMessage("§cIf you don't want to see this warning anymore use §6/togglevertwarning");
                    }
                }

        }catch(IOException | JCodecException ex) {
                ex.printStackTrace();
                //player.sendMessage("§cWidth and height need to be integers");
            }
    } 
   
    private boolean isInteger(String s) {
        boolean isNumber = true;
        try {
            Integer.parseInt(s);
        }catch(NumberFormatException e) {
            isNumber = false;
        }
        return isNumber; 
    } 
}
