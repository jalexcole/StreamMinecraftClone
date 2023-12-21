package io.mc;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.joml.Vector4f;

public class Core {
    private static final Logger logger = Logger.getLogger(Core.class.getName());

    public static int hexToInt(char hexCode) {
        switch (hexCode) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return hexCode - '0';
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
                return hexCode - 'A' + 10;
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
                return hexCode - 'a' + 10;
        }

        logger.warning("Invalid hex character: " + hexCode);
        return -1;
    }
}
