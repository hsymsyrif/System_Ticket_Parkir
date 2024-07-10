package javaapplication;

import javax.swing.*; // Mengimpor komponen Swing untuk GUI
import java.awt.*; // Mengimpor komponen AWT untuk GUI
import java.awt.event.ActionEvent; // Mengimpor kelas event
import java.awt.event.ActionListener; // Mengimpor antarmuka event listener
import java.sql.Connection; // Mengimpor koneksi SQL
import java.sql.PreparedStatement; // Mengimpor prepared statement SQL
import java.sql.SQLException; // Mengimpor pengecualian SQL

public class RegisterForm extends JFrame { // Mendefinisikan kelas RegisterForm yang merupakan subclass dari JFrame
    // Mendeklarasikan komponen GUI sebagai variabel instance
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField dobField;
    private JButton registerButton;
    private JButton loginButton;

    public RegisterForm() { // Konstruktor untuk kelas RegisterForm
        setTitle("Register"); // Menetapkan judul jendela
        setSize(500, 400); // Menetapkan ukuran jendela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Menetapkan operasi default saat jendela ditutup
        setLocationRelativeTo(null); // Menetapkan lokasi jendela ke tengah layar
        setLayout(new GridBagLayout()); // Mengatur layout manager untuk jendela

        GridBagConstraints gbc = new GridBagConstraints(); // Membuat objek GridBagConstraints untuk pengaturan tata letak
        gbc.insets = new Insets(10, 10, 10, 10); // Menetapkan margin untuk komponen

        // Menambahkan label dan field untuk Username
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(usernameField, gbc);

        // Menambahkan label dan field untuk Password
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);

        // Menambahkan label dan field untuk Email
        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(emailLabel, gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(emailField, gbc);

        // Menambahkan label dan field untuk Tanggal Lahir
        JLabel dobLabel = new JLabel("Date of Birth (YYYY-MM-DD):");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(dobLabel, gbc);

        dobField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(dobField, gbc);

        // Menambahkan tombol Register
        registerButton = new JButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        add(registerButton, gbc);

        // Menambahkan tombol Login
        loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        // Menambahkan action listener untuk tombol Register
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser(); // Memanggil metode registerUser saat tombol Register ditekan
            }
        });

        // Menambahkan action listener untuk tombol Login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLoginForm(); // Memanggil metode openLoginForm saat tombol Login ditekan
            }
        });
    }

    private void registerUser() { // Metode untuk registrasi pengguna baru
        // Mendapatkan teks dari field input
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText();
        String dob = dobField.getText();

        // Memeriksa apakah ada field yang kosong
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || dob.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Menghentikan eksekusi jika ada field yang kosong
        }

        try (Connection conn = DatabaseConnection.getConnection()) { // Mendapatkan koneksi ke database
            String sql = "INSERT INTO user (username, password, email, date_of_birth, member_since) VALUES (?, ?, ?, ?, NOW())";
            PreparedStatement stmt = conn.prepareStatement(sql); // Mempersiapkan pernyataan SQL
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, dob);
            stmt.executeUpdate(); // Menjalankan pernyataan SQL untuk memasukkan data pengguna baru
            JOptionPane.showMessageDialog(this, "Registration successful! You can now login."); // Menampilkan pesan sukses
            clearFields(); // Membersihkan field input setelah registrasi
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE); // Menampilkan pesan kesalahan jika terjadi kesalahan SQL
        }
    }

    private void clearFields() { // Metode untuk membersihkan field input
        usernameField.setText("");
        passwordField.setText("");
        emailField.setText("");
        dobField.setText("");
    }

    private void openLoginForm() { // Metode untuk membuka form login
        new LoginForm().setVisible(true); // Membuka jendela LoginForm
        dispose(); // Menutup jendela RegisterForm
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) { // Metode utama untuk menjalankan aplikasi
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegisterForm().setVisible(true); // Membuat dan menampilkan jendela RegisterForm
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
