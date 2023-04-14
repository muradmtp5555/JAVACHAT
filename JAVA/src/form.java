import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class form extends JFrame implements Runnable {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JLabel lblNewLabel;
    private JTextField textField;
    private JLabel lblNewLabel_1;
    private JTabbedPane tabbedPane;
    private JButton btnNewButton;

    // _________________________________________
    form thisManager;
    ServerSocket socket = null;
    // Đây là khởi tạo biến socket có kiểu dữ liệu là ServerSocket, một lớp trong Java được sử dụng để tạo
    // một socket server, cho phép kết nối từ phía client đến server.
    // Biến socket được gán giá trị null, tức là chưa được khởi tạo một đối tượng ServerSocket cụ thể.
    BufferedReader br = null;
    //Đây là khởi tạo biến br có kiểu dữ liệu là BufferedReader, một lớp trong Java được sử dụng để đọc dữ liệu từ
    // một luồng đầu vào (input stream).
    // Biến br cũng được gán giá trị null, tức là chưa được khởi tạo một đối tượng BufferedReader cụ thể.
    Thread t;
    private JLabel lblNewLabel_2;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    form frame = new form();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public form() {
        initComponents();
        thisManager = this;
    }

    private void initComponents() {
        setTitle("MANAGER");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 835, 674);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        contentPane.add(getLblNewLabel());
        contentPane.add(getTextField());
        contentPane.add(getTabbedPane());
        contentPane.add(getBtnNewButton());
    }

    public JLabel getLblNewLabel() {
        if (lblNewLabel == null) {
            lblNewLabel = new JLabel("Manager Port ");
            lblNewLabel.setForeground(Color.BLUE);
            lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
            lblNewLabel.setBounds(202, 22, 148, 53);
        }
        return lblNewLabel;
    }

    public JTextField getTextField() {
        if (textField == null) {
            textField = new JTextField();
            textField.setFont(new Font("Tahoma", Font.PLAIN, 20));
            textField.setBounds(331, 27, 200, 36);
            textField.setBorder(new LineBorder(new Color(0, 255, 35, 255)));
            textField.setColumns(10);
        }
        return textField;
    }

    public JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane(JTabbedPane.TOP);
            tabbedPane.setFont(new Font("Sylfaen", Font.PLAIN, 20));
            tabbedPane.setBorder(null);
            tabbedPane.setBounds(12, 73, 805, 530);
            tabbedPane.addTab(null, null, getLblNewLabel_2(), null);
        }
        return tabbedPane;
    }

    //////////////////////////////////////////////////////////////////------------------------------------------------------

    @Override
    public void run() {
        while (true)

            try {
                // Chấp nhận kết nối từ Client
                Socket staffSocket = socket.accept();
                if (staffSocket != null) {
                    // Lấy tên của nhân viên vừa nhắn tin cho Server
                    br = new BufferedReader(new InputStreamReader(staffSocket.getInputStream()));
                    String staffName = br.readLine();
                    staffName = staffName.substring(0, staffName.indexOf(":"));

                    // Tạo ChatPanel và show nó vào cái TabbedPane, khá là đơn giản
                    ChatPanel chatPanel = new ChatPanel(staffSocket, "MANAGER", staffName);
                    tabbedPane.add(staffName, chatPanel);
                    chatPanel.updateUI();

                    // Chạy Thread ChatPanel để kiểm tra các tin nhắn đến và đi
                    Thread t = new Thread(chatPanel);
                    t.start();
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                // Do not change this because it spawn try-catch many time while running thread!
            }
    }

    public JButton getBtnNewButton() {
        if (btnNewButton == null) {
            btnNewButton = new JButton("START SERVER");
            btnNewButton.setBorder(new LineBorder(new Color(241, 12, 149)));
            btnNewButton.setBackground(new Color(40, 146, 112));
            btnNewButton.setForeground(Color.WHITE);
            btnNewButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    // Cổng mặc định là 1
                    int port = 1;
                    try {
                        // Kiểm tra dữ liệu nhập vào
                        port = Integer.parseInt(textField.getText());
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(contentPane,
                                "Can't start at this port, program will use the default port=1\nDetails: " + e,
                                "Error while read Port", JOptionPane.ERROR_MESSAGE);
                    }
                    try {
                        //chạy Server tại port này
                        socket = new ServerSocket(port);
                        JOptionPane.showMessageDialog(contentPane, "Server is running at port: " + port, "Started server",
                                JOptionPane.INFORMATION_MESSAGE);

                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(contentPane, "Details: " + e, "Start server error",
                                JOptionPane.ERROR_MESSAGE);
                    }

                    // Chạy Server (class hiên tại) để kiểm tra các luồng kết nối vào server sau này
                    //đã gán thisManager=this (tức class hiện tại)
                    Thread t = new Thread(thisManager);
                    t.start();
                }
            });
            btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 18));
            btnNewButton.setBounds(560, 25, 167, 37);
        }
        return btnNewButton;
    }

    public JLabel getLblNewLabel_2() {
        if (lblNewLabel_2 == null) {
            lblNewLabel_2 = new JLabel("Waitting for client...");
            lblNewLabel_2.setBackground(Color.LIGHT_GRAY);
            lblNewLabel_2.setForeground(Color.RED);
            lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 30));
            lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
        }
        return lblNewLabel_2;
    }
}