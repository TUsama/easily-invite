package com.clefal.ei;

import com.clefal.ei.detector.TriggerStringResetter;
import net.fabricmc.api.ClientModInitializer;

public class EasilyInviteClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		TriggerStringResetter.initResetter();
	}
}