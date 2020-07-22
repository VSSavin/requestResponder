package ru.requestResponder;

import org.apache.log4j.Logger;
import ru.library.hardware.Network.Serial.Driver.Driver;
import ru.library.hardware.Network.Serial.Driver.Queue;
import ru.library.hardware.Network.Serial.Driver.SerialPort;
import ru.library.hardware.Network.TCPIP.TCPIPClient;
import ru.library.utils.TimeServices;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by valera on 22.07.20.
 */
public class Connector {
    public static final Logger LOG = Logger.getLogger(Connector.class);
    public static final int PORT_TYPE_SERIAL = 0;
    public static final int PORT_TYPE_TCP_CLIENT = 1;

    public static String TCP_CLIENT_STRING = "tcpip client mode";
    public static final String TCP_SPLIT_STRING = ":";

    private Queue receivedQueue = new Queue(32768);

    private int portType = PORT_TYPE_SERIAL;

    Date date = new Date();

    private int connectTimeout = 2000;
    private boolean isOpened = false;
    private String portName = "";
    private Driver drv;
    private SerialPort sp;
    private TCPIPClient tcpipClient;
    private portReader pr;

    private int baudRate = 0, dataBits = 8, stopBits = 1;
    private int parity = 0;

    public Connector()
    {
        //drv = new Driver(true);
        sp = new SerialPort();
    }

    public Queue getReceivedQueue() {return receivedQueue;}


    public void setRTS(boolean value)
    {
        try {
            sp.setRTS(value);
        } catch (Exception e) {
            LOG.error("Set RTS error: ", e);
        }
    }

    public void setDTR(boolean value)
    {
        try {
            sp.setDTR(value);
        } catch (Exception e) {
            LOG.error("Set DTR error: ", e);
        }
    }

    public void setConnectTimeout(int connectTimeout) {this.connectTimeout = connectTimeout;}

    public boolean openPort(String portName)
    {
        this.portName = portName;
        portType = PORT_TYPE_TCP_CLIENT;
        try
        {
            if (portName.toLowerCase().contains(TCP_CLIENT_STRING))
            {
                String[] splitted = portName.toLowerCase().split(TCP_SPLIT_STRING);
                String host = "127.0.0.1";
                int port = 5000;
                if (splitted.length > 2)
                {
                    host = splitted[1];
                    host = host.replaceAll(" ", "");
                    String portString = splitted[2].replaceAll(" ", "");
                    port = Integer.parseInt(portString);
                    tcpipClient = new TCPIPClient(host, port, connectTimeout, true);
                    int tmpTimeout = connectTimeout;
                    while(!tcpipClient.isWorking() && (tmpTimeout > 0))
                    {
                        tmpTimeout = tmpTimeout - 100;
                        TimeServices.wait(100);
                    }
                    if (tcpipClient.isWorking())
                    {
                        pr = new portReader();
                        isOpened = true;
                    }
                    else
                    {
                        isOpened = false;
                    }

                }

            }
        }

        catch (Exception ex)
        {
            return false;
        }

        return isOpened;
    }

    public boolean openPort(String portName, int baudRate, int dataBits, int stopBits, int parity)
    {
        //this.portName = portName;
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
        try {
            portType = PORT_TYPE_SERIAL;
            sp.SerialReceivedQueue.Clear();
            sp.openPort(portName, baudRate, dataBits, stopBits, parity);
            this.portName = portName;
            pr = new portReader();
            isOpened = true;
        }
        catch (Exception ex)
        {
            this.portName = "";
            return false;
        }

        return true;
    }

    public ArrayList<String> getPortsList()
    {
        return sp.getPortsList();
    }

    public void closePort(String portName)
    {
        if (portName.toLowerCase().contains(TCP_CLIENT_STRING))
        {
            tcpipClient.closeTCPClientConnect();
            tcpipClient = null;
            isOpened = false;
        }
        else if ((portName.toLowerCase().contains("/dev/tty")) || (portName.toLowerCase().contains("COM")))
        {
            sp.closePort(portName);
            pr.stopThread();
            pr = null;
            isOpened = false;
            this.portName = "";
        }
    }
    public void send(String request)
    {
        //date = new Date();
        //System.err.println("Sending time: " + date.getTime());
        if (isOpened)
        {
            if ((portName.toLowerCase().contains("/dev/tty")) || (portName.toLowerCase().contains("COM")))
            {
                sp.send(request);
            }
            else if (portName.toLowerCase().contains(TCP_CLIENT_STRING))
            {
                tcpipClient.send(request);
            }
        }
    }

    public Driver getSerialDriver()
    {
        return drv;
    }

    public void setSerialDriver(Driver serialDriver)
    {
        drv = serialDriver;
    }

    public String getPortName() {return portName;}

    public boolean isOpened() {return isOpened;}

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public int getDataBits() {
        return dataBits;
    }

    public int getStopBits() {
        return stopBits;
    }

    public int getParity() {
        return parity;
    }

    private class portReader extends Thread
    {
        private boolean isWorking = false;

        public portReader()
        {
            start();
        }

        @Override
        public void run()
        {
            isWorking = true;
            while(isWorking)
            {
                try
                {
                    if (sp != null)
                    {
                        synchronized (sp.SerialReceivedQueue)
                        {
                            //if (sp.receivedQueue.hasNext())
                            while (sp.SerialReceivedQueue.hasNext())
                            {
                                synchronized (receivedQueue)
                                {
                                    receivedQueue.add(sp.SerialReceivedQueue.get());
                                }
                            }
                        }
                    }

                    if (tcpipClient != null)
                    {
                        if (!tcpipClient.isWorking())
                        {
                            isWorking = false;
                            isOpened = false;
                        }
                        if (tcpipClient.TCPClientReceivedQueue != null)
                        {
                            synchronized (tcpipClient.TCPClientReceivedQueue)
                            {
                                //if (tcpipClient.TCPClientReceivedQueue.hasNext())
                                while (tcpipClient.TCPClientReceivedQueue.hasNext())
                                {
                                    synchronized (receivedQueue)
                                    {
                                        receivedQueue.add(tcpipClient.TCPClientReceivedQueue.get());
                                    }
                                }
                            }
                        }

                    }
                }
                catch (Exception e)
                {
                    //System.err.println("["+ Thread.currentThread().getName() + "] " + e);
                    LOG.error("[" + Thread.currentThread().getName() + "]: ", e);
                }

                TimeServices.wait(10);
            }
            System.out.println("PortReader finished!");
        }

        public void stopThread() {isWorking = false;}
    }
}