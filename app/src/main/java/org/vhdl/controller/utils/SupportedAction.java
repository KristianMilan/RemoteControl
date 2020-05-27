package org.vhdl.controller.utils;

public class SupportedAction {
    private static String[] actions = {"command"};
    private static String[] commands = {
            "POWER_ON",
            "POWER_OFF",
            "AUTO_BRIGHTNESS_ON",
            "AUTO_BRIGHTNESS_OFF",
            "BRIGHTNESS_SET"
    };
    public static String[] getAllowedcommend(){return commands;}
    public static String[] getAllowed(){return actions;}
}
