package javaapplication;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class MainForm extends JFrame { //Mendefinisikan kelas MainForm yang merupakan subclass dari JFrame
    private JTable eventsTable; // Deklarasi tabel acara
    private JButton reserveButton;
    private int userId;

    public MainForm(int userId) { // Konstruktor untuk kelas MainForm
        this.userId = userId;
        setTitle("Sistem Tiket Acara");
        setSize(1000, 600); // Ubah ukuran jendela sesuai kebutuhan
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout()); // Mengatur layout manager untuk jendela

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Menu Profil
        JMenu profileMenu = new JMenu("Profil"); // Menambahkan item menu Lihat Profil ke menu Profil
        JMenuItem viewProfileItem = new JMenuItem("Lihat Profil");
        viewProfileItem.addActionListener(new ActionListener() { // Menambahkan action listener untuk item menu Lihat Profil
            @Override
            public void actionPerformed(ActionEvent e) {
                viewProfile();
            }
        });
        profileMenu.add(viewProfileItem);
        menuBar.add(profileMenu);

        // Menu Tiket Acara
        JMenu ticketMenu = new JMenu("Tiket Acara");
        JMenuItem viewClaimedTicketsItem = new JMenuItem("Tiket yang Telah Diklaim");
        viewClaimedTicketsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewClaimedTickets();
            }
        });
        ticketMenu.add(viewClaimedTicketsItem);
        menuBar.add(ticketMenu);

        // Menu Tiket Parkir
        JMenu parkingMenu = new JMenu("Tiket Parkir");
        JMenuItem viewParkingSlotsItem = new JMenuItem("Lihat Tempat Parkir");
        viewParkingSlotsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewParkingSlots(); // Memanggil metode viewParkingSlots saat item menu diklik
            }
        });
        parkingMenu.add(viewParkingSlotsItem);

        JMenuItem viewReservedParkingSlotsItem = new JMenuItem("Tempat Parkir yang Telah Dipesan");
        viewReservedParkingSlotsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewReservedParkingSlots();
            }
        });
        parkingMenu.add(viewReservedParkingSlotsItem);

        menuBar.add(parkingMenu);

        // Menu Logout
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        menuBar.add(logoutItem);

        eventsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(eventsTable);
        add(scrollPane, BorderLayout.CENTER);

        reserveButton = new JButton("Reservasi");
        add(reserveButton, BorderLayout.SOUTH);

        reserveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reserveEvent();
            }
        });

        loadEvents();
    }

    private void loadEvents() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, nama, tanggal, durasi, status, harga, deskripsi, lokasi, jenis FROM acara WHERE status = 'ready'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            Vector<String> columnNames = new Vector<>();
            columnNames.add("ID");
            columnNames.add("Nama");
            columnNames.add("Tanggal");
            columnNames.add("Durasi");
            columnNames.add("Status");
            columnNames.add("Harga");
            columnNames.add("Deskripsi");
            columnNames.add("Lokasi");
            columnNames.add("Jenis");

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("nama"));
                row.add(rs.getDate("tanggal"));
                row.add(rs.getInt("durasi"));
                row.add(rs.getString("status"));
                row.add(rs.getDouble("harga"));
                row.add(rs.getString("deskripsi"));
                row.add(rs.getString("lokasi"));
                row.add(rs.getString("jenis"));
                data.add(row);
            }

            DefaultTableModel model = new DefaultTableModel(data, columnNames);
            eventsTable.setModel(model);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reserveEvent() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih acara untuk melakukan reservasi.", "Tidak Ada Acara Dipilih", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int eventId = (int) eventsTable.getValueAt(selectedRow, 0);
        String eventName = (String) eventsTable.getValueAt(selectedRow, 1);

        String quantityStr = JOptionPane.showInputDialog(this, "Masukkan jumlah tiket untuk " + eventName + ":");
        if (quantityStr == null || quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Jumlah tiket diperlukan.", "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Jumlah tiket tidak valid.", "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO ticket_acara (user_id, acara_id, quantity, total, kode, status) VALUES (?, ?, ?, ?, ?, 'unclaim')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);
            stmt.setInt(3, quantity);
            double price = (double) eventsTable.getValueAt(selectedRow, 5);
            stmt.setDouble(4, price * quantity);
            String code = generateCode();
            stmt.setString(5, code);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Pemesanan berhasil! Kode reservasi Anda adalah " + code);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateCode() {
        return "RES" + System.currentTimeMillis();
    }

    private void viewProfile() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM user WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                String email = rs.getString("email");
                String dob = rs.getString("date_of_birth");
                String memberSince = rs.getString("member_since");

                String profileInfo = "Username: " + username + "\n"
                        + "Email: " + email + "\n"
                        + "Tanggal Lahir: " + dob + "\n"
                        + "Member Sejak: " + memberSince;

                JOptionPane.showMessageDialog(this, profileInfo, "Profil Pengguna", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "User tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewClaimedTickets() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT ta.id, a.nama AS nama_acara, ta.quantity, ta.kode, ta.status " +
                    "FROM ticket_acara ta " +
                    "INNER JOIN acara a ON ta.acara_id = a.id " +
                    "WHERE ta.user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            Vector<String> columnNames = new Vector<>();
            columnNames.add("ID Tiket");
            columnNames.add("Nama Acara");
            columnNames.add("Jumlah Tiket");
            columnNames.add("Kode Reservasi");
            columnNames.add("Status");

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("nama_acara"));
                row.add(rs.getInt("quantity"));
                row.add(rs.getString("kode"));
                row.add(rs.getString("status"));
                data.add(row);
            }

            DefaultTableModel model = new DefaultTableModel(data, columnNames);
            JTable ticketsTable = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(ticketsTable);

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);

            JOptionPane.showMessageDialog(this, panel, "Tiket yang Telah Diklaim", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewParkingSlots() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, Parkir, status FROM slot WHERE status = 'ready'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            Vector<String> columnNames = new Vector<>();
            columnNames.add("ID");
            columnNames.add("Parkir");
            columnNames.add("Status");

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("Parkir"));
                row.add(rs.getString("status"));
                data.add(row);
            }

            DefaultTableModel model = new DefaultTableModel(data, columnNames);
            JTable parkingTable = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(parkingTable);

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);

            // Custom buttons
            JButton selectButton = new JButton("Pilih Tempat Reservasi");
            selectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedRow = parkingTable.getSelectedRow();
                    if (selectedRow != -1) {
                        int slotId = (int) parkingTable.getValueAt(selectedRow, 0);
                        String slotName = (String) parkingTable.getValueAt(selectedRow, 1);

                        String dateStr = JOptionPane.showInputDialog(MainForm.this, "Masukkan tanggal reservasi untuk " + slotName + " (YYYY-MM-DD):");
                        if (dateStr != null && !dateStr.isEmpty()) {
                            reserveParkingSlot(slotId, dateStr);
                        } else {
                            JOptionPane.showMessageDialog(MainForm.this, "Tanggal reservasi diperlukan.", "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(MainForm.this, "Silakan pilih tempat parkir untuk reservasi.", "Tidak Ada Tempat Parkir Dipilih", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });

            JButton cancelButton = new JButton("Batal");
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(panel);
                    if (dialog != null) {
                        dialog.dispose();
                    }
                }
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(selectButton);
            buttonPanel.add(cancelButton);

            panel.add(buttonPanel, BorderLayout.SOUTH);

            JOptionPane.showOptionDialog(this, panel, "Tempat Parkir Tersedia", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewReservedParkingSlots() {
    try (Connection conn = DatabaseConnection.getConnection()) {
        String sql = "SELECT tp.id, s.Parkir AS nama_slot, tp.tanggal, tp.barcode, u.username AS nama_pengguna " +
                     "FROM ticket_parkir tp " +
                     "INNER JOIN slot s ON tp.slot_id = s.id " +
                     "INNER JOIN user u ON tp.user_id = u.id " +
                     "WHERE tp.user_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();

        Vector<String> columnNames = new Vector<>();
        columnNames.add("ID Tiket Parkir");
        columnNames.add("Nama Pengguna");
        columnNames.add("Nama Tempat Parkir");
        columnNames.add("Tanggal Reservasi");
        columnNames.add("Kode Reservasi");

        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add(rs.getInt("id"));
            row.add(rs.getString("nama_pengguna"));
            row.add(rs.getString("nama_slot"));
            row.add(rs.getDate("tanggal"));
            row.add(rs.getString("barcode"));
            data.add(row);
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable reservedParkingTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(reservedParkingTable);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Tempat Parkir yang Telah Dipesan", JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void reserveParkingSlot(int slotId, String dateStr) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM ticket_parkir WHERE slot_id = ? AND tanggal = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, slotId);
            checkStmt.setDate(2, java.sql.Date.valueOf(dateStr));
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO ticket_parkir (barcode, slot_id, user_id, tanggal) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                String barcode = generateBarcode();
                insertStmt.setString(1, barcode);
                insertStmt.setInt(2, slotId);
                insertStmt.setInt(3, userId);
                insertStmt.setDate(4, java.sql.Date.valueOf(dateStr));
                insertStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Reservasi tempat parkir berhasil! Kode reservasi Anda adalah " + barcode);
            } else {
                JOptionPane.showMessageDialog(this, "Tempat parkir sudah dipesan untuk tanggal tersebut.", "Reservasi Gagal", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateBarcode() {
        return "PARK" + System.currentTimeMillis();
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            // Tutup MainForm dan buka LoginForm
            dispose(); // Menutup MainForm
            new LoginForm().setVisible(true); // Membuka LoginForm baru
        }
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

    public static void main(String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainForm(1).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
