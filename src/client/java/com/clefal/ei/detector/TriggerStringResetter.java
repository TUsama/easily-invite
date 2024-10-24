package com.clefal.ei.detector;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.neoforged.bus.api.SubscribeEvent;

public class TriggerStringResetter {

    private static int timer = 0;

    public static void initResetter(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!ChatDetector.shouldDetect) return;
            if (ChatDetector.triggerString.isBlank()){
                ChatDetector.shouldDetect = false;
                throw new RuntimeException("detection mark is true but the trigger string is blank!");
            }
            timer++;
            if (timer > 20 * 30) {
                ChatDetector.triggerString = "";
                ChatDetector.shouldDetect = false;
                timer = 0;
            }
        });
    }
}
