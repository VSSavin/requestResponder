package ru.requestResponder;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import ru.requestResponder.ui.ApplicationUI;

import javax.swing.*;

/**
 * Created by valera2 on 22.07.20.
 */
public class Launcher {
    private static final Logger LOG = Logger.getLogger(Launcher.class);
    public static void main(String[] args) {

        try {
            DOMConfigurator.configure("./log4j.xml");

            //UIManager.setLookAndFeel(new Plastic3DLookAndFeel());

            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                LOG.error("Nimbuf LAF not found! Try to set default crossplatform LAF!");
                try {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception ex) {
                    LOG.error("Can't set default crossplatform LAF!", ex);

                    // not worth my time
                }
            }



            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    new ApplicationUI();
                }
            });

        }

        catch (Exception e)
        {
            LOG.error("Launcher error: ", e);
        }
    }
}
