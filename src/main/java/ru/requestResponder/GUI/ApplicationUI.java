package ru.requestResponder.GUI;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import ru.library.utils.TimeServices;
import ru.requestResponder.Connector;
import ru.requestResponder.RequestResponder;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.*;
import java.util.List;

/**
 * Created by valera2 on 22.07.20.
 */
public class ApplicationUI extends JPanel {
    public static final Logger LOG = Logger.getLogger(ApplicationUI.class);
    private LinkedHashMap<String, String> settings;
    private String path = "settings.xml";
    private RequestResponder requestResponder;
    private JTextArea txtLog = new JTextArea();
    private ResponderChecker responderChecker;
    //JToggleButton btnStartStop = new JToggleButton("Start");
    //JButton btnClearLog = new JButton("Clear");
    JToggleButton btnStartStop;
    JButton btnClearLog;

    Object[][] data;
    String[] columnNames = {"Request", "Respond"};
    JTable table;

    String port = "";

    public ApplicationUI(final RequestResponder requestResponder)
    {
        //super(new GridLayout(3,0));

        this.settings = parseSettings();

        JFrame frame = new JFrame("Request Responder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setOpaque(true);
        this.requestResponder = requestResponder;
        this.requestResponder.setPrintArea(txtLog);
        this.requestResponder.setSettings(settings);

        port = this.settings.get("port");
        String[] portSettings = port.split(Connector.TCP_SPLIT_STRING);
        initUI();

        /*
        settings.put("port",
                Connector.TCP_CLIENT_STRING +
                        Connector.TCP_SPLIT_STRING + "192.168.1.11" +
                        Connector.TCP_SPLIT_STRING + "5000"
        );
        */



        try
        {
            if (portSettings.length > 4) requestResponder.setReceivingTimeout(Integer.parseInt(portSettings[4]));
        }
        catch (Exception e)
        {
            LOG.error("Port settings error: ", e);
        }

        this.requestResponder.setPort(port);





        //add(btnStartStop);




        frame.setContentPane(this);
        frame.setMinimumSize(new Dimension(1000, 300));

        //Display the window.
        frame.pack();
        frame.setVisible(true);



        //requestResponder.startRequestResponder();
    }


    private void initUI()
    {
        data = new Object[settings.size()][2];
        //this.settings.remove("port");
        data[0][0] = "Connection port:";
        data[0][1] = port;
        int i = 1;
        for(Map.Entry entry: settings.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if (!key.equals("port"))
            {
                data[i][0] = key;
                data[i][1] = value;
                i++;
            }

        }

        table = new JTable(data, columnNames)
        {
            public boolean editCellAt(int row, int column, java.util.EventObject e) {
                return false;
            }
        };
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPaneTable = new JScrollPane(table);

        //Add the scroll pane to this panel.
        //add(scrollPane);

        //JScrollPane scrollPaneLog = new JScrollPane(txtLog);
        //add(scrollPaneLog);

        setLayout(new GridBagLayout());
        btnClearLog = new JButton();
        btnClearLog.setText("Clear");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(2, 2, 2, 2);
        add(btnClearLog, gbc);
        btnStartStop = new JToggleButton();
        btnStartStop.setText("Start");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(2, 2, 2, 2);
        add(btnStartStop, gbc);
        //table = new JTable();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 2, 2, 2);
        //add(table, gbc);
        add(scrollPaneTable, gbc);
        JScrollPane scrollPaneLog = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 2, 2, 2);
        add(scrollPaneLog, gbc);
        //txtLog = new JTextArea();
        scrollPaneLog.setViewportView(txtLog);



        btnStartStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(btnStartStop.getModel().isSelected())
                {
                    requestResponder.print("Connecting...");
                    requestResponder.startRequestResponder();
                    btnStartStop.setText("Stop");
                    responderChecker = new ResponderChecker();
                }
                else
                {
                    stopAll();
                }
            }
        });

        btnClearLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtLog.setText("");
            }
        });


        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2)
                {
                    int row = table.getSelectedRow();
                    int column = table.getSelectedColumn();
                    Object dat = data[row][column];
                    String value = "";
                    if (dat instanceof String)
                    {
                        value = (String)dat;
                    }

                    String result = JOptionPane.showInputDialog("Enter data: ", value);
                    if (result != null)
                    {
                        if (data[row][column] instanceof String)
                        {
                            TableModel tableModel = table.getModel();
                            value = (String)tableModel.getValueAt(row, column);
                            String key = (String)tableModel.getValueAt(row, 0);
                            tableModel.setValueAt(result, row, column);

                            List<Map.Entry<String, String>> indexedList = new ArrayList<Map.Entry<String, String>>(settings.entrySet());
                            //Map.Entry<String, String> entry = indexedList.get(0);

                            if (column == 0)
                            {
                                LinkedHashMap<String, String> tmp = settings;
                                settings = new LinkedHashMap<String, String>(32);
                                for (Map.Entry<String, String> entry: indexedList)
                                {
                                    if (entry.getKey().equals(key))
                                    {
                                        value = entry.getValue();
                                        settings.put(result, value);
                                    }
                                    else
                                    {
                                        settings.put(entry.getKey(), entry.getValue());
                                    }

                                }

                            }
                            else
                            {
                               // value = settings.get(key);
                                /*
                                for (Map.Entry<String, String> entry: indexedList)
                                {
                                    if (entry.getKey().equals(key))
                                    {
                                        entry.setValue(result);
                                    }
                                }
                                */
                                settings.put(key, result);
                            }

                            saveSettings();
                        }

                    }
                }
            }
        });




    }

    private class ResponderChecker extends Thread {
        private boolean isWorking = false;
        private boolean connectionFailed = true;
        public ResponderChecker()
        {
            isWorking = true;
            start();
        }

        @Override
        public void run() {
            boolean ready = requestResponder.isReady();
            while (!ready)
            {
                TimeServices.wait(10);
                synchronized (requestResponder)
                {
                    ready = requestResponder.isReady();
                }
            }

            if (ready)
            {
                if (requestResponder.isWorking())
                {
                    requestResponder.print("Connected...");
                    connectionFailed = false;
                }
                else
                {
                    requestResponder.print("Connection failed!");
                    connectionFailed = true;
                }

            }

            while(isWorking)
            {
                if (!requestResponder.isWorking())
                {
                    isWorking = false;
                }
                TimeServices.wait(10);
            }

            if (!connectionFailed) requestResponder.print("Disconnected!");

            stopAll();

        }
    }

    private void stopAll()
    {
        requestResponder.stop();
        btnStartStop.setText("Start");
        btnStartStop.getModel().setSelected(false);
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
            java.util.List<Node> nodes = document.selectNodes("/settings/connection" );
            for (Node node : nodes) {
                String port = node.valueOf("@port");
                responds.put("port", port);
            }

            nodes = document.selectNodes("/settings/datas/data" );
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
            fileWriter.write("<settings>\r\n");
            fileWriter.write("<connection port = \"\"/>\r\n");
            fileWriter.write("<datas>\r\n");
            fileWriter.write("</datas>\r\n");
            fileWriter.write("</settings>\r\n");
            fileWriter.close();

        }
        catch (Exception e)
        {
            LOG.error("Creating settings file error: ", e);
        }

    }

    private void saveSettings()
    {
        try {
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement("settings");
            Element connection = root.addElement("connection");
            connection.addAttribute("port", port);
            root = root.addElement("datas");

            for (Map.Entry entry : settings.entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (!key.equals("port"))
                {
                    Element data = root.addElement("data");
                    data.addAttribute("request", key);
                    data.addAttribute("respond", value);
                }

            }

            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            OutputStream os = new FileOutputStream(this.path);
            XMLWriter writer = new XMLWriter(os, format);
            writer.write(document);
            writer.close();

        }
        catch (Exception e)
        {
            LOG.error("Save settings error: ", e);
        }

    }

}
