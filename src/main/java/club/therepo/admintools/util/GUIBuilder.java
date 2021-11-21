package club.therepo.admintools.util;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIBuilder {

    private Inventory inv;
    private MessageTranslator msg;

    public GUIBuilder(String invName, int rows , int items) {
        if(invName.length() > 32) invName = invName.substring(0,32);
        if(items > 5) {
            inv = Bukkit.createInventory(null, rows*9, invName);
        } else {
            inv = Bukkit.createInventory(null, InventoryType.HOPPER, invName);
        }
        msg = MessageTranslator.getInstance();
    }

    public GUIBuilder fill(ItemStack item) {
        for(int slot = 0; slot < inv.getSize(); slot++) {
            inv.setItem(slot,item);
        }
        return this;
    }

    public GUIBuilder fillRow(ItemStack item, int row) {
        for(int slot = row*9; slot < (row+1)*9; slot++) {
            inv.setItem(slot,item);
        }
        return this;
    }

    public GUIBuilder setItem(int slot, ItemStack item) {
        inv.setItem(slot, item);
        return this;
    }

    public Inventory build() {
        return inv;
    }
}