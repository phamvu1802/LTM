package client;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import data.DataFile;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import tags.Decode;
import tags.Encode;
import tags.Tags;

public class ChatFrame extends JFrame {

    /**
     *
     */
    // Socket
    private static String URL_DIR = System.getProperty("user.dir");
    private Socket socketChat;
    private String nameUser = "", nameGuest = "", nameFile = "";
    public boolean isStop = false, isSendFile = false, isReceiveFile = false;
//	private JFrame frameChat;
    private ChatRoom chat;
    private int portServer = 0;
    private int clo;

    // Frame
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtMessage;
    private JTextPane txtDisplayMessage;
    private JButton btnSendFile;
    private JLabel lblReceive;
    private ChatFrame frame = this;
    private JProgressBar progressBar;
    JButton btnSend;

    //caro
    public static JFrame f;
    JButton[][] bt;
    static boolean flat = false;
    boolean winner;
    
    Timer thoigian;
    String temp = "", strNhan = "";
    Integer second, minute;
    JLabel demthoigian;


    JPanel p;
    int xx, yy, x, y;

    int[][] matran;
    int[][] matrandanh;
    MenuBar menubar;

    OutputStream os;// ....
    InputStream is;// ......
    ObjectOutputStream oos;// .........
    ObjectInputStream ois;// 

    /////////////////////
    public ChatFrame(String user, String guest, Socket socket, int port, int cl) throws Exception {
        super();
        nameUser = user;
        nameGuest = guest;
        socketChat = socket;
        clo = cl;
        frame = new ChatFrame(user, guest, socket, port, cl, cl);
        frame.setVisible(true);
    }

    public ChatFrame(String user, String guest, Socket socket, int port, int cl, int g) throws Exception {
        // TODO Auto-generated constructor stub,in cl
        super();
        nameUser = user;
        nameGuest = guest;
        socketChat = socket;
        this.portServer = port;
        clo = cl;
        System.out.println("user: " + user);
        System.out.println("Guest: " + guest);
        System.out.println("Port: " + port);
        System.out.println("Socket: " + socket);
        chat = new ChatRoom(socketChat, nameUser, nameGuest);
        chat.start();
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    initial();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateChat_receive(String msg) {
        appendToPane(txtDisplayMessage, "<div class='left' style='width: 40%; background-color: #f1f0f0;'>" + "    "
                + msg + "<br>" + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "</div>");
    }

    public void updateChat_send(String msg) {
        appendToPane(txtDisplayMessage,
                "<table class='bang' style='color: white; clear:both; width: 100%;'>" + "<tr align='right'>"
                + "<td style='width: 59%; '></td>" + "<td style='width: 40%; background-color: #0084ff;'>"
                + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + "<br>" + msg
                + "</td> </tr>" + "</table>");
    }




    /**
     * Create the frame.
     */
    public void initial() {
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                try {
                    isStop = true;
                    frame.dispose();
      
                    chat.stopChat();
                    System.gc();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        setTitle("Chat Frame");
        setBounds(100, 100, 1076, 595);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        x = 25;
        y = 25;

        matran = new int[x][y];
        matrandanh = new int[x][y];
        menubar = new MenuBar();
        p = new JPanel();
        p.setBounds(0,10, 400, 400);
        p.setLayout(new GridLayout(x, y));
        frame.setMenuBar(menubar);

        Menu game = new Menu("Game");
        menubar.add(game);
        Menu help = new Menu("Help");
        menubar.add(help);
        MenuItem helpItem = new MenuItem("Help");
        help.add(helpItem);
        MenuItem about = new MenuItem("About ..");
        help.add(about);
        help.addSeparator();
        MenuItem newItem = new MenuItem("New Game");
        game.add(newItem);
        MenuItem exit = new MenuItem("Exit");
        game.add(exit);
        game.addSeparator();
        newItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                newgame();
                try {
                    oos.writeObject("newgame,123");
                } catch (IOException ie) {
                    //
                }
            }

        });

        JPanel panel = new JPanel();
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setBounds(400, 10, 567, 67);
        contentPane.add(panel);
        panel.setLayout(null);

        JLabel nameLabel = new JLabel("Player: " + nameGuest);
        nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 28));
        nameLabel.setToolTipText("");
        nameLabel.setBounds(10, 10, 180, 38);
        panel.add(nameLabel);

        JPanel panel_6 = new JPanel();
        txtDisplayMessage = new JTextPane();
        txtDisplayMessage.setEditable(false);
        txtDisplayMessage.setContentType("text/html");
        txtDisplayMessage.setBackground(Color.BLACK);
        txtDisplayMessage.setForeground(Color.WHITE);
        txtDisplayMessage.setFont(new Font("Courier New", Font.PLAIN, 18));
        appendToPane(txtDisplayMessage, "<div class='clear' style='background-color:white'></div>"); // set default

        panel_6.setBounds(400, 66, 567, 323);
        panel_6.setLayout(null);
        JScrollPane scrollPane = new JScrollPane(txtDisplayMessage);
        scrollPane.setBounds(0, 0, 567, 323);
        panel_6.add(scrollPane);
        contentPane.add(panel_6);



        JPanel panel_3 = new JPanel();
        panel_3.setBounds(400, 446, 550, 73);
        contentPane.add(panel_3);
        panel_3.setLayout(null);

        btnSend = new JButton();
        btnSend.setBorder(new EmptyBorder(0, 0, 0, 0));
        btnSend.setContentAreaFilled(false);
        btnSend.setIcon(new ImageIcon(ChatFrame.class.getResource("/image/send.png")));
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = txtMessage.getText();
                // Clear messageif
                if (msg.equals("")) {
                    return;
                }

                if (e.getSource().equals(btnSend)) {
                    try {


                        oos.writeObject("chat," + msg);

                        txtMessage.setText("");

                    } catch (Exception r) {
                        r.printStackTrace();
                    }
                    updateChat_send(msg);
                }
            }
        });
        btnSend.setBounds(480, 5, 64, 64);
        panel_3.add(btnSend);

        txtMessage = new JTextField();
        txtMessage.setBounds(20, 5, 433, 58);
        panel_3.add(txtMessage);
        txtMessage.setColumns(10);
        txtMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnSend.doClick();
                }
            }
        });

        demthoigian = new JLabel("Thời Gian:");
        demthoigian.setFont(new Font("TimesRoman", Font.ITALIC, 16));
        demthoigian.setForeground(Color.BLACK);
        contentPane.add(demthoigian);
        demthoigian.setBounds(0, 430, 300, 50);
        
        second = 0;
        minute = 0;
        thoigian = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String temp = minute.toString();
                String temp1 = second.toString();
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                if (temp1.length() == 1) {
                    temp1 = "0" + temp1;
                }
                /*if (second == 59) {
                                demthoigian.setText("Thời Gian:" + temp + ":" + temp1);
                                minute++;
                                second = 0;
                        } else {
                                demthoigian.setText("Thời Gian:" + temp + ":" + temp1);
                                second++;
                        }*/

                if (second == 30) {
                    try {
                        oos.writeObject("checkwin,123");
                    } catch (IOException ex) {
                    }
                    Object[] options = {"Dong y", "Huy bo"};
                    int m = JOptionPane.showConfirmDialog(f,
                            "Ban da thua.Ban co muon choi lai khong?", "Thong bao",
                            JOptionPane.YES_NO_OPTION);
                    if (m == JOptionPane.YES_OPTION) {
                        second = 0;
                        minute = 0;
                        setVisiblePanel(p);
                        newgame();
                        try {
                            oos.writeObject("newgame,123");
                        } catch (IOException ie) {
                            //
                        }
                    } else if (m == JOptionPane.NO_OPTION) {
                        thoigian.stop();
                    }
                } else {
                    demthoigian.setText("Thời Gian:" + temp + ":" + temp1);
                    second++;
                }

            }

        });

        /////////////////////////////////
        bt = new JButton[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                final int a = i, b = j;
                bt[a][b] = new JButton();
                bt[a][b].setBackground(Color.LIGHT_GRAY);
                bt[a][b].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        flat = true;// server da click

                        matrandanh[a][b] = 1;
                        bt[a][b].setEnabled(false);
                        //bt[a][b].setIcon(new ImageIcon(getClass().getResource("o.png")));
                        if (clo == 1) {
                            bt[a][b].setBackground(Color.RED);
                        } else {
                            bt[a][b].setBackground(Color.BLACK);
                        }
                        try {
                            oos.writeObject("caro," + a + "," + b);
                            setEnableButton(false);
                        } catch (Exception ie) {
                            ie.printStackTrace();
                        }

                    }

                });
                p.add(bt[a][b]);
                p.setVisible(false);
                p.setVisible(true);
            }
            contentPane.add(p);
            
        }
    }

    public class ChatRoom extends Thread {

        private Socket connect;
        private ObjectOutputStream outPeer;
        private ObjectInputStream inPeer;

        public ChatRoom(Socket connection, String name, String guest) throws Exception {
            connect = new Socket();
            connect = connection;
            nameGuest = guest;
            System.out.println(connect);
        }

        @Override
        public void run() {
            super.run();
            System.out.println("Chat Room start");
            OutputStream out = null;
            while (!isStop) {
                try {

                    os = connect.getOutputStream();
                    is = connect.getInputStream();
                    oos = new ObjectOutputStream(os);
                    ois = new ObjectInputStream(is);
                    while (true) {
                        String stream = ois.readObject().toString();
                        String[] data = stream.split(",");
                        if (data[0].equals("chat")) {
                            String msg1=data[1];
                            updateChat_receive(msg1);
                            
                        } else if (data[0].equals("caro")) {
                             thoigian.start();
                                    second = 0;
                                    minute = 0;
                            caro(data[1], data[2]);
                            setEnableButton(true);

                            if (winner == false) {
                                setEnableButton(true);
                            }
                        } else if (data[0].equals("newgame")) {
                            newgame();
                            second = 0;
                            minute = 0;
                        } else if (data[0].equals("checkwin")) {
                            thoigian.stop();
                        }
                    }

                } catch (Exception ie) {
                    // ie.printStackTrace();
                }
            }

        }



        public void stopChat() {
            try {
                connect.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void appendToPane(JTextPane tp, String msg) {
        HTMLDocument doc = (HTMLDocument) tp.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit) tp.getEditorKit();
        try {

            editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
            tp.setCaretPosition(doc.getLength());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    ////////////////////////////////////////////////////////////////

    public void newgame() {
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                bt[i][j].setBackground(Color.LIGHT_GRAY);
                matran[i][j] = 0;
                matrandanh[i][j] = 0;
            }
        }
        setEnableButton(true);
        second = 0;
        minute = 0;
        thoigian.stop();

    }

    public void setVisiblePanel(JPanel pHienthi) {
        contentPane.add(pHienthi);
        pHienthi.setVisible(true);
        pHienthi.updateUI();// ......

    }

    public void setEnableButton(boolean b) {
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                if (matrandanh[i][j] == 0) {
                    bt[i][j].setEnabled(b);
                }
            }
        }
    }

    //thuat toan tinh thang thua
    public int checkHang() {
        int win = 0, hang = 0, n = 0, k = 0;
        boolean check = false;
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                if (check) {
                    if (matran[i][j] == 1) {
                        hang++;
                        if (hang > 4) {
                            win = 1;
                            break;
                        }
                        continue;
                    } else {
                        check = false;
                        hang = 0;
                    }
                }
                if (matran[i][j] == 1) {
                    check = true;
                    hang++;
                } else {
                    check = false;
                }
            }
            hang = 0;
        }
        return win;
    }

    public int checkCot() {
        int win = 0, cot = 0;
        boolean check = false;
        for (int j = 0; j < y; j++) {
            for (int i = 0; i < x; i++) {
                if (check) {
                    if (matran[i][j] == 1) {
                        cot++;
                        if (cot > 4) {
                            win = 1;
                            break;
                        }
                        continue;
                    } else {
                        check = false;
                        cot = 0;
                    }
                }
                if (matran[i][j] == 1) {
                    check = true;
                    cot++;
                } else {
                    check = false;
                }
            }
            cot = 0;
        }
        return win;
    }

    public int checkCheoPhai() {
        int win = 0, cheop = 0, n = 0, k = 0;
        boolean check = false;
        for (int i = x - 1; i >= 0; i--) {
            for (int j = 0; j < y; j++) {
                if (check) {
                    if (matran[n - j][j] == 1) {
                        cheop++;
                        if (cheop > 4) {
                            win = 1;
                            break;
                        }
                        continue;
                    } else {
                        check = false;
                        cheop = 0;
                    }
                }
                if (matran[i][j] == 1) {
                    n = i + j;
                    check = true;
                    cheop++;
                } else {
                    check = false;
                }
            }
            cheop = 0;
            check = false;
        }
        return win;
    }

    public int checkCheoTrai() {
        int win = 0, cheot = 0, n = 0;
        boolean check = false;
        for (int i = 0; i < x; i++) {
            for (int j = y - 1; j >= 0; j--) {
                if (check) {
                    if (matran[n - j - 2 * cheot][j] == 1) {
                        cheot++;
                        System.out.print("+" + j);
                        if (cheot > 4) {
                            win = 1;
                            break;
                        }
                        continue;
                    } else {
                        check = false;
                        cheot = 0;
                    }
                }
                if (matran[i][j] == 1) {
                    n = i + j;
                    check = true;
                    cheot++;
                } else {
                    check = false;
                }
            }
            n = 0;
            cheot = 0;
            check = false;
        }
        return win;
    }

    public void caro(String x, String y) {
        xx = Integer.parseInt(x);
        yy = Integer.parseInt(y);
        // danh dau vi tri danh
        matran[xx][yy] = 1;
        matrandanh[xx][yy] = 1;
        bt[xx][yy].setEnabled(false);
        //bt[xx][yy].setIcon(new ImageIcon("x.png"));
        if (clo == 1) {
            bt[xx][yy].setBackground(Color.BLACK);

        } else {
            bt[xx][yy].setBackground(Color.RED);
        }

        // Kiem tra thang hay chua
        System.out.println("CheckH:" + checkHang());
        System.out.println("CheckC:" + checkCot());
        System.out.println("CheckCp:" + checkCheoPhai());
        System.out.println("CheckCt:" + checkCheoTrai());
        winner = (checkHang() == 1 || checkCot() == 1 || checkCheoPhai() == 1 || checkCheoTrai() == 1);
        if (checkHang() == 1 || checkCot() == 1 || checkCheoPhai() == 1
                || checkCheoTrai() == 1) {
            setEnableButton(false);

            try {
                oos.writeObject("checkwin,123");
            } catch (IOException ex) {
            }
            Object[] options = {"Dong y", "Huy bo"};
            int m = JOptionPane.showConfirmDialog(f,
                    "Ban da thua.Ban co muon choi lai khong?", "Thong bao",
                    JOptionPane.YES_NO_OPTION);
            if (m == JOptionPane.YES_OPTION) {
                second = 0;
                minute = 0;
                setVisiblePanel(p);
                newgame();
                try {
                    oos.writeObject("newgame,123");
                } catch (IOException ie) {
                    //
                }
            } else if (m == JOptionPane.NO_OPTION) {
                thoigian.stop();
            }
        }

    }

}
