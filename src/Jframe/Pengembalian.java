package Jframe;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.TableModel;

public class Pengembalian extends javax.swing.JFrame {

    public Pengembalian() {
        initComponents();   
        setTitle("Pengembalian"); 
        comboIDTransaksi();
        comboIDPelanggan();
        
    }
    public void comboIDTransaksi(){
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement("select * from transaksi");
            
            ResultSet rs = pst.executeQuery();
            
            while(rs.next()){
                combo_idTransaksi.addItem(rs.getString("id_transaksi"));    
            }
            
                rs.last();
                int jumlahdata = rs.getRow();
                rs.first();
                
        } catch (Exception e) {
        }
        
    }
    
    public void comboIDPelanggan(){
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement("select * from pelanggan");
            
            ResultSet rs = pst.executeQuery();
            
            while(rs.next()){
                combo_idPelanggan.addItem(rs.getString("id_pelanggan"));    
            }
            
                rs.last();
                int jumlahdata = rs.getRow();
                rs.first();
                
        } catch (Exception e) {
        }
        
    }
    
    public void getSewaMobil() {
        String idTransaksi = combo_idTransaksi.getSelectedItem().toString();
        String idPelanggan = combo_idPelanggan.getSelectedItem().toString();

        try {
            // Membuat koneksi ke database
            Connection con = DBConnection.getConnection();

            // Query untuk mendapatkan data dari tabel transaksi, mobil, dan pelanggan
            String sql = "SELECT t.id_transaksi, p.nama AS nama_pelanggan, m.merk, m.model, m.tipe, " +
                         "t.tanggal_sewa, t.tanggal_kembali, t.total_biaya " +
                         "FROM transaksi t " +
                         "JOIN pelanggan p ON t.id_pelanggan = p.id_pelanggan " +
                         "JOIN mobil m ON t.id_mobil = m.id_mobil " +
                         "WHERE t.id_transaksi = ? AND t.id_pelanggan = ?";

            // Menyiapkan statement dan set parameter
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, idTransaksi);
            pst.setString(2, idPelanggan);

            // Menjalankan query
            ResultSet rs = pst.executeQuery();

            // Jika data ditemukan, isi form dengan data tersebut
            if (rs.next()) {
                txt_idTransaksi.setText(rs.getString("id_transaksi"));
                txt_merk.setText(rs.getString("merk"));
                txt_model.setText(rs.getString("model"));
                txt_tipe.setText(rs.getString("tipe"));
                txt_namaPelanggan.setText(rs.getString("nama_pelanggan"));

                // Set tanggal sewa dan kembali ke date picker
                tgl_sewa.setText(rs.getString("tanggal_sewa"));
                tgl_kembali.setText(rs.getString("tanggal_kembali"));
            } else {
                // Jika data tidak ditemukan
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan untuk Transaksi ID: " + idTransaksi + " dan Pelanggan ID: " + idPelanggan);
            }

            // Menutup koneksi
            con.close();
        } catch (Exception e) {
            // Menampilkan error jika terjadi masalah
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    
    public void prosesPengembalian() {
        int idTransaksi = Integer.parseInt(combo_idTransaksi.getSelectedItem().toString());
        String pelangganID = combo_idPelanggan.getSelectedItem().toString();

        try {
            // Membuat koneksi ke database
            Connection con = DBConnection.getConnection();

            // Mendapatkan informasi transaksi
            String sql = "SELECT t.tanggal_kembali, t.total_biaya, m.id_mobil " +
                         "FROM transaksi t " +
                         "JOIN mobil m ON t.id_mobil = m.id_mobil " +
                         "WHERE t.id_transaksi = ? AND t.id_pelanggan = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, idTransaksi);
            pst.setString(2, pelangganID);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                Date tanggalKembali = rs.getDate("tanggal_kembali");
                int idMobil = rs.getInt("id_mobil");
                double totalBiaya = rs.getDouble("total_biaya");

                // Ambil tanggal dikembalikan dari date picker
                java.util.Date tanggalDikembalikanUtil = new java.util.Date();
                java.sql.Date tanggalDikembalikan = new java.sql.Date(tanggalDikembalikanUtil.getTime());

                // Hitung denda jika terlambat
                long diffInMillis = tanggalDikembalikan.getTime() - tanggalKembali.getTime();
                long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

                double denda = 0;
                if (diffInDays > 0) {
                    denda = diffInDays * 100000; // 100000 per hari keterlambatan

                    // Tampilkan pesan konfirmasi denda
                    int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "Pengembalian terlambat. Denda: Rp " + denda + "\nApakah Anda ingin melanjutkan?",
                            "Konfirmasi Denda",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    // Jika user memilih NO, batalkan proses
                    if (confirm == JOptionPane.NO_OPTION) {
                        JOptionPane.showMessageDialog(this, "Proses pengembalian dibatalkan.");
                        return;
                    }
                }

                // Update data transaksi dengan tanggal dikembalikan dan denda
                String updateTransaksi = "UPDATE transaksi SET tanggal_dikembalikan = ?, denda = ? WHERE id_transaksi = ?";
                PreparedStatement pstUpdateTransaksi = con.prepareStatement(updateTransaksi);
                pstUpdateTransaksi.setDate(1, tanggalDikembalikan);
                pstUpdateTransaksi.setDouble(2, denda);
                pstUpdateTransaksi.setInt(3, idTransaksi);

                int transaksiUpdated = pstUpdateTransaksi.executeUpdate();

                // Update status mobil menjadi "Tersedia"
                String updateMobil = "UPDATE mobil SET status = 'Tersedia' WHERE id_mobil = ?";
                PreparedStatement pstUpdateMobil = con.prepareStatement(updateMobil);
                pstUpdateMobil.setInt(1, idMobil);

                int mobilUpdated = pstUpdateMobil.executeUpdate();

                if (transaksiUpdated > 0 && mobilUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Pengembalian berhasil diproses. Total denda: Rp " + denda);
                    clear();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal memperbarui data pengembalian.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Data transaksi tidak ditemukan.");
            }

            con.close(); // Menutup koneksi setelah selesai
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    public void clear(){
        // Reset ComboBox untuk ID Mobil dan ID Pelanggan
        combo_idTransaksi.setSelectedItem(null);
        combo_idPelanggan.setSelectedItem(null);
    
        txt_idTransaksi.setText("");
        txt_merk.setText("");
        txt_model.setText("");
        txt_tipe.setText("");
        txt_namaPelanggan.setText("");
        tgl_sewa.setText("");
        tgl_kembali.setText("");
      
    }


    
  
    
    
    
    
    //checking whether book already allocated or not
    
  
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jdl_username2 = new javax.swing.JLabel();
        FindData = new javax.swing.JButton();
        Proses = new javax.swing.JButton();
        jdl_username3 = new javax.swing.JLabel();
        combo_idTransaksi = new javax.swing.JComboBox<>();
        combo_idPelanggan = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lbl_bookError = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        tgl_sewa = new javax.swing.JTextField();
        txt_merk = new javax.swing.JTextField();
        txt_namaPelanggan = new javax.swing.JTextField();
        txt_idTransaksi = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        tgl_kembali = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        lbl_denda = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txt_model = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txt_tipe = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setBackground(new java.awt.Color(166, 45, 45));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 153));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/return.png"))); // NOI18N
        jLabel2.setText("Pengembalian");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, -1, -1));

        jdl_username2.setBackground(new java.awt.Color(255, 0, 0));
        jdl_username2.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jdl_username2.setForeground(new java.awt.Color(0, 0, 153));
        jdl_username2.setText("ID Pelanggan:");
        jPanel1.add(jdl_username2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 340, -1, -1));

        FindData.setBackground(new java.awt.Color(153, 153, 255));
        FindData.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        FindData.setForeground(new java.awt.Color(255, 255, 255));
        FindData.setText("FIND");
        FindData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FindDataActionPerformed(evt);
            }
        });
        jPanel1.add(FindData, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 450, 250, 40));

        Proses.setBackground(new java.awt.Color(0, 0, 153));
        Proses.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Proses.setForeground(new java.awt.Color(255, 255, 255));
        Proses.setText("PROSES");
        Proses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ProsesActionPerformed(evt);
            }
        });
        jPanel1.add(Proses, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 500, 250, 40));

        jdl_username3.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jdl_username3.setForeground(new java.awt.Color(0, 0, 153));
        jdl_username3.setText("ID Transaksi:");
        jPanel1.add(jdl_username3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 260, -1, -1));

        combo_idTransaksi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--- Pilih ---" }));
        combo_idTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_idTransaksiActionPerformed(evt);
            }
        });
        jPanel1.add(combo_idTransaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 290, 240, 40));

        combo_idPelanggan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--- Pilih ---" }));
        combo_idPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_idPelangganActionPerformed(evt);
            }
        });
        jPanel1.add(combo_idPelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 370, 240, 40));

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 0, 420, 710));

        jPanel8.setBackground(new java.awt.Color(0, 0, 153));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/rental-car.png"))); // NOI18N
        jLabel8.setText("Detail Penyewaan");
        jPanel8.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 160, -1, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("ID Transaksi :");
        jPanel8.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, 90, -1));

        lbl_bookError.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_bookError.setForeground(new java.awt.Color(255, 204, 51));
        jPanel8.add(lbl_bookError, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 550, -1, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Detail Sewa");
        jPanel8.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 490, 140, -1));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Nama Pelanggan :");
        jPanel8.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 530, 150, -1));

        tgl_sewa.setEditable(false);
        tgl_sewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgl_sewaActionPerformed(evt);
            }
        });
        jPanel8.add(tgl_sewa, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 580, 290, 30));

        txt_merk.setEditable(false);
        txt_merk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_merkActionPerformed(evt);
            }
        });
        jPanel8.add(txt_merk, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 350, 290, 30));

        txt_namaPelanggan.setEditable(false);
        txt_namaPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_namaPelangganActionPerformed(evt);
            }
        });
        jPanel8.add(txt_namaPelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 530, 290, 30));

        txt_idTransaksi.setEditable(false);
        txt_idTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_idTransaksiActionPerformed(evt);
            }
        });
        jPanel8.add(txt_idTransaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 260, 290, 30));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Tanggal Sewa:");
        jPanel8.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 580, 130, 30));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Tanggal Kembali :");
        jPanel8.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 630, 140, 30));

        tgl_kembali.setEditable(false);
        tgl_kembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgl_kembaliActionPerformed(evt);
            }
        });
        jPanel8.add(tgl_kembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 630, 290, 30));

        jLabel5.setBackground(new java.awt.Color(102, 102, 255));
        jLabel5.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Back_40Px2.png"))); // NOI18N
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });
        jPanel8.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        lbl_denda.setFont(new java.awt.Font("Segoe UI Black", 1, 14)); // NOI18N
        lbl_denda.setForeground(new java.awt.Color(255, 204, 51));
        jPanel8.add(lbl_denda, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 540, 300, 40));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Merk");
        jPanel8.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 350, 90, 30));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Model");
        jPanel8.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 390, 90, 30));

        txt_model.setEditable(false);
        txt_model.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_modelActionPerformed(evt);
            }
        });
        jPanel8.add(txt_model, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 390, 290, 30));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Tipe");
        jPanel8.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 430, 90, 30));

        txt_tipe.setEditable(false);
        txt_tipe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_tipeActionPerformed(evt);
            }
        });
        jPanel8.add(txt_tipe, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 430, 290, 30));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Detail Mobil");
        jPanel8.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 320, 140, -1));

        jPanel2.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 710));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 890, 710));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void FindDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FindDataActionPerformed
      if (!combo_idTransaksi.getSelectedItem().toString().isEmpty() && !combo_idPelanggan.getSelectedItem().toString().isEmpty()) {
    getSewaMobil();
}else{
            JOptionPane.showMessageDialog(this, "Form is Empty. Please Fill the Form!");
            bersih();
        }
   

    }//GEN-LAST:event_FindDataActionPerformed

    private void tgl_sewaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgl_sewaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tgl_sewaActionPerformed

    private void txt_merkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_merkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_merkActionPerformed

    private void txt_namaPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_namaPelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_namaPelangganActionPerformed

    private void txt_idTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_idTransaksiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_idTransaksiActionPerformed

    private void tgl_kembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgl_kembaliActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tgl_kembaliActionPerformed

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        // TODO add your handling code here:
        HomePage home = new HomePage();
        home.setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel5MouseClicked

    private void ProsesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ProsesActionPerformed
         if (!combo_idTransaksi.getSelectedItem().toString().isEmpty() && !combo_idPelanggan.getSelectedItem().toString().isEmpty()) {
            prosesPengembalian();
        }else{
                    JOptionPane.showMessageDialog(this, "Form is Empty. Please Fill the Form!");
                    bersih();
                }
   

    }//GEN-LAST:event_ProsesActionPerformed

    private void combo_idTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_idTransaksiActionPerformed
     
    }//GEN-LAST:event_combo_idTransaksiActionPerformed

    private void combo_idPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_idPelangganActionPerformed
     
    }//GEN-LAST:event_combo_idPelangganActionPerformed

    private void txt_modelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_modelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_modelActionPerformed

    private void txt_tipeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_tipeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_tipeActionPerformed
    private void bersih(){
    combo_idTransaksi.setSelectedItem(null);
    combo_idPelanggan.setSelectedItem(null);
    }
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
            java.util.logging.Logger.getLogger(Pengembalian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Pengembalian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Pengembalian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Pengembalian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
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
                new Pengembalian().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton FindData;
    private javax.swing.JButton Proses;
    private javax.swing.JComboBox<String> combo_idPelanggan;
    private javax.swing.JComboBox<String> combo_idTransaksi;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JLabel jdl_username2;
    private javax.swing.JLabel jdl_username3;
    private javax.swing.JLabel lbl_bookError;
    private javax.swing.JLabel lbl_denda;
    private javax.swing.JTextField tgl_kembali;
    private javax.swing.JTextField tgl_sewa;
    private javax.swing.JTextField txt_idTransaksi;
    private javax.swing.JTextField txt_merk;
    private javax.swing.JTextField txt_model;
    private javax.swing.JTextField txt_namaPelanggan;
    private javax.swing.JTextField txt_tipe;
    // End of variables declaration//GEN-END:variables
}
