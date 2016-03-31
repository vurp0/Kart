package org.sandholm.max.kart.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import org.sandholm.max.kart.KartGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(256, 256);
		config.setResizable(false);

		DesktopSpecifics desktopSpecifics = new DesktopSpecifics();

		new Lwjgl3Application(new KartGame(desktopSpecifics), config);
	}
}