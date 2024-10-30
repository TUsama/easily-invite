package com.clefal.ei;

import com.clefal.ei.detector.TickWorker;
import net.fabricmc.api.ClientModInitializer;

public class EasilyInviteClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		TickWorker.initResetter();
	}
}