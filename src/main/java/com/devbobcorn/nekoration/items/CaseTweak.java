package com.devbobcorn.nekoration.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class CaseTweak {
    private static LanguageManager lang = null;

    public static Component getTweaked(Component original){
        if (lang == null)
            lang = Minecraft.getInstance().getLanguageManager();
        if (lang.getSelected().getCode().equals("lol_us")){
            int num = original.hashCode();
            if (num % 10 == 3)
                return new TextComponent(original.getString().toUpperCase());
            else if (num % 9 == 7)
                return new TextComponent(original.getString().toLowerCase());
        }
        return original;
    }
}
