package Jframe;

import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.table.TableModel;
import java.text.NumberFormat;


public class DataMobil extends javax.swing.JFrame {


    DefaultTableModel model;
    
    public DataMobil() {
        initComponents();
        model = (DefaultTableModel) tbl_mobil.getModel();

        setTitle("Data Mobil"); // Menambahkan judul aplikasi
        
        tampilData();
        
        
        //Agar id hanya menginput number
        txt_harga.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) { // Memeriksa apakah karakter bukan angka
                    e.consume(); // Mengabaikan keystroke jika bukan angka
                }
            }
        });
        
    }
    private void tampilData() {
    // Pastikan model tabel di-clear terlebih dahulu
    clearTable();

    try {
        // Buat koneksi ke database
        Connection con = DBConnection.getConnection();

        // Query untuk mengambil data dari tabel mobil
        String sql = "SELECT * FROM mobil";

        // Menyiapkan PreparedStatement
        PreparedStatement pst = con.prepareStatement(sql);

        // Eksekusi query
        ResultSet rs = pst.executeQuery();

        // Menambahkan data ke tabel
        while (rs.next()) {
            // Membuat array objek untuk setiap baris data
            Object[] row = new Object[6];
            row[0] = rs.getInt("id_mobil"); // Kolom ID Mobil
            row[1] = rs.getString("merk"); // Kolom Merk
            row[2] = rs.getString("model"); // Kolom Model
            row[3] = rs.getString("tipe"); // Kolom Tipe

            // Mengambil harga sewa per hari dan memformat menjadi currency
            int hargaSewa = rs.getInt("harga_sewa_per_hari"); // Kolom Harga Sewa
            String formattedHarga = formatToCurrency(hargaSewa); // Format harga menjadi Rp
            
            row[4] = formattedHarga; // Kolom Harga Sewa
            row[5] = rs.getString("status"); // Kolom Status

            // Menambahkan baris ke model tabel
            model.addRow(row);
        }

    } catch (Exception ex) {
        ex.printStackTrace();
    }
}

// Format harga ke dalam format currency (Rp)
private String formatToCurrency(int price) {
    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID")); // Menggunakan locale Indonesia
    String formattedPrice = formatter.format(price);
    
    // Menghapus bagian desimal jika ada
    if (formattedPrice.contains(",00")) {
        formattedPrice = formattedPrice.replace(",00", "");
    }

    return formattedPrice;
}


// Method untuk membersihkan tabel sebelum menampilkan data baru
private void clearTable() {
    // Pastikan model adalah DefaultTableModel
    if (model != null) {
        model.setRowCount(0); // Menghapus semua baris dalam tabel
    }
}

    
    public void setMobilID() {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT MAX(id_mobil) FROM mobil";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int lastSchID = rs.getInt(1) + 1;  // Increment the last film_id to get the next available id
                id_mobil.setText(String.valueOf(lastSchID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    // Method untuk menambah data
private void addData() {
    String merk = txt_merk.getText();
    String model = txt_model.getText();
    String tipe = (String) combo_tipe.getSelectedItem(); // Mengambil tipe dari combo box
    String harga = txt_harga.getText();
    
    // Validasi input
    if (merk.isEmpty() || model.isEmpty() || tipe.equals("--Pilih--") || harga.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Harap isi semua form input.");
        return; // Menghentikan eksekusi jika ada input kosong
    }

    try {
        
        int hargaSewaPerHari = Integer.parseInt(harga); // Validasi format angka
        
        Connection con = DBConnection.getConnection();
        String sql = "INSERT INTO Mobil (merk, model, tipe, harga_sewa_per_hari) VALUES (?, ?, ?, ?)";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, merk);
        pst.setString(2, model);
        pst.setString(3, tipe);
        pst.setInt(4, hargaSewaPerHari);

        int rowsInserted = pst.executeUpdate();
        if (rowsInserted > 0) {
            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan.");
            clear();
            tampilData();
            setMobilID();
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menambahkan data.");
    }
}

// Method untuk memperbarui data
private void updateData() {
    String idText = id_mobil.getText();
    String merk = txt_merk.getText();
    String model = txt_model.getText();
    String tipe = (String) combo_tipe.getSelectedItem();
    String hargaText = txt_harga.getText();

    // Validasi input
    if (idText.isEmpty() || merk.isEmpty() || model.isEmpty() || tipe.equals("--Pilih--") || hargaText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Harap isi semua form input.");
        return;
    }

    try {
        int idMobil = Integer.parseInt(idText); // Validasi format angka untuk ID
        int hargaSewaPerHari = Integer.parseInt(hargaText); // Validasi format angka untuk harga
        
        Connection con = DBConnection.getConnection();
        String sql = "UPDATE Mobil SET merk = ?, model = ?, tipe = ?, harga_sewa_per_hari = ? WHERE id_mobil = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, merk);
        pst.setString(2, model);
        pst.setString(3, tipe);
        pst.setInt(4, hargaSewaPerHari);
        pst.setInt(5, idMobil);

        int rowsUpdated = pst.executeUpdate();
        if (rowsUpdated > 0) {
            JOptionPane.showMessageDialog(this, "Data berhasil diperbarui.");
            clear();
            tampilData();
            setMobilID();

        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memperbarui data.");
    }
}

// Method untuk menghapus data
private void deleteData() {
    String idText = id_mobil.getText();

    // Validasi input
    if (idText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Harap isi ID Mobil yang akan dihapus.");
        return;
    }

    try {
        int idMobil = Integer.parseInt(idText); // Validasi format angka untuk ID
        Connection con = DBConnection.getConnection();
        String sql = "DELETE FROM Mobil WHERE id_mobil = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, idMobil);

        int rowsDeleted = pst.executeUpdate();
        if (rowsDeleted > 0) {
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
            clear();
            tampilData();
            setMobilID();

        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menghapus data.");
    }
}

private void clear(){
       // Mengosongkan semua input form
    id_mobil.setText("");     
    txt_merk.setText("");
    txt_model.setText("");      
    combo_tipe.setSelectedIndex(0); 
    txt_harga.setText("0");  
}


public void cariData() {
    // Bersihkan tabel sebelum menampilkan hasil pencarian
    clearTable();
    
    // Ambil input dari text field untuk pencarian
    String cari = txt_cariMobil.getText();
    
    try {
        // Koneksi ke database
        Connection con = DBConnection.getConnection();
        
        // Query untuk mencari data berdasarkan semua kolom
        String sql = "SELECT * FROM mobil WHERE "
                   + "id_mobil LIKE ? OR "
                   + "merk LIKE ? OR "
                   + "model LIKE ? OR "
                   + "tipe LIKE ? OR "
                   + "CAST(harga_sewa_per_hari AS CHAR) LIKE ? OR "
                   + "status LIKE ?";
        
        // Menggunakan PreparedStatement untuk mencegah SQL Injection
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, "%" + cari + "%");
        pst.setString(2, "%" + cari + "%");
        pst.setString(3, "%" + cari + "%");
        pst.setString(4, "%" + cari + "%");
        pst.setString(5, "%" + cari + "%");
        pst.setString(6, "%" + cari + "%");
        
        // Eksekusi query
        ResultSet rs = pst.executeQuery();
        
        // Loop untuk menambahkan data ke tabel
        while (rs.next()) {
            Object[] row = new Object[6];
            row[0] = rs.getInt("id_mobil"); // ID Mobil
            row[1] = rs.getString("merk"); // Merk
            row[2] = rs.getString("model"); // Model
            row[3] = rs.getString("tipe"); // Tipe
            row[4] = rs.getInt("harga_sewa_per_hari"); // Harga Sewa per Hari
            row[5] = rs.getString("status"); // Status Mobil
            
            // Tambahkan baris ke model tabel
            model.addRow(row);
        }
        
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    
    


    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        button_signup = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jdl_username = new javax.swing.JLabel();
        id_mobil = new javax.swing.JTextField();
        txt_harga = new javax.swing.JTextField();
        jdl_username1 = new javax.swing.JLabel();
        jdl_username3 = new javax.swing.JLabel();
        button_Add = new javax.swing.JButton();
        button_Update = new javax.swing.JButton();
        button_Delete = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jdl_username2 = new javax.swing.JLabel();
        button_clear = new javax.swing.JButton();
        jdl_username5 = new javax.swing.JLabel();
        txt_model = new javax.swing.JTextField();
        txt_merk = new javax.swing.JTextField();
        combo_tipe = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbl_mobil = new rojerusan.RSTableMetro();
        txt_cariMobil = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();

        button_signup.setBackground(new java.awt.Color(255, 204, 0));
        button_signup.setText("Sign Up");
        button_signup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_signupActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(96, 123, 179));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jdl_username.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jdl_username.setForeground(new java.awt.Color(255, 255, 255));
        jdl_username.setText("Merk :");
        jPanel1.add(jdl_username, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 140, -1, -1));

        id_mobil.setEditable(false);
        id_mobil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                id_mobilActionPerformed(evt);
            }
        });
        jPanel1.add(id_mobil, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 90, 250, 36));

        txt_harga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_hargaActionPerformed(evt);
            }
        });
        jPanel1.add(txt_harga, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 420, 247, 36));

        jdl_username1.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jdl_username1.setForeground(new java.awt.Color(255, 255, 255));
        jdl_username1.setText("ID");
        jPanel1.add(jdl_username1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, -1, -1));

        jdl_username3.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jdl_username3.setForeground(new java.awt.Color(255, 255, 255));
        jdl_username3.setText("Harga Sewa / Hari");
        jPanel1.add(jdl_username3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 390, -1, -1));

        button_Add.setBackground(new java.awt.Color(51, 204, 0));
        button_Add.setForeground(new java.awt.Color(255, 255, 255));
        button_Add.setText("ADD");
        button_Add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_AddActionPerformed(evt);
            }
        });
        jPanel1.add(button_Add, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 560, 110, 30));

        button_Update.setBackground(new java.awt.Color(255, 204, 0));
        button_Update.setForeground(new java.awt.Color(255, 255, 255));
        button_Update.setText("UPDATE");
        button_Update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_UpdateActionPerformed(evt);
            }
        });
        jPanel1.add(button_Update, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 560, 110, 30));

        button_Delete.setBackground(new java.awt.Color(255, 52, 37));
        button_Delete.setForeground(new java.awt.Color(255, 255, 255));
        button_Delete.setText("DELETE");
        button_Delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_DeleteActionPerformed(evt);
            }
        });
        jPanel1.add(button_Delete, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 600, 113, 30));

        jLabel7.setBackground(new java.awt.Color(102, 102, 255));
        jLabel7.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Back_40Px2.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 56, -1));

        jdl_username2.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jdl_username2.setForeground(new java.awt.Color(255, 255, 255));
        jdl_username2.setText("Model : ");
        jPanel1.add(jdl_username2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 220, -1, -1));

        button_clear.setBackground(new java.awt.Color(255, 52, 37));
        button_clear.setForeground(new java.awt.Color(255, 255, 255));
        button_clear.setText("CLEAR");
        button_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_clearActionPerformed(evt);
            }
        });
        jPanel1.add(button_clear, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 600, 113, 30));

        jdl_username5.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jdl_username5.setForeground(new java.awt.Color(255, 255, 255));
        jdl_username5.setText("Tipe : ");
        jPanel1.add(jdl_username5, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 300, -1, -1));

        txt_model.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_modelActionPerformed(evt);
            }
        });
        jPanel1.add(txt_model, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 250, 250, 36));

        txt_merk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_merkActionPerformed(evt);
            }
        });
        jPanel1.add(txt_merk, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 170, 250, 36));

        combo_tipe.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Pilih--", "MPV", "Sedan", "SUV", "Electric" }));
        combo_tipe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_tipeActionPerformed(evt);
            }
        });
        jPanel1.add(combo_tipe, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 330, 250, 40));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(96, 123, 179));
        jLabel4.setText("Data Mobil");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 80, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/mobility.png"))); // NOI18N
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 70, 52, 70));

        tbl_mobil.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Merk", "Model", "Tipe", "Harga", "Status"
            }
        ));
        tbl_mobil.setColorBackgoundHead(new java.awt.Color(96, 123, 179));
        tbl_mobil.setRowHeight(25);
        tbl_mobil.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_mobilMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tbl_mobil);

        jPanel3.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, 720, 320));

        txt_cariMobil.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_cariMobilKeyReleased(evt);
            }
        });
        jPanel3.add(txt_cariMobil, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 160, 310, 40));

        jLabel6.setFont(new java.awt.Font("Segoe UI Semibold", 0, 15)); // NOI18N
        jLabel6.setText("Cari Mobil :");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 170, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 796, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(187, 187, 187)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

      
    
    private void id_mobilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_id_mobilActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_id_mobilActionPerformed

    private void txt_hargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_hargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_hargaActionPerformed

    private void button_signupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_signupActionPerformed
 
    }//GEN-LAST:event_button_signupActionPerformed

    private void button_AddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_AddActionPerformed
        addData();
    }//GEN-LAST:event_button_AddActionPerformed

    private void button_UpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_UpdateActionPerformed
       updateData();
    }//GEN-LAST:event_button_UpdateActionPerformed

    private void button_DeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_DeleteActionPerformed
        deleteData();
    }//GEN-LAST:event_button_DeleteActionPerformed

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        HomePage HomePage = new HomePage();
        HomePage.setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel7MouseClicked

    private void button_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_clearActionPerformed
       clear();
       setMobilID();
    }//GEN-LAST:event_button_clearActionPerformed

    private void tbl_mobilMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_mobilMouseClicked
    int rowNo = tbl_mobil.getSelectedRow();
    TableModel model = tbl_mobil.getModel();
    
    // Ambil nilai dari kolom yang dipilih
    String idMobil = model.getValueAt(rowNo, 0).toString();
    String merk = model.getValueAt(rowNo, 1).toString();
    String modelMobil = model.getValueAt(rowNo, 2).toString();
    String tipe = model.getValueAt(rowNo, 3).toString();
    String harga = model.getValueAt(rowNo, 4).toString(); // Ini dalam format Rp 1.000.000

    // Set data ke form input
    id_mobil.setText(idMobil);
    txt_merk.setText(merk);
    txt_model.setText(modelMobil);
    combo_tipe.setSelectedItem(tipe);
    txt_harga.setText(removeCurrencySymbol(harga)); // Set harga tanpa simbol "Rp"
    
    }//GEN-LAST:event_tbl_mobilMouseClicked

    
// Fungsi untuk menghapus simbol "Rp" dan format angka
private String removeCurrencySymbol(String harga) {
    // Menghapus "Rp" dan tanda pemisah ribuan
    if (harga.contains("Rp")) {
        harga = harga.replace("Rp", "").replace(".", "").trim();
    }
    return harga;
}
    private void txt_modelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_modelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_modelActionPerformed

    private void txt_merkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_merkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_merkActionPerformed

    private void combo_tipeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_tipeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combo_tipeActionPerformed

    private void txt_cariMobilKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_cariMobilKeyReleased
        // TODO add your handling code here:
        cariData();
    }//GEN-LAST:event_txt_cariMobilKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DataMobil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DataMobil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DataMobil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DataMobil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DataMobil().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_Add;
    private javax.swing.JButton button_Delete;
    private javax.swing.JButton button_Update;
    private javax.swing.JButton button_clear;
    private javax.swing.JButton button_signup;
    private javax.swing.JComboBox<String> combo_tipe;
    private javax.swing.JTextField id_mobil;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel jdl_username;
    private javax.swing.JLabel jdl_username1;
    private javax.swing.JLabel jdl_username2;
    private javax.swing.JLabel jdl_username3;
    private javax.swing.JLabel jdl_username5;
    private rojerusan.RSTableMetro tbl_mobil;
    private javax.swing.JTextField txt_cariMobil;
    private javax.swing.JTextField txt_harga;
    private javax.swing.JTextField txt_merk;
    private javax.swing.JTextField txt_model;
    // End of variables declaration//GEN-END:variables

    private void diapose() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
