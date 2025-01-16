
package Jframe;

import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import javax.swing.JOptionPane;

public class DataPelanggan extends javax.swing.JFrame {

    DefaultTableModel model;
    
    public DataPelanggan() {
        initComponents();
        setTitle("Data Pelanggan"); // Menambahkan judul aplikasi
        setPelangganTabel();
        
        //Agar id hanya menginput number
        txt_noTelp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) { // Memeriksa apakah karakter bukan angka
                    e.consume(); // Mengabaikan keystroke jika bukan angka
                }
            }
        });
        
    }
    
    public void setPelangganTabel() {
        clearTable(); // Bersihkan tabel sebelum menampilkan data baru
        try {
            // Koneksi ke database
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * FROM pelanggan"; // Query untuk mengambil data pelanggan
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            // Loop untuk menambahkan data ke tabel
            while (rs.next()) {
                Object[] obj = {
                    rs.getInt("id_pelanggan"),
                    rs.getString("nama"),
                    rs.getString("alamat"),
                    rs.getString("no_telepon"),
                    rs.getString("email")
                };
                model = (DefaultTableModel) tbl_pelanggan.getModel();
                model.addRow(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean addPelanggan() {
        // Validasi input
        if (txt_nama.getText().trim().isEmpty() || txt_alamat.getText().trim().isEmpty() ||
            txt_noTelp.getText().trim().isEmpty() || txt_email.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
            return false;
        }

        try {
            // Koneksi ke database
            Connection con = DBConnection.getConnection();
            String sql = "INSERT INTO pelanggan (nama, alamat, no_telepon, email) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, txt_nama.getText().trim());
            pst.setString(2, txt_alamat.getText().trim());
            pst.setString(3, txt_noTelp.getText().trim());
            pst.setString(4, txt_email.getText().trim());

            int rowCount = pst.executeUpdate(); // Eksekusi query
            if (rowCount > 0) {
                JOptionPane.showMessageDialog(this, "Data pelanggan berhasil ditambahkan!");
                clear();
                setPelangganTabel(); // Refresh tabel
                setPelangganID();
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambahkan data pelanggan!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updatePelanggan() {
        // Validasi input
        if (txt_idPelanggan.getText().trim().isEmpty() || txt_nama.getText().trim().isEmpty() || 
            txt_alamat.getText().trim().isEmpty() || txt_noTelp.getText().trim().isEmpty() || 
            txt_email.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
            return false;
        }

        try {
            // Koneksi ke database
            Connection con = DBConnection.getConnection();
            String sql = "UPDATE pelanggan SET nama = ?, alamat = ?, no_telepon = ?, email = ? WHERE id_pelanggan = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, txt_nama.getText().trim());
            pst.setString(2, txt_alamat.getText().trim());
            pst.setString(3, txt_noTelp.getText().trim());
            pst.setString(4, txt_email.getText().trim());
            pst.setInt(5, Integer.parseInt(txt_idPelanggan.getText().trim()));

            int rowCount = pst.executeUpdate(); // Eksekusi query
            if (rowCount > 0) {
                JOptionPane.showMessageDialog(this, "Data pelanggan berhasil diperbarui!");
                clear();
                setPelangganTabel(); // Refresh tabel
                setPelangganID();

                return true;
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui data pelanggan!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deletePelanggan() {
        // Validasi input
        if (txt_idPelanggan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID Pelanggan harus diisi!");
            return false;
        }

        try {
            // Koneksi ke database
            Connection con = DBConnection.getConnection();
            String sql = "DELETE FROM pelanggan WHERE id_pelanggan = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(txt_idPelanggan.getText().trim()));

            int rowCount = pst.executeUpdate(); // Eksekusi query
            if (rowCount > 0) {
                JOptionPane.showMessageDialog(this, "Data pelanggan berhasil dihapus!");
                clear();
                setPelangganTabel(); // Refresh tabel
                setPelangganID();
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data pelanggan!");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    
    
    public void setPelangganID() {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT MAX(id_pelanggan) FROM pelanggan";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int lastSchID = rs.getInt(1) + 1;  // Increment the last film_id to get the next available id
                txt_idPelanggan.setText(String.valueOf(lastSchID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
   
    
    public void cariData() {
    clearTable(); // Bersihkan tabel sebelum menampilkan hasil pencarian
    String cari = txt_cariCustomer.getText().trim();

    try {
        // Koneksi ke database
        Connection con = DBConnection.getConnection();
        String sql = "SELECT * FROM pelanggan WHERE id_pelanggan LIKE ? OR nama LIKE ? OR alamat LIKE ? OR no_telepon LIKE ? OR email LIKE ?";
        PreparedStatement pst = con.prepareStatement(sql);

        // Mengisi parameter untuk pencarian
        String keyword = "%" + cari + "%";
        pst.setString(1, keyword);
        pst.setString(2, keyword);
        pst.setString(3, keyword);
        pst.setString(4, keyword);
        pst.setString(5, keyword);

        ResultSet rs = pst.executeQuery();

        // Menambahkan data yang ditemukan ke tabel
        while (rs.next()) {
            Object[] obj = {
                rs.getInt("id_pelanggan"),
                rs.getString("nama"),
                rs.getString("alamat"),
                rs.getString("no_telepon"),
                rs.getString("email")
            };
            model.addRow(obj);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
    
    public void clear() {
        // Mengosongkan semua field input
        txt_idPelanggan.setText("");
        txt_nama.setText("");
        txt_alamat.setText("");
        txt_noTelp.setText("");
        txt_email.setText("");
        txt_cariCustomer.setText(""); // Kosongkan juga field pencarian jika ada
    }


    
    
    //method clear table
    public void clearTable(){
        DefaultTableModel model = (DefaultTableModel) tbl_pelanggan.getModel();
        model.setRowCount(0);
        
    }
 



        
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        button_signup = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jdl_username = new javax.swing.JLabel();
        txt_idPelanggan = new javax.swing.JTextField();
        txt_noTelp = new javax.swing.JTextField();
        jdl_username1 = new javax.swing.JLabel();
        jdl_username3 = new javax.swing.JLabel();
        button_Add = new javax.swing.JButton();
        button_Update = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txt_nama = new javax.swing.JTextField();
        txt_email = new javax.swing.JTextField();
        jdl_username4 = new javax.swing.JLabel();
        button_clear = new javax.swing.JButton();
        txt_alamat = new javax.swing.JTextField();
        jdl_username2 = new javax.swing.JLabel();
        button_delete = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt_cariCustomer = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_pelanggan = new rojerusan.RSTableMetro();

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
        jdl_username.setText("ID Pelanggan :");
        jPanel1.add(jdl_username, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, -1, -1));

        txt_idPelanggan.setEditable(false);
        txt_idPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_idPelangganActionPerformed(evt);
            }
        });
        jPanel1.add(txt_idPelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 130, 247, 36));

        txt_noTelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_noTelpActionPerformed(evt);
            }
        });
        jPanel1.add(txt_noTelp, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 370, 247, 36));

        jdl_username1.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jdl_username1.setForeground(new java.awt.Color(255, 255, 255));
        jdl_username1.setText("Nama :");
        jPanel1.add(jdl_username1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 180, -1, -1));

        jdl_username3.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jdl_username3.setForeground(new java.awt.Color(255, 255, 255));
        jdl_username3.setText("Masukkan No. Telpon :");
        jPanel1.add(jdl_username3, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 340, -1, -1));

        button_Add.setBackground(new java.awt.Color(51, 204, 0));
        button_Add.setForeground(new java.awt.Color(255, 255, 255));
        button_Add.setText("ADD");
        button_Add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_AddActionPerformed(evt);
            }
        });
        jPanel1.add(button_Add, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 500, 110, 30));

        button_Update.setBackground(new java.awt.Color(255, 204, 0));
        button_Update.setForeground(new java.awt.Color(255, 255, 255));
        button_Update.setText("UPDATE");
        button_Update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_UpdateActionPerformed(evt);
            }
        });
        jPanel1.add(button_Update, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 500, 110, 30));

        jLabel7.setBackground(new java.awt.Color(102, 102, 255));
        jLabel7.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Back_40Px2.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 56, -1));

        txt_nama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_namaActionPerformed(evt);
            }
        });
        jPanel1.add(txt_nama, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 210, 247, 36));

        txt_email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_emailActionPerformed(evt);
            }
        });
        jPanel1.add(txt_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 450, 247, 36));

        jdl_username4.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jdl_username4.setForeground(new java.awt.Color(255, 255, 255));
        jdl_username4.setText("Masukkan Email :");
        jPanel1.add(jdl_username4, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 420, -1, -1));

        button_clear.setBackground(new java.awt.Color(255, 52, 37));
        button_clear.setForeground(new java.awt.Color(255, 255, 255));
        button_clear.setText("CLEAR");
        button_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_clearActionPerformed(evt);
            }
        });
        jPanel1.add(button_clear, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 550, 113, 30));

        txt_alamat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_alamatActionPerformed(evt);
            }
        });
        jPanel1.add(txt_alamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 290, 247, 36));

        jdl_username2.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jdl_username2.setForeground(new java.awt.Color(255, 255, 255));
        jdl_username2.setText("Alamat : ");
        jPanel1.add(jdl_username2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 260, -1, -1));

        button_delete.setBackground(new java.awt.Color(255, 52, 37));
        button_delete.setForeground(new java.awt.Color(255, 255, 255));
        button_delete.setText("DELETE");
        button_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_deleteActionPerformed(evt);
            }
        });
        jPanel1.add(button_delete, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 550, 113, 30));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(96, 123, 179));
        jLabel4.setText("Data Pelanggan");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 68, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_People_50px.png"))); // NOI18N
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 52, 70));

        jLabel6.setFont(new java.awt.Font("Segoe UI Semibold", 0, 15)); // NOI18N
        jLabel6.setText("Cari Pelanggan :");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 156, -1, -1));

        txt_cariCustomer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_cariCustomerKeyReleased(evt);
            }
        });
        jPanel3.add(txt_cariCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(264, 151, 281, 34));

        tbl_pelanggan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Pelanggan", "Nama", "Alamat", "No. Telp", "Email"
            }
        ));
        tbl_pelanggan.setRowHeight(30);
        tbl_pelanggan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_pelangganMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_pelanggan);

        jPanel3.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 250, 670, 300));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 757, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void txt_idPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_idPelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_idPelangganActionPerformed

    private void txt_noTelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_noTelpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_noTelpActionPerformed

    private void button_signupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_signupActionPerformed
 
    }//GEN-LAST:event_button_signupActionPerformed

    private void button_AddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_AddActionPerformed
        addPelanggan();
    }//GEN-LAST:event_button_AddActionPerformed

    private void button_UpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_UpdateActionPerformed
        updatePelanggan();
    }//GEN-LAST:event_button_UpdateActionPerformed
/**
    private voidtbl_pelanggansMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_customerDetailsMouseClicked
      
        
    }//GEN-LAST:event_tbl_customerDetailsMouseClicked
**/
    private void txt_cariCustomerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_cariCustomerKeyReleased
        // TODO add your handling code here:
        cariData();
    }//GEN-LAST:event_txt_cariCustomerKeyReleased

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        // TODO add your handling code here:
        HomePage home = new HomePage();
        home.setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel7MouseClicked

    private void txt_namaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_namaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_namaActionPerformed

    private void txt_emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_emailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_emailActionPerformed

    private void button_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_clearActionPerformed
        clear();
        setPelangganID();
    }//GEN-LAST:event_button_clearActionPerformed

    private void txt_alamatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_alamatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_alamatActionPerformed

    private void button_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_deleteActionPerformed
        deletePelanggan();
    }//GEN-LAST:event_button_deleteActionPerformed

    private void tbl_pelangganMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_pelangganMouseClicked
        // Mendapatkan indeks baris yang diklik
    int selectedRow = tbl_pelanggan.getSelectedRow();

    if (selectedRow != -1) {
        // Mendapatkan nilai dari tabel berdasarkan kolom
        String idPelanggan = model.getValueAt(selectedRow, 0).toString();
        String nama = model.getValueAt(selectedRow, 1).toString();
        String alamat = model.getValueAt(selectedRow, 2).toString();
        String noTelp = model.getValueAt(selectedRow, 3).toString();
        String email = model.getValueAt(selectedRow, 4).toString();

        // Mengisi form input dengan data yang diperoleh
        txt_idPelanggan.setText(idPelanggan);
        txt_nama.setText(nama);
        txt_alamat.setText(alamat);
        txt_noTelp.setText(noTelp);
        txt_email.setText(email);
    }
    }//GEN-LAST:event_tbl_pelangganMouseClicked

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
            java.util.logging.Logger.getLogger(DataPelanggan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DataPelanggan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DataPelanggan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DataPelanggan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new DataPelanggan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_Add;
    private javax.swing.JButton button_Update;
    private javax.swing.JButton button_clear;
    private javax.swing.JButton button_delete;
    private javax.swing.JButton button_signup;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jdl_username;
    private javax.swing.JLabel jdl_username1;
    private javax.swing.JLabel jdl_username2;
    private javax.swing.JLabel jdl_username3;
    private javax.swing.JLabel jdl_username4;
    private rojerusan.RSTableMetro tbl_pelanggan;
    private javax.swing.JTextField txt_alamat;
    private javax.swing.JTextField txt_cariCustomer;
    private javax.swing.JTextField txt_email;
    private javax.swing.JTextField txt_idPelanggan;
    private javax.swing.JTextField txt_nama;
    private javax.swing.JTextField txt_noTelp;
    // End of variables declaration//GEN-END:variables

    private void diapose() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
