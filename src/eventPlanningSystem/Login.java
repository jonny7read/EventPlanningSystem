/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eventPlanningSystem;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author advent
 */
public class Login {

    private JFrame frame;
    private final JTextField txtUser;
    private final JPasswordField pwdPass;
    private static Login instance;
    private final JButton btnCreate, btnLogin;
    private String user, pass, fileName;
    private File userFile;

    public static void main(String[] args) {
        getInstance();
    }

    public static Login getInstance() {
        if (instance == null) {
            instance = new Login();
        }
        return instance;
    }
    private GridBagConstraints gbc;

    public Login() {
        txtUser = new JTextField();
        pwdPass = new JPasswordField();
        btnCreate = new JButton("Create new Account");
        btnLogin = new JButton("Log in");
        
        btnLogin.addActionListener(new ButtonHandler());
        btnCreate.addActionListener(new ButtonHandler());

        makeFrame();
        addComponents();

        frame.getRootPane().setDefaultButton(btnLogin);

        frame.pack();
        frame.setVisible(true);
    }

    private void makeFrame() {
        frame = new JFrame("Log in");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(frame,
                        "Are you sure you wish to close the application?", "Really Close?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

    }

    private void addComponents() {
        JLabel lblWelcome, lblUser, lblPass, lblAuthor;
        lblWelcome = new JLabel("Welcome");
        lblUser = new JLabel("Username:");
        lblPass = new JLabel("Password:");
        lblAuthor = new JLabel("Created by Jonathan Read");

        lblWelcome.setFont(lblWelcome.getFont().deriveFont(Font.BOLD, 30));
        lblAuthor.setFont(lblAuthor.getFont().deriveFont(Font.BOLD, 10));

        frame.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        addComponent(lblWelcome, 0, 0, 4);
        addComponent(lblUser, 0, 1, 1);
        addComponent(txtUser, 1, 1, 3);
        addComponent(lblPass, 0, 2, 1);
        addComponent(pwdPass, 1, 2, 3);
        addComponent(btnCreate, 0, 3, 3);
        addComponent(btnLogin, 3, 3, 1);
        addComponent(btnLogin, 3, 3, 1);
        gbc.anchor = GridBagConstraints.EAST;
        addComponent(lblAuthor, 3, 4, 1);
    }

    private void addComponent(Component comp, int x, int y, int width) {
        if (comp.getClass().equals(JTextField.class)) {
            gbc.fill = GridBagConstraints.HORIZONTAL;
        }
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;

        frame.add(comp, gbc);
    }

    public void show() {
        frame.setVisible(true);
    }

    public String getUser() {
        return user;
    }

    private class ButtonHandler implements ActionListener {

        public ButtonHandler() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // save input
            user = txtUser.getText().trim();
            pass = pwdPass.getText().trim();
            boolean fail = false;
            String errorMsg = "";
            if (user.isEmpty()) {
                fail = true;
                errorMsg = "Username field blank";
            } else if (pass.isEmpty()){
                fail = true;
                errorMsg = "Password field blank";                
            } else if (!user.isEmpty() && user.length() < 5){
                fail = true;
                errorMsg = "Username is too short.\nMust be atleast 5 characters.";                  
            } else if (!pass.isEmpty() && pass.length() < 5) {
                fail = true;
                errorMsg = "Password is too short.\nMust be atleast 5 characters.";                 
            }
            
            if (fail) {
                JOptionPane.showMessageDialog(frame, errorMsg,
                        "Bad username or Password", JOptionPane.ERROR_MESSAGE);
                return;
            }

            userFile = new File("User Accounts/" + user);

            fileName = userFile.getPath() + "/login.txt";

            if (e.getSource() == btnLogin) {
                if (login()) {
                    switchForms();
                }
            } else if (e.getSource() == btnCreate) {
                createNewUser();
            }
        }

        private void createNewUser() {
            if (!userFile.exists()) {
                System.out.println("creating directory: \"" + userFile.getPath() + "\"");
                userFile.mkdirs();
            } else {
                JOptionPane.showMessageDialog(frame, "A user with that name already exists!\nPlease choose another username",
                    "Username taken", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try {
                FileWriter fw = new FileWriter(fileName);
                try (BufferedWriter bw = new BufferedWriter(fw)) {
                    bw.write(user + "," + pass);
                    bw.newLine();
                }
            } catch (IOException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(frame, "User created successfully.",
                    "User created", JOptionPane.INFORMATION_MESSAGE);
        }

        private boolean login() {
            try {
                FileReader fr = new FileReader(fileName);
                try (BufferedReader br = new BufferedReader(fr)) {
                    String userAndPass;
                    while ((userAndPass = br.readLine()) != null) {
                        String[] parts = userAndPass.split(",");
                        String errorMsg;
                        if (parts[0].equals(user) && parts[1].equals(pass)) {
                            return true;
                        } else if (parts[0].equals(user) && !parts[1].equals(pass)){
                            errorMsg = "Incorrect password";
                        } else {
                            errorMsg = "User doesn't exist";
                        }
                        JOptionPane.showMessageDialog(frame, errorMsg,
                                    "Invalid login details", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "User doesn't exist.\nPlease click Create New Account",
                        "User not found", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }

        private void switchForms() {
            try {
                CalendarForm.class.newInstance();
                frame.setVisible(false);
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}