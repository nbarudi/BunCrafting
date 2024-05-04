package ca.bungo.customcraftingsystem;

import ca.bungo.customcraftingsystem.commands.TestCommand;
import ca.bungo.customcraftingsystem.events.InventoryListener;
import ca.bungo.customcraftingsystem.manager.CraftingManager;
import ca.bungo.customcraftingsystem.types.CustomCraft;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public class CustomCraftingSystem extends JavaPlugin {

    public static Logger LOGGER;
    private static CustomCraftingSystem instance;
    public CraftingManager craftingManager;


    @Override
    public void onEnable() {
        instance = this;
        LOGGER = this.getSLF4JLogger();

        craftingManager = new CraftingManager();

        //Events
        this.getServer().getPluginManager().registerEvents(new InventoryListener(), this);

        //Commands
        this.getServer().getCommandMap().register("customcraftingsystem", new TestCommand());

    }

    @Override
    public void onDisable() {
        craftingManager.cleanup();
    }

    public static CustomCraftingSystem getInstance() { return instance; }
}
