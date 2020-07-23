package ru.requestResponder;

import org.apache.log4j.Logger;
import ru.library.hardware.Network.Serial.Driver.Queue;
import ru.library.utils.TimeServices;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

/**
 * Created by valera2 on 22.07.20.
 */
public class RequestResponder {
    public static final Logger LOG = Logger.getLogger(RequestResponder.class);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
    Connector connector = new Connector();
    Queue receivedUueue = connector.getReceivedQueue();
    private int receivingTimeout = 100;
    private LinkedHashMap<String, String> settings = new LinkedHashMap<String, String>(32);
    private CommandProcessor commandProcessor;
    private Object printArea;
    private boolean isWorking = false;
    private boolean isReady = false;
    private String port = "";

    public RequestResponder()
    {
        //settings.put(":0104000A0006EB\r\n", ":01040C00AB00FF000000000000000045\r\n");
        //settings.put(":0204000A0006EA\r\n", ":02040C00AB00FF000000000000000044\r\n");
        //settings.put(":0A04000A0006E2\r\n", ":0A040C00AB00FF00000000000000003C\r\n");
        //settings.put(":0B04000A0006E1\r\n", ":0B040C00AB00FF00000000000000003B\r\n");
        //settings.put(":0C04000A0006E0\r\n", ":0C040C00AB00FF00000000000000003A\r\n");
        /*
        settings = parseSettings();
        settings.put("port",
                Connector.TCP_CLIENT_STRING +
                        Connector.TCP_SPLIT_STRING + "192.168.1.11" +
                        Connector.TCP_SPLIT_STRING + "5000"
        );
        settingsTable = new ApplicationUI(settings, this);
        */
        //startRequestResponder();

    }

    public void setReceivingTimeout(int receivingTimeout)
    {
        this.receivingTimeout = receivingTimeout;
    }

    public void setPort(String port) {this.port = port;}

    public boolean isReady() {return isReady;}

    private void setReady(boolean isReady)
    {
        this.isReady = isReady;
    }

    public void setWorking(boolean isWorking)
    {
        this.isWorking = isWorking;
    }

    public boolean isWorking() {return isWorking;}

    public void setSettings(LinkedHashMap<String, String> settings)
    {
        this.settings = settings;
    }

    public void setPrintArea(Object textArea)
    {
        printArea = textArea;
    }

    public void stop()
    {
        if (commandProcessor != null)
        {
            commandProcessor.stopThread();
        }
    }

    public void startRequestResponder()
    {
        new StartingThread();
    }

    private class StartingThread extends Thread {

        public StartingThread()
        {
            start();
        }

        @Override
        public void run() {
            connector.openPort(port);
            isReady = true;
            if (!connector.isOpened()) TimeServices.wait(500);
            commandProcessor = new CommandProcessor();
        }
    }

    private class CommandProcessor extends Thread {
        private boolean isWorking = false;

        public CommandProcessor()
        {
            isWorking = true;
            start();
        }

        public boolean isWorking() {
            return isWorking;
        }

        public void stopThread() {
            isWorking = false;
            connector.closePort(connector.getPortName());
        }

        @Override
        public void run() {
            printConsole("Command processor started!");
            boolean added = false;
            String command = "";

            if (isWorking && connector.isOpened())
            {
                setWorking(true);
            }
            while (isWorking && connector.isOpened())
            {
                if (receivedUueue != null)
                {
                    if (receivedUueue.hasNext()) TimeServices.wait(receivingTimeout);

                    while(receivedUueue.hasNext())
                    {
                        added = true;
                        command += receivedUueue.get();
                    }
                    if (added)
                    {
                        String date = "[" + sdf.format(System.currentTimeMillis()) + "]: ";
                        System.out.println(date);
                        printConsole("Command: " + command.replaceAll("\r\n", ""));
                        added = false;
                        String respond = settings.get(command);
                        if (respond == null)
                        {
                            print("Unknown request: " + command);
                            printConsole("Unknown request: " + command);
                        }
                        else
                        {
                            printConsole("Respond: " + respond);
                            connector.send(respond);
                        }

                        command = "";
                    }
                }

                TimeServices.wait(10);
            }

            printConsole("CommandProcessor finished!");
            //print("Disconnected!");
            setWorking(false);
            setReady(false);
        }
    }

    public void printConsole(String str)
    {
        String date = "[" + sdf.format(System.currentTimeMillis()) + "]: ";
        str = date + str;
        System.out.println(str);
    }

    public void print(String str)
    {
        if (printArea !=  null)
        {
            if (printArea instanceof JTextArea)
            {
                JTextArea txtLog = (JTextArea)printArea;
                String date = "[" + sdf.format(System.currentTimeMillis()) + "]: ";
                str = date + str;
                if (!txtLog.getText().endsWith("\n"))
                {
                    if (txtLog.getText().length() > 0) txtLog.append("\n" + str);
                    else txtLog.append(str);
                }
                else
                {
                    txtLog.append(str);
                }
            }

        }
    }






}
