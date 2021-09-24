#include "input/Input.h"

namespace Minecraft
{
	namespace Input
	{
		int mouseX = 0;
		int mouseY = 0;
		int deltaMouseX = 0;
		int deltaMouseY = 0;

		bool keyPressed[GLFW_KEY_LAST];
		bool keyBeginPressData[GLFW_KEY_LAST];

		static int mLastMouseX;
		static int mLastMouseY;
		static bool mFirstMouse = true;

		void mouseCallback(GLFWwindow* window, double xpos, double ypos)
		{
			mouseX = xpos;
			mouseY = ypos;
			if (mFirstMouse)
			{
				mLastMouseX = xpos;
				mLastMouseY = ypos;
				mFirstMouse = false;
			}

			deltaMouseX = xpos - mLastMouseX;
			deltaMouseY = mLastMouseY - ypos;
			mLastMouseX = xpos;
			mLastMouseY = ypos;
		}

		void endFrame()
		{
			deltaMouseX = 0;
			deltaMouseY = 0;
			g_memory_zeroMem(keyBeginPressData, sizeof(keyBeginPressData));
		}

		void keyCallback(GLFWwindow* window, int key, int scancode, int action, int mods)
		{
			if (action == GLFW_PRESS)
			{
				keyPressed[key] = true;
				keyBeginPressData[key] = true;
			}
			else if (action == GLFW_RELEASE)
			{
				keyPressed[key] = false;
				keyBeginPressData[key] = false;
			}
		}

		bool isKeyPressed(int key)
		{
			g_logger_assert(key >= 0 && key < GLFW_KEY_LAST, "Invalid key.");
			return keyPressed[key];
		}

		bool keyBeginPress(int key)
		{
			g_logger_assert(key >= 0 && key < GLFW_KEY_LAST, "Invalid key.");
			return keyBeginPressData[key];
		}
	}
}