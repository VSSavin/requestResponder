package ru.requestResponder;

import org.apache.log4j.Logger;
import ru.library.hardware.Network.Serial.Driver.Queue;
import ru.library.utils.TimeServices;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import ru.requestResponder.GUI.ApplicationUI;

/**
 * Created by valera2 on 22.07.20.
 */
public class RequestResponder {
    public static final Logger LOG = Logger.getLogger(RequestResponder.class);
    Connector connector = new Connector();
    Queue receivedUueue = connector.getReceivedQueue();
    private final int RECEIVING_TIMEOUT = 100;
    LinkedHashMap<String, String> settings = new LinkedHashMap<String, String>(32);
    String path = "settings.xml";
    ApplicationUI settingsTable;
    private CommandProcessor commandProcessor;

    public RequestResponder()
    {
        //settings.put(":0104000A0006EB\r\n", ":01040C00AB00FF000000000000000045\r\n");
        //settings.put(":0204000A0006EA\r\n", ":02040C00AB00FF000000000000000044\r\n");
        //settings.put(":0A04000A0006E2\r\n", ":0A040C00AB00FF00000000000000003C\r\n");
        //settings.put(":0B04000A0006E1\r\n", ":0B040C00AB00FF00000000000000003B\r\n");
        //settings.put(":0C04000A0006E0\r\n", ":0C040C00AB00FF00000000000000003A\r\n");
        settings = parseSettings();
        settings.put("port",
                Connector.TCP_CLIENT_STRING +
                        Connector.TCP_SPLIT_STRING + "192.168.1.11" +
                        Connector.TCP_SPLIT_STRING + "5000"
        );
        settingsTable = new ApplicationUI(settings, this);
        //startRequestResponder();

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
        connector.openPort(
                Connector.TCP_CLIENT_STRING +
                        Connector.TCP_SPLIT_STRING + "192.168.1.11" +
                        Connector.TCP_SPLIT_STRING + "5000"
        );

        commandProcessor = new CommandProcessor();
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
            this.isWorking = false;
            connector.closePort(connector.getPortName());
        }

        @Override
        public void run() {
            boolean added = false;
            String command = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            if (isWorking && connector.isOpened())
            {
                String date = "[" + sdf.format(System.currentTimeMillis()) + "]: ";
                settingsTable.print(date + "Connected!");
            }
            while (isWorking && connector.isOpened())
            {
                if (receivedUueue != null)
                {
                    if (receivedUueue.hasNext()) TimeServices.wait(RECEIVING_TIMEOUT);

                    while(receivedUueue.hasNext())
                    {
                        added = true;
                        command += receivedUueue.get();
                    }
                    if (added)
                    {
                        String date = "[" + sdf.format(System.currentTimeMillis()) + "]: ";
                        System.out.println(date);
                        System.out.println("Command: " + command.replaceAll("\r\n", ""));
                        added = false;
                        String respond = settings.get(command);
                        if (respond == null)
                        {
                            if (settingsTable != null)
                            {
                                settingsTable.print(date + " Unknown request: " + command);
                            }
                            System.out.println("Unknown request: " + command);
                        }
                        else
                        {
                            System.out.println("Respond: " + respond);
                            connector.send(respond);
                        }

                        command = "";
                    }
                }

                TimeServices.wait(10);
            }

            System.out.println("CommandProcessor finished!");
            if (settingsTable != null)
            {
                String date = "[" + sdf.format(System.currentTimeMillis()) + "]: ";
                settingsTable.print(date + "Disconnected!");
            }
        }
    }


    private LinkedHashMap<String, String> parseSettings()
    {
        LinkedHashMap<String, String> responds = new LinkedHashMap<String, String>(32);
        try {
            File inputFile = new File(this.path);
            if (!inputFile.exists())
            {
                createSettingsFile();
            }
            SAXReader reader = new SAXReader();
            Document document = reader.read( inputFile );
            //Element classElement = document.getRootElement();
            List<Node> nodes = document.selectNodes("/datas/data" );
            System.out.println("----------------------------");
            for (Node node : nodes) {
                String request = node.valueOf("@request");
                String respond = node.valueOf("@respond");
                responds.put(request, respond);
            }
        } catch (Exception e) {
            LOG.error("Devices parse error: ", e);
        }

        return responds;
    }

    private void createSettingsFile()
    {
        try
        {
            File file = new File(this.path);
            FileWriter fileWriter = new FileWriter(this.path);
            file.createNewFile();
            fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
            fileWriter.close();

        }
        catch (Exception e)
        {
            LOG.error("Creating settings file error: ", e);
        }

    }



}
