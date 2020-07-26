package ru.requestResponder.utils;

import org.apache.log4j.Logger;
import ru.requestResponder.ui.locale.ApplicationUILocale;

import java.lang.reflect.Field;
import java.util.ResourceBundle;

/**
 * Created by valera on 26.07.20.
 */
public class InterfaceLocalizer {
    private static final Logger LOG = Logger.getLogger(InterfaceLocalizer.class);

    public void loadLanguageResources()
    {
        ResourceBundle bundle = null;
        try {
            bundle = ResourceBundle.getBundle ("ApplicationUI", new UTF8Control());
            Field[] fields = ApplicationUILocale.class.getFields();
            ApplicationUILocale applicationUILocale = new ApplicationUILocale();
            for (Field field: fields)
            {
                try
                {
                    field.setAccessible(true);
                    field.set(applicationUILocale, bundle.getString(field.getName()));
                }
                catch (Exception e)
                {
                    LOG.error("Set field [" + field.getName() + "] error: " + e);
                }

            }

            /*
            ApplicationUILocale.title = bundle.getString("title");
            ApplicationUILocale.btnClear = bundle.getString("btnClear");
            ApplicationUILocale.btnAdd = bundle.getString("btnAdd");
            ApplicationUILocale.btnStartStarted = bundle.getString("btnStartStarted");
            ApplicationUILocale.btnStartStopped = bundle.getString("btnStartStopped");
            ApplicationUILocale.requestColumn = bundle.getString("requestColumn");
            ApplicationUILocale.respondColumn = bundle.getString("respondColumn");
            */
        } catch (Exception ex) {
            LOG.error("Localization error: " , ex);
        }
    }
}
