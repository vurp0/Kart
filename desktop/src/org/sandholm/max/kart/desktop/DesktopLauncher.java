package org.sandholm.max.kart.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.jglfw.JglfwApplication;
import com.badlogic.gdx.backends.jglfw.JglfwApplicationConfiguration;
import org.sandholm.max.kart.KartGame;

public class DesktopLauncher {
	public static void main (String[] arg) {

		/* For some reason (god knows why, I blame Nvidia), when I use LWJGL
		 * my left monitor disconnects when I exit the program.
		 * That's the entire reason I use JGLFW instead of LWJGL.
		 * Let's hope for the day when this bug disappears as mysteriously as it appeared.
		 */

		/* Also, my laptop is 32-bit, and JGLFW doesn't compile properly
		 * for 32-bit Linux, so on my laptop I have to use LWJGL instead.
		 */

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//JglfwApplicationConfiguration config = new JglfwApplicationConfiguration();
		config.forceExit = false;
		config.vSyncEnabled = true;
		config.height = 480;
		config.width = 800;
		new LwjglApplication(new KartGame(), config);
		//new JglfwApplication(new KartGame(), config);
	}
}
