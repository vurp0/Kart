package org.sandholm.max.kart.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.jglfw.JglfwApplication;
import com.badlogic.gdx.backends.jglfw.JglfwApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
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

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		//JglfwApplicationConfiguration config = new JglfwApplicationConfiguration();
		config.setWindowedMode(800, 480);
		new Lwjgl3Application(new KartGame(), config);
		//new JglfwApplication(new KartGame(), config);
	}
}
