package com.hero.witchery_rewitched.util.listeners;


import net.minecraftforge.event.AddReloadListenerEvent;

public class ListenerEvents {

    public static void addReloadListeners(AddReloadListenerEvent event){
        event.addListener(new AltarPowererReloadListener());
    }
}
