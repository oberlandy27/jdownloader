package jd.gui.swing.jdgui.views;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;

import javax.swing.Icon;

import jd.controlling.LinkGrabberController;
import jd.controlling.LinkGrabberControllerEvent;
import jd.controlling.LinkGrabberControllerListener;
import jd.gui.swing.jdgui.interfaces.View;
import jd.gui.swing.jdgui.views.info.LinkGrabberInfoPanel;
import jd.gui.swing.jdgui.views.linkgrabberview.LinkGrabberPanel;
import jd.gui.swing.jdgui.views.toolbar.ViewToolbar;
import jd.utils.JDTheme;
import jd.utils.locale.JDL;

public class LinkgrabberView extends View {

    private static final long serialVersionUID = -8027069594232979742L;

    /**
     * DO NOT MOVE THIS CONSTANT. IT's important to have it in this file for the
     * LFE to parse JDL Keys correct
     */
    private static final String IDENT_PREFIX = "jd.gui.swing.jdgui.views.linkgrabberview.";

    private AWTEventListener ael;

    public LinkgrabberView() {

        super();
        this.setContent(LinkGrabberPanel.getLinkGrabber());
        this.setDefaultInfoPanel(new LinkGrabberInfoPanel());
        ViewToolbar toolbar = new ViewToolbar();

        toolbar.setList(new String[] {
                "action.addurl", "action.load"
        });
      
        this.setToolBar(toolbar);
//globaler keylistener
        ael = new AWTEventListener() {
            public void eventDispatched(AWTEvent event) {
                if (event.getID() == KeyEvent.KEY_TYPED) {
                    char keycode = ((KeyEvent) event).getKeyChar();
                    if ( keycode== '\r'||keycode=='\n') {
                        
                        LinkGrabberPanel.getLinkGrabber().confirmButton.doClick(500);
                    }

                }
            }
        };

        LinkGrabberController.getInstance().addListener(new LinkGrabberControllerListener() {
            public void onLinkGrabberControllerEvent(LinkGrabberControllerEvent event) {
                switch (event.getID()) {
                case LinkGrabberControllerEvent.ADDED:
                    // taskPane.switcher(dlTskPane);
                    break;
                }
            }
        });
    }

    @Override
    public Icon getIcon() {
        return JDTheme.II("gui.images.taskpanes.linkgrabber", ICON_SIZE, ICON_SIZE);
    }

    @Override
    public String getTitle() {
        return JDL.L(IDENT_PREFIX + "tab.title", "Linkgrabber");
    }

    @Override
    public String getTooltip() {
        return JDL.L(IDENT_PREFIX + "tab.tooltip", "Collect, add and select links and URLs");
    }

    @Override
    protected void onHide() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(ael);
    }

    @Override
    protected void onShow() {

        Toolkit.getDefaultToolkit().addAWTEventListener(ael, AWTEvent.KEY_EVENT_MASK);
    }

}