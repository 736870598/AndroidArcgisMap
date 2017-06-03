package com.bs.graduation.drawtool;

import java.util.EventListener;

public interface DrawEventListener extends EventListener {

	void handleDrawEvent(DrawEvent event);
}
