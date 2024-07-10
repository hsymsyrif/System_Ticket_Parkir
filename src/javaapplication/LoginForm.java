package javaapplication;

import javax.swing.*; // Mengimpor komponen Swing untuk GUI
import java.awt.*; // Mengimpor komponen AWT untuk GUI
import java.awt.event.ActionEvent; // Mengimpor kelas event
import java.awt.event.ActionListener; // Mengimpor antarmuka event listener
import java.sql.Connection; // Mengimpor koneksi SQL
import java.sql.PreparedStatement; // Mengimpor prepared statement SQL
import java.sql.ResultSet; // Mengimpor result set SQL
import java.sql.SQLException; // Mengimpor pengecualian SQL

public class LoginForm extends JFrame { // Mendefinisikan kelas LoginForm yang merupakan subclass dari JFrame
    // Mendeklarasikan komponen GUI sebagai variabel instance
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginForm() { // Konstruktor untuk kelas LoginForm
        setTitle("Login"); // Menetapkan judul jendela
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

        // Menambahkan tombol Login
        loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        // Menambahkan tombol Register
        registerButton = new JButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        add(registerButton, gbc);

        // Menambahkan action listener untuk tombol Login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser(); // Memanggil metode loginUser saat tombol Login ditekan
            }
        });

        // Menambahkan action listener untuk tombol Register
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterForm(); // Memanggil metode openRegisterForm saat tombol Register ditekan
            }
        });
    }

    private void loginUser() { // Metode untuk login pengguna
        // Mendapatkan teks dari field input
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Memeriksa apakah ada field yang kosong
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all the fields", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Menghentikan eksekusi jika ada field yang kosong
        }

        try (Connection conn = DatabaseConnection.getConnection()) { // Mendapatkan koneksi ke database
            String sql = "SELECT id FROM user WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql); // Mempersiapkan pernyataan SQL
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery(); // Menjalankan pernyataan SQL dan mendapatkan hasilnya

            if (rs.next()) { // Memeriksa apakah pengguna ditemukan
                int userId = rs.getInt("id"); // Mendapatkan ID pengguna dari hasil query
                JOptionPane.showMessageDialog(this, "Login successful!"); // Menampilkan pesan sukses
                // Membuka MainForm dan mengirimkan userId
                new MainForm(userId).setVisible(true);
                dispose(); // Menutup jendela LoginForm
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE); // Menampilkan pesan kesalahan jika username atau password salah
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE); // Menampilkan pesan kesalahan jika terjadi kesalahan SQL
        }
    }

    private void openRegisterForm() { // Metode untuk membuka form register
        new RegisterForm().setVisible(true); // Membuka jendela RegisterForm
        dispose(); // Menutup jendela LoginForm
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
                new LoginForm().setVisible(true); // Membuat dan menampilkan jendela LoginForm
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
