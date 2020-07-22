package ru.requestResponder.GUI;

import ru.requestResponder.RequestResponder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by valera2 on 22.07.20.
 */
public class ApplicationUI extends JPanel {
    private LinkedHashMap<String, String> settings;
    RequestResponder requestResponder;
    JTextArea txtLog = new JTextArea();

    public ApplicationUI(LinkedHashMap<String, String> settings, final RequestResponder requestResponder)
    {
        super(new GridLayout(3,0));

        Object[][] data = new Object[settings.size()][2];

        JFrame frame = new JFrame("Настройки");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setOpaque(true);
        this.settings = settings;
        this.requestResponder = requestResponder;

        String port = this.settings.get("port");
        this.settings.remove("port");

        String[] columnNames = {"Request", "Respond"};

        data[0][0] = "Порт подключения:";
        data[0][1] = port;
        int i = 1;
        for(Map.Entry entry: settings.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            data[i][0] = key;
            data[i][1] = value;
            i++;
        }

        final JButton btnStartStop = new JButton("Start");
        add(btnStartStop);

        final JTable table = new JTable(data, columnNames);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);

        JScrollPane scrollPaneLog = new JScrollPane(txtLog);
        add(scrollPaneLog);


        frame.setContentPane(this);
        frame.setMinimumSize(new Dimension(1000, 300));

        //Display the window.
        frame.pack();
        frame.setVisible(true);

        btnStartStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnStartStop.getText().equals("Start"))
                {
                    requestResponder.startRequestResponder();
                    btnStartStop.setText("Stop");
                }
                else
                {
                    requestResponder.stop();
                    btnStartStop.setText("Start");
                }

            }
        });

        //requestResponder.startRequestResponder();
    }

    public void print(String str)
    {
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
