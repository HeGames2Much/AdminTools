package club.therepo.admintools.util;

import org.apache.commons.lang.ObjectUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private ItemStack i;
    private ItemMeta im;

    public static ItemStack WHITEPANE;
    public static ItemStack BLACKPANE;

    static {
        WHITEPANE = new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE, " ").build();
        BLACKPANE = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE, " ").build();
    }

    public ItemBuilder(XMaterial m, String name) {
        i = m.parseItem();
        im = i.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        im.setDisplayName(name);
    }

    public ItemBuilder(XMaterial m) {
        i = new ItemStack(m.parseMaterial());
        im = i.getItemMeta();
    }

    public ItemBuilder setAmount(int amount) {
        i.setAmount(amount);
        return this;
    }

    public ItemBuilder setName(String name) {
        im.setDisplayName(name);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        im.setLore(lore);
        return this;
    }

    public ItemBuilder addLore(String lore) {
        List<String> oldLore = im.getLore();
        if(oldLore == null) oldLore = new ArrayList<>();

        if(lore.contains("##")) {
            String[] loreSplit = lore.split("##");
            for(String s : loreSplit) {
                oldLore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
        } else {
            oldLore.add(lore);
        }
        im.setLore(oldLore);
        return this;
    }

    public ItemStack build() {
        i.setItemMeta(im);
        return i;
    }
}