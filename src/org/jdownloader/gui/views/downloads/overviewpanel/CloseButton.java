package org.jdownloader.gui.views.downloads.overviewpanel;

import org.appwork.swing.components.ExtButton;
import org.jdownloader.actions.AppAction;
import org.jdownloader.gui.IconKey;
import org.jdownloader.images.AbstractIcon;

public class CloseButton extends ExtButton {

    private AbstractIcon on;
    private AbstractIcon off;

    public CloseButton(AppAction appAction) {
        super(appAction);
        setRolloverEffectEnabled(true);
        onRollOut();
        setBorderPainted(false);
        setContentAreaFilled(false);
        on = new AbstractIcon(IconKey.ICON_CLOSE, 10);
        off = new AbstractIcon(IconKey.ICON_CLOSE_ON, 10);
    }

    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
    }

    protected void onReleased() {
        onRollOut();

    }

    private static final long serialVersionUID = 1L;

    protected void onRollOut() {
        setIcon(on);

    }

    /**
     *
     */
    protected void onRollOver() {
        setIcon(off);

    }
}
