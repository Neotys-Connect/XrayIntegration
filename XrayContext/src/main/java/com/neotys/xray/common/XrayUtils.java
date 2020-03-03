package com.neotys.xray.common;

import javax.swing.*;
import java.net.URL;

public  class XrayUtils {
    private static final ImageIcon XRAY_ICON;

    static {

        final URL iconURL = XrayUtils.class.getResource("Logo-Xray.png");
        if (iconURL != null) {
            XRAY_ICON = new ImageIcon(iconURL);
        } else {
            XRAY_ICON = null;
        }
    }

    public XrayUtils() {
    }

    public static ImageIcon getXrayIcon() {
        return XRAY_ICON;
    }

}
