# requestResponder
The program connects to the specified port.
If the input stream contains data from the "request" column, then the output stream write data from the "respond" column.

![Alt-текст](https://github.com/VSSavin/requestResponder/blob/master/doc/requestResponder.png "requestResponder")

Supported connections:
- serial port (Linux, Windows)
- tcpip client mode

Configuration in the first line of the table, in column "respond". For change - double click at cell.
For configure serial port use connection string: serial port mode:/dev/ttyUSB0:19200:8:1:0:100

Here:
- /dev/ttyUSB0 - port identificator
- 19200 - baud rate
- 8 - data bits
- 1 - stop bits
- 0 - parity
- 100 - bufferization time

For configure tcpip client mode use connection string: tcpip client mode:192.168.1.11:5000:2000:100

Here:
- 192.168.1.11 - IP address
- 5000 - connection port
- 2000 - connect timeout
- 100 - bufferization time

For adding data to table - click button "Add".
For delete data from table - select row in table and press key "delete" on keyboard.
For change values - double click at cell in table

For starting click button "Start".

All messages writes in the log field under table.
If input stream not contains data from the "request" column - it will be displayed in log field.
