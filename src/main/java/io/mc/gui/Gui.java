package io.mc.gui;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import io.mc.core.Application;
import io.mc.core.Fonts;
import io.mc.input.Input;
import io.mc.renderer.Font;
import io.mc.renderer.RenderableChar;
import io.mc.renderer.Renderer;
import io.mc.renderer.Sprite;
import io.mc.renderer.Style;
import io.mc.renderer.Styles;

public class Gui {
    private static final Logger logger = Logger.getLogger(Gui.class.getName());
    private static List<WindowState> windows = new ArrayList<>(10);
    private static int currentWindow = 0;
    private static Style guiStyle;
    private static Vector2f elementPadding = new Vector2f();
    private static Font defaultFont = new Font();
    private static float defaultTextScale = 0;

    private static final float keyHoldDelayTime = 0.0f;

    public static void init() {
        guiStyle = Styles.defaultStyle;
        currentWindow = -1;
        elementPadding = new Vector2f(0.01f, 0.01f);
        defaultFont = Fonts.loadFont("assets/fonts/Minecraft.tff", 16l);
        defaultTextScale = 0.0025f;
    }

    public static void beginFrame() {
    }

    public static void beginWindow(final Vector2f state, final Vector2f size, int numColumns) {

    }

    public void endWindow() {

    }

    public static void advanceCursor(Vector2f delta) {
        WindowState window = getCurrentWindow();
        window.cursorPos = window.cursorPos.add(new Vector2f(delta.x, -delta.y));
    }

    public static void centerNextElement() {
        WindowState windowState = getCurrentWindow();
        windowState.centerNextElement = true;
    }

    public static void sameLine() {
        WindowState windowState = getCurrentWindow();
        windowState.nextElementSameLine = true;
    }

    public static void image(final Sprite sprite, final Vector2f size) {
        WindowState windowState = getCurrentWindow();
        if (elementExceedsWindowHeight(windowState, size)) {
            return;
        }

        Vector2f spritePosition = getElementPosition(windowState, size);
        Renderer.drawTexture2D(sprite, spritePosition, size, Styles.defaultStyle);
        advanceCursorPastElement(windowState, size);
    }

    public static void label(String text, float scale, float maxHeight) {
        WindowState windowState = getCurrentWindow();
        if (defaultFont != null) {
            logger.info("Default font was invalid");
        }

        Vector2f strSize = defaultFont.getSize(text, scale);
        if (maxHeight != -1.0f && maxHeight > strSize.y) {
            strSize.y += (maxHeight - strSize.y) / 2.0f;
        }
        Vector2f textPos = getElementPosition(windowState, strSize);
        guiStyle.color = 0xffffff;
        Renderer.drawString(text, defaultFont, textPos, scale, guiStyle);
        advanceCursorPastElement(windowState, strSize);
    }

    public static boolean input(final String text, float scale, String inputBuffer, int inputBufferLength,
            Boolean isFocused, boolean drawOutline, int zIndex) {
        // TODO: Add window overrun support (scroll support)
        WindowState windowState = getCurrentWindow();
        sameLine();
        final float textBoxPadding = 0.02f;
        float height = (defaultFont.lineHeight * scale) + (textBoxPadding * 2.0f);
        label(text, scale, height);

        float windowWidthLeft = windowState.size.x - windowState.cursorPos.x;
        Vector2f inputBoxSize = new Vector2f(windowWidthLeft, height);
        Vector2f inputBoxPos = getElementPosition(windowState, inputBoxSize);
        WidgetState state = mouseInAABB(inputBoxPos, inputBoxSize);
        if (Input.isMousePressed(GLFW.GLFW_MOUSE_BUTTON_LEFT) && state == WidgetState.NONE) {
            isFocused = false;
        }

        if (state == WidgetState.CLICK) {
            isFocused = true;
            guiStyle.color = 0x00000099;
        } else if (state == WidgetState.HOVER) {
            guiStyle.color = 0x00000099;
        } else {
            guiStyle.color = 0x00000066;
        }
        Renderer.drawFilledSquare2D(inputBoxPos, inputBoxSize, guiStyle, zIndex - 1);

        guiStyle.color = 0xffffff;
        if (drawOutline) {
            guiStyle.strokeWidth = 0.01f;
            Renderer.drawSquare2D(inputBoxPos, inputBoxSize, guiStyle, zIndex - 2);
        }

        String inputText = inputBuffer;
        Vector2f inputTextStrSize = defaultFont.getSize(inputText, scale);
        Vector2f sizeOfPipeChar = defaultFont.getSize("|", scale);
        if (inputTextStrSize.x >= (inputBoxSize.x - sizeOfPipeChar.x - textBoxPadding)) {
            // Figure out how many chars fit in the line
            inputText = defaultFont.getStringThatFitsIn(inputText, scale,
                    inputBoxSize.x - sizeOfPipeChar.x - textBoxPadding, false);
            inputTextStrSize = defaultFont.getSize(inputText, scale);
        }
        float inputCursorPosX = textBoxPadding;
        if (inputText.length() > 0) {
            // TODO: Simplify this, there has to be a better way to center this stuff
            RenderableChar charInfo = defaultFont.getCharInfo('|');
            float heightOfPipe = charInfo.charSize.y * scale;
            float centeredHeight = (height - heightOfPipe) / 2.0f;
            // Renderer.drawString(inputText,defaultFont,inputBoxPos new
            // Vector2f(inputCursorPosX,centeredHeight+((charInfo.charSize.y-charInfo.bearingY)*scale)/2.0f),scale,guiStyle,zIndex);
            Style style = Styles.defaultStyle;
            style.strokeWidth = 0.01f;
            inputCursorPosX += inputTextStrSize.x + 0.01f;
        }

        boolean res = false;
        if (isFocused) {

            final int maxCursorBlink = 50;
            int cursorBlinkTick = 0;
            cursorBlinkTick = (cursorBlinkTick + 1) % maxCursorBlink;
            if (cursorBlinkTick > maxCursorBlink / 2) {
                // TODO: Simplify this, there has to be a better way to center this stuff
                RenderableChar charInfo = defaultFont.getCharInfo('|');// float heightOfPipe=charInfo.charSize.y*scale;
                // float centeredHeight=(height-heightOfPipe)/2.0f;
                // Renderer.drawString("|",defaultFont,inputBoxPos+Vector2f(inputCursorPosX,centeredHeight+(charInfo.charSize.y-charInfo.bearingY)*scale),scale,guiStyle,zIndex);}

                int c = Input.lastCharPressed();
                if (c != '\0')

                {
                    boolean placedNullChar = false;
                    // for(int i=0;i<inputBuffer.length-1;i++){
                    // if(inputBuffer.charAt(i)=='\0'){inputBuffer[i]=c;inputBuffer[i+1]='\0';placedNullChar=true;break;}}

                    res = placedNullChar;
                    if (!placedNullChar) {
                        logger.severe("Ran out of room in input buffer, terminating string early.");
                        // inputBuffer[inputBufferLength-1]='\0';}
                    }

                    float backspaceDelayTimeLeft = 0.0f;
                    boolean backspacePressedLastFrame = false;
                    if (Input.isKeyPressed(GLFW.GLFW_KEY_BACKSPACE)) {
                        if (backspaceDelayTimeLeft <= 0.0f) {
                            backspaceDelayTimeLeft = keyHoldDelayTime * (backspacePressedLastFrame ? 1.0f : 6.0f);
                            for (int i = 0; i < inputBufferLength; i++) {
                                if (inputBuffer.charAt(i) == '\0') {
                                    if (i > 0) {
                                        var intBuffer = inputBuffer.toCharArray();
                                        intBuffer[i] = '\0';
                                        inputBuffer = new String(intBuffer);
                                    }
                                    break;
                                }
                            }
                            backspacePressedLastFrame = true;
                        }
                        res = true;
                    } else {
                        backspacePressedLastFrame = false;
                        backspaceDelayTimeLeft = -1.0f;
                    }
                    backspaceDelayTimeLeft -= Application.deltaTime;
                }

                Gui.advanceCursorPastElement(windowState, inputBoxSize);
                return res;
            }
        }

        return false;
    }

    static boolean button(final Button button) {
        // TODO: Add window overrun support (scroll support)
        WindowState windowState = getCurrentWindow();
        if (elementExceedsWindowHeight(windowState, button.size)) {
            return false;
        }

        boolean res = false;
        Vector2f buttonPosition = getElementPosition(windowState, button.size);
        WidgetState state = mouseInAABB(buttonPosition, button.size);
        if (state == WidgetState.CLICK) {
            guiStyle.color = button.clickColor;
            res = true;
        } else if (state == WidgetState.HOVER) {
            guiStyle.color = button.hoverColor;
        } else {
            guiStyle.color = button.color;
        }

        Renderer.drawFilledSquare2D(buttonPosition, button.size, guiStyle, -1);

        if (button.text == null) {
            logger.warning("Invalid button text. Cannot be null");
        }
        if (button.font == null) {
            logger.warning("Invalid button font. Cannot be null.");
        }

        // Vector2f strSize = button.font.getSize(button.text, button.textScale);
        // Vector2f textPos = buttonPosition + ((button.size - strSize) * 0.5f);
        // Renderer.drawString(button.text, button.font, textPos, button.textScale,
        // guiStyle);

        advanceCursorPastElement(windowState, button.size);
        return res;
    }

    public static boolean textureButton(final TexturedButton button, boolean isDisabled) {
        WindowState windowState = getCurrentWindow();
        if (elementExceedsWindowHeight(windowState, button.size)) {
            return false;
        }

        final Sprite sprite;
        guiStyle.color = 0xffffff;

        boolean res = false;
        Vector2f buttonPosition = getElementPosition(windowState, button.size);
        WidgetState state = mouseInAABB(buttonPosition, button.size);
        if (state == WidgetState.CLICK || isDisabled) {
            // If the button isn't disabled and they clicked it, then set the result to true
            // Otherwise if it's disabled set the result to false
            res = !isDisabled;
            sprite = button.clickSprite;
        } else if (state == WidgetState.HOVER) {
            sprite = button.hoverSprite;
        } else {
            sprite = button.sprite;
        }

        // Renderer.drawTexture2D(sprite, buttonPosition, button.size, guiStyle, -1);

        // g_logger_assert(button.text != null, "Invalid button text. Cannot be null.");
        // g_logger_assert(button.font != null, "Invalid button font. Cannot be null.");
        // Vector2f strSize = button.font.getSize(button.text, button.textScale);
        // Vector2f textPos = buttonPosition + ((button.size - strSize) * 0.5f);
        // Renderer.drawString(button.text, button.font, textPos, button.textScale,
        // guiStyle);

        advanceCursorPastElement(windowState, button.size);
        return res;
    }

    public static boolean worldSaveItem(final String worldDataPath, final Vector2f size, final Sprite icon,
            boolean isSelected) {
        WindowState windowState = getCurrentWindow();
        if (elementExceedsWindowHeight(windowState, size)) {
            return false;
        }

        guiStyle.color = 0xffffff;

        boolean res = false;
        Vector2f buttonPosition = getElementPosition(windowState, size);
        WidgetState state = mouseInAABB(buttonPosition, size);
        if (state == WidgetState.CLICK) {
            res = true;
        }

        Vector2f imageSize = new Vector2f(size.y - elementPadding.y * 2.0f, size.y - elementPadding.y * 2.0f);
        Vector2f imagePos = buttonPosition.add(elementPadding);
        // if (!icon.texture.isNull()) {
        // Renderer.drawTexture2D(icon, imagePos, imageSize, guiStyle, 0);
        // } else {
        // Renderer.drawFilledSquare2D(imagePos, imageSize, Styles.defaultStyle);
        // }

        // g_logger_assert(worldDataPath != null, "Invalid world data path. Cannot be
        // null.");
        // g_logger_assert(defaultFont != null, "Invalid default font. Cannot be
        // null.");
        Vector2f strSize = defaultFont.getSize(worldDataPath, defaultTextScale);
        Vector2f textPos = imagePos.add(imageSize).add(new Vector2f(elementPadding.x, -(size.y - (strSize.y * 0.5f))));
        String sanitizedWorldDataPath = worldDataPath;
        float maxSize = size.x - defaultFont.getSize("...", defaultTextScale).x - imageSize.x
                - (elementPadding.x * 2.0f);
        if (strSize.x >= maxSize) {
            sanitizedWorldDataPath = defaultFont.getStringThatFitsIn(sanitizedWorldDataPath, defaultTextScale, maxSize)
                    + "...";
        }
        Renderer.drawString(sanitizedWorldDataPath, defaultFont, textPos, defaultTextScale, guiStyle);

        if (isSelected) {

            Style lineStyle = Styles.defaultStyle;
            lineStyle.strokeWidth = 0.01f;
            Renderer.drawSquare2D(buttonPosition, size, lineStyle);
        }

        advanceCursorPastElement(windowState, size);
        return res;
    }

    public static boolean selectableText(final String text, final Vector2f size, boolean isSelected) {
        WindowState windowState = getCurrentWindow();
        if (elementExceedsWindowHeight(windowState, size)) {
            return false;
        }

        guiStyle.color = 0xffffff;

        boolean res = false;
        Vector2f buttonPosition = getElementPosition(windowState, size);
        WidgetState state = mouseInAABB(buttonPosition, size);
        if (state == WidgetState.CLICK) {
            res = true;
        }

        // g_logger_assert(text != null, "Invalid world data path. Cannot be null.");
        // g_logger_assert(defaultFont != null, "Invalid default font. Cannot be
        // null.");
        Vector2f strSize = defaultFont.getSize(text, defaultTextScale);
        Vector2f textPos = buttonPosition.add(new Vector2f(elementPadding.x, (strSize.y * 0.5f)));
        String sanitizedWorldDataPath = (text);
        float maxSize = size.x - defaultFont.getSize("...", defaultTextScale).x - (elementPadding.x * 2.0f);
        if (strSize.x >= maxSize) {
            sanitizedWorldDataPath = defaultFont.getStringThatFitsIn(sanitizedWorldDataPath, defaultTextScale, maxSize)
                    + "...";
        }
        Renderer.drawString(sanitizedWorldDataPath, defaultFont, textPos, defaultTextScale, guiStyle);

        if (isSelected) {

            Style lineStyle = Styles.defaultStyle;
            lineStyle.strokeWidth = 0.01f;
            Renderer.drawSquare2D(buttonPosition, size, lineStyle);
        }

        advanceCursorPastElement(windowState, size);
        return res;
    }

    public static boolean slider(final Slider slider, Float value) {
        WindowState windowState = getCurrentWindow();
        if (elementExceedsWindowHeight(windowState, slider.size)) {
            return false;
        }

        final Vector2f sliderPosition = getElementPosition(windowState, slider.size);
        final Vector2f sliderSize = slider.size;
        float normalizedValue = (value - slider.minValue) / (slider.maxValue - slider.minValue);
        // normalizedValue = glm::clamp(normalizedValue, 0.0f, 1.0f);

        final Vector2f buttonSize = new Vector2f(0.05f, 0.1f);
        float offsetY = (sliderSize.y - buttonSize.y) * 0.5f;
        final Vector2f buttonPosition = sliderPosition.add(normalizedValue * sliderSize.x, offsetY);

        guiStyle.color = 0x1a1a1a;
        Renderer.drawFilledSquare2D(sliderPosition, sliderSize, guiStyle, -1);

        boolean isDragging = false;
        WidgetState state = mouseInAABB(buttonPosition, buttonSize);
        if (state == WidgetState.CLICK) {
            isDragging = true;
        } else if (state == WidgetState.HOVER) {
            guiStyle.color = 0x636363;
            Renderer.drawFilledSquare2D(buttonPosition, buttonSize, guiStyle);
        } else {
            // We are not hovering
            guiStyle.color = 0xe3e3e3;
            Renderer.drawFilledSquare2D(buttonPosition, buttonSize, guiStyle);
        }

        if (isDragging) {
            if (!Input.isMousePressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                isDragging = false;
            }

            // Set the value to the position of the mouse
            float normalizedMouseX = (Input.mouseScreenX - sliderPosition.x) / sliderSize.x;
            value = (normalizedMouseX * (slider.maxValue - slider.minValue)) + slider.minValue;
            // value = glm::clamp(value, slider.minValue, slider.maxValue);

            // We are clicking the button
            guiStyle.color = 0xababab;
            Renderer.drawFilledSquare2D(buttonPosition, buttonSize, guiStyle);
            return true;
        }

        advanceCursorPastElement(windowState, sliderSize);
        return false;
    }

    public static Vector2f getLastElementSize() {
        return windows.get(currentWindow).lastElementSize;
    }

    public static Vector2f getLastElementPosition() {
        return windows.get(currentWindow).position.add(
                new Vector2f(windows.get(currentWindow).lastElementPosition.x,
                        windows.get(currentWindow).lastElementPosition.y
                                - windows.get(currentWindow).lastElementSize.y));
    }

    // ===================================
    // Internal Functions
    // ===================================
    private static WidgetState mouseInAABB(final Vector2f position, final Vector2f size) {
        if (Input.mouseScreenX >= position.x && Input.mouseScreenX <= position.x + size.x
                && Input.mouseScreenY >= position.y && Input.mouseScreenY <= position.y + size.y) {
            // We are hovering over the button
            if (Input.mouseBeginPress(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                // We are clicking the button
                return WidgetState.CLICK;
            } else {
                return WidgetState.HOVER;
            }
        }

        return WidgetState.NONE;
    }

    private static void pushWindow(final WindowState frameState) {
        currentWindow++;
        // g_logger_assert(currentWindow >= 0 && currentWindow < windows.size(), "Window
        // index out of bounds %d",
        // currentWindow);
        windows.set(currentWindow, frameState);
        windows.get(currentWindow).cursorPos = elementPadding;
    }

    private static void popWindow() {
        currentWindow--;
        // g_logger_assert(currentWindow >= -1, "Too many endWindow() calls.");
    }

    private static WindowState getCurrentWindow() {
        // g_logger_assert(currentWindow > -1, "No windows are active. Did you miss a
        // beginWindow() call?");
        // g_logger_assert(currentWindow < windows.size(), "Too many windows.");

        return windows.get(currentWindow);
    }

    private static void advanceCursorPastElement(WindowState window, final Vector2f elementSize) {
        window.lastElementPosition = window.cursorPos.sub(elementPadding);
        window.lastElementSize = elementSize.add(elementPadding);
        if (window.nextElementSameLine) {
            window.cursorPos = window.cursorPos
                    .add(new Vector2f(elementSize.x, 0).add(new Vector2f(elementPadding.x, 0)));
            window.nextElementSameLine = false;
        } else {
            window.cursorPos = new Vector2f(0, window.cursorPos.y).add(elementPadding).sub(0, elementSize.y);
        }
    }

    private static Vector2f getElementPosition(WindowState window, final Vector2f elementSize) {
        if (window.centerNextElement) {
            float windowWidthLeft = window.size.x - window.cursorPos.x;
            window.cursorPos.x += (windowWidthLeft / 2.0f) - (elementSize.x / 2.0f);
            window.centerNextElement = false;
            return window.cursorPos.add(window.position).sub(0, elementSize.y);
        }

        return window.cursorPos.add(window.position).sub(0, elementSize.y);
    }

    private static boolean elementExceedsWindowHeight(WindowState window, final Vector2f elementSize) {
        window.lastElementPosition = window.cursorPos.sub(elementPadding);
        window.lastElementSize = elementSize.add(elementPadding);

        Vector2f nextPos;
        if (window.nextElementSameLine) {
            nextPos = window.cursorPos.add(new Vector2f(elementSize.x, 0)).add(new Vector2f(elementPadding.x, 0));

        } else {
            nextPos = new Vector2f(0, window.cursorPos.y).add(elementPadding).add(0.0f, elementSize.y);
        }

        return false;// nextPos.x >= (window.size.x + window.cursorPos.x) ||
        // nextPos.y >= (window.size.y + window.cursorPos.y) ||
        // nextPos.x < window.cursorPos.x ||
        // nextPos.y < window.cursorPos.y;
    }

}
