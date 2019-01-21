package bham.bioshock;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL;

public class HelloWorld {
	public static void main(String[] args) {
		new HelloWorld().run();
	}

	private void run() {
		if (!glfwInit()) {
			System.out.println("GLFW failed to initialize.");
			System.exit(1);
		}

		long window = glfwCreateWindow(640, 480, "Window", 0, 0);

		glfwShowWindow(window);
		glfwMakeContextCurrent(window);

		GL.createCapabilities();

		while(!glfwWindowShouldClose(window)) {
			glfwPollEvents();

			if (glfwGetKey(window, GLFW_KEY_A) == GL_TRUE) {
			}

			glClear(GL_COLOR_BUFFER_BIT);

			glBegin(GL_QUADS);
			glColor4f(1, 0, 0, 0);
			glVertex2f(-0.5f, 0.5f);
			glVertex2f(0.5f, 0.5f);
			glVertex2f(0.5f, -0.5f);
			glVertex2f(-0.5f, -0.5f);
			glEnd();

			glfwSwapBuffers(window);
		}

		glfwTerminate();
	}
}