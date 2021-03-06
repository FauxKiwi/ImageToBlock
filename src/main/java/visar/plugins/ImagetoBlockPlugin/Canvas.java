package visar.plugins.ImagetoBlockPlugin;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import visar.plugins.ImagetoBlockPlugin.renderers.ImageRenderer;


public class Canvas {
    private static final Main plugin = Main.getPlugin();

    public static void createCanvas(Location l, int width, int height, Player p) {
        String path = p.getUniqueId().toString()+".canvas";
        String canvasexists = path+".canvasexists";
        if(!plugin.getConfig().contains(canvasexists)) {
            plugin.getConfig().set(canvasexists,false);
        }
        if(!plugin.getConfig().getBoolean(canvasexists)) {
            boolean vertical = plugin.getConfig().getBoolean(p.getUniqueId().toString() + ".vertical");
            ImageRenderer.renderImage(p.getLocation(), width, height, null, p);
            plugin.getConfig().set(path+".canvasexists",true);
            plugin.getConfig().set(path+".location",l);
            plugin.getConfig().set(path+".width",width);
            plugin.getConfig().set(path+".height",height);
            plugin.saveConfig();
            if (vertical) {
                p.sendMessage("§4Canvas created vertically");
            } else p.sendMessage("§4Canvas created horizontally");
        }else p.sendMessage("§cCanvas already exists, if you want to create a new one you need to delete your old canvas with §6/delcanvas");
    }
    public static void clearCanvas(String path, Player p) {
        int width = plugin.getConfig().getInt(path+".width"), height = plugin.getConfig().getInt(path+".height");
        Location l = (Location) plugin.getConfig().get(path+".location");
        assert l != null;
        boolean vertical = plugin.getConfig().getBoolean(p.getUniqueId().toString()+".vertical");
        ImageRenderer.renderImage(l, width, height, null, p);
    }
    public static void deleteCanvas(String path) {
        plugin.getConfig().set(path,null);
        plugin.saveConfig();
    }

}
