package ru.requestResponder.GUI;

import org.apache.log4j.Logger;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import ru.library.utils.TimeServices;
import ru.requestResponder.Connector;
import ru.requestResponder.RequestResponder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
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
    DefaultTableModel tableModel;
    //JToggleButton btnStartStop = new JToggleButton("Start");
    //JButton btnClearLog = new JButton("Clear");
    JToggleButton btnStartStop;
    JButton btnClearLog;
    JButton btnAdd;

    JTable table;

    String port = "";

    public ApplicationUI()
    {
        requestResponder = new RequestResponder();
        loadSettings();
        JFrame frame = new JFrame("Request Responder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setOpaque(true);


        initUI();

        this.requestResponder.setPrintArea(txtLog);


        frame.setContentPane(this);
        frame.setMinimumSize(new Dimension(1000, 300));

        frame.pack();
        frame.setVisible(true);

    }


    private void initUI()
    {
        //table = new JTable(data, columnNames)
        table = new JTable(tableModel)
        {
            public boolean editCellAt(int row, int column, java.util.EventObject e) {
                return false;
            }
        };
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPaneTable = new JScrollPane(table);

        //Add the scroll pane to this panel.
        //add(scrollPane);

        //JScrollPane scrollPaneLog = new JScrollPane(txtLog);
        //add(scrollPaneLog);



        /*
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
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 2, 2, 2);
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
        scrollPaneLog.setViewportView(txtLog);
        btnAdd = new JButton();
        btnAdd.setText("Add");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(btnAdd, gbc);
*/



        setLayout(new GridBagLayout());
        btnClearLog = new JButton();
        btnClearLog.setText("Clear Log");
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
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 2, 2, 2);
        add(scrollPaneTable, gbc);
        JScrollPane scrollPaneLog = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(2, 2, 2, 2);
        add(scrollPaneLog, gbc);
        txtLog = new JTextArea();
        scrollPaneLog.setViewportView(txtLog);
        btnAdd = new JButton();
        btnAdd.setText("Add");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);
        add(btnAdd, gbc);

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

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String value = settings.get("Request");
                if (value == null)
                {
                    DefaultTableModel defaultTableModel = (DefaultTableModel)table.getModel();
                    defaultTableModel.addRow(new Object[] {"Request","Respond"});
                    settings.put("Request", "Respond");
                    saveSettings();
                }

            }
        });


        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e))
                {
                    if (e.getClickCount() == 2)
                    {
                        int row = table.getSelectedRow();
                        int column = table.getSelectedColumn();
                        if (!((row == 0) && (column == 0)))
                        {
                            Object dat = table.getModel().getValueAt(row, column);
                            String value = "";
                            if (dat instanceof String)
                            {
                                value = (String)dat;
                            }

                            String result = JOptionPane.showInputDialog("Enter data: ", value);

                            if (result != null)
                            {
                                if ((row == 0) && (column == 1))    //changing port settings
                                {
                                    port = result;
                                }
                            }


                            else
                            {

                                if (result != null)
                                {
                                    TableModel tableModel = table.getModel();
                                    String oldKey = (String)tableModel.getValueAt(row, 0);
                                    oldKey = oldKey.replaceAll("\\\\r", "\r");
                                    oldKey = oldKey.replaceAll("\\\\n", "\n");
                                    result = result.replaceAll("\\\\r", "\r");
                                    result = result.replaceAll("\\\\n", "\n");
                                    tableModel.setValueAt(result, row, column);

                                    List<Map.Entry<String, String>> indexedList = new ArrayList<Map.Entry<String, String>>(settings.entrySet());

                                    if (column == 0)        //if changed key...
                                    {
                                        settings = new LinkedHashMap<String, String>(32);
                                        for (Map.Entry<String, String> entry: indexedList)
                                        {
                                            if (entry.getKey().equals(oldKey))
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
                                        //settings.put(key, result);
                                        settings.put(oldKey, result);
                                    }



                                }
                            }

                            saveSettings();
                            loadSettings();


                        }

                    }
                }

            }
        });

        table.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE)
                {
                    int result = JOptionPane.showConfirmDialog(null, "Are you sure?", "Deleting", JOptionPane.YES_NO_OPTION);

                    System.out.println(result);

                    if (result == JOptionPane.YES_OPTION)
                    {
                        int row = table.getSelectedRow();
                        Object key = table.getModel().getValueAt(row, 0);
                        if (key instanceof String)
                        {
                            if (row > 0)
                            {
                                settings.remove(key);
                                saveSettings();
                                loadSettings();
                            }
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
                synchronized (requestResponder)
                {
                    isWorking = requestResponder.isWorking();
                }
                if (isWorking)
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
                /*
                synchronized (requestResponder)
                {
                    isWorking = requestResponder.isWorking();
                }
                */


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
                request = request.replaceAll("<caret>", "\r");
                request = request.replaceAll("<newline>", "\n");
                String respond = node.valueOf("@respond");
                respond = respond.replaceAll("<caret>", "\r");
                respond = respond.replaceAll("<newline>", "\n");
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
                key = key.replaceAll("\r", "<caret>");
                key = key.replaceAll("\n", "<newline>");
                value = value.replaceAll("\r", "<caret>");
                value = value.replaceAll("\n", "<newline>");
                if (!key.equals("port"))
                {
                    Element data = root.addElement("data");
                    Element req = data.addAttribute("request", key);
                    Element resp = data.addAttribute("respond", value);
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

    private void loadSettings()
    {
        this.settings = parseSettings();
        port = this.settings.get("port");
        String[] portSettings = port.split(Connector.CONNECTOR_SPLIT_STRING);
        this.requestResponder.setPort(port);

        try
        {
            if (port.contains(Connector.SERIAL_PORT_STRING))
            {
                if (portSettings.length > 7) requestResponder.setReceivingTimeout(Integer.parseInt(portSettings[7]));
            }
            else if (port.contains(Connector.TCP_CLIENT_STRING))
            {
                if (portSettings.length > 4) requestResponder.setReceivingTimeout(Integer.parseInt(portSettings[4]));
            }

        }
        catch (Exception e)
        {
            LOG.error("Port settings error: ", e);
        }

        /*
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
        */

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Request");
        tableModel.addColumn("Respond");
        tableModel.addRow(new Object[] { "Connection port: ", port });

        for(Map.Entry entry: settings.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if (!key.equals("port"))
            {
                key = key.replaceAll("\r", "\\\\r");
                key = key.replaceAll("\n", "\\\\n");
                value = value.replaceAll("\r", "\\\\r");
                value = value.replaceAll("\n", "\\\\n");
                tableModel.addRow(new Object[] {key, value});
            }

        }

        if (table != null) table.setModel(tableModel);


        this.requestResponder.setSettings(settings);
    }

}
