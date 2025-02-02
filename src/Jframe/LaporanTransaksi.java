package Jframe;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class LaporanTransaksi extends javax.swing.JFrame {

    DefaultTableModel model;
    
    public LaporanTransaksi() {
        initComponents();
        setTitle("Data Riwayat");
        setRiwayatToTabel();
    }

    public void setRiwayatToTabel() {
        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();

            // Query untuk mengambil seluruh data transaksi
            String query = "SELECT t.id_transaksi, t.id_pelanggan, t.id_mobil, t.tanggal_sewa, t.tanggal_kembali, t.tanggal_dikembalikan, t.total_biaya, t.denda " +
                           "FROM transaksi t " +
                           "ORDER BY t.id_transaksi ASC";
            ResultSet rs = st.executeQuery(query);

            // Mendapatkan model tabel
            DefaultTableModel model = (DefaultTableModel) tbl_transaksi.getModel();
            model.setRowCount(0); // Membersihkan tabel sebelum memasukkan data baru

            while (rs.next()) {
                int idTransaksi = rs.getInt("id_transaksi");
                String idPelanggan = rs.getString("id_pelanggan");
                String idMobil = rs.getString("id_mobil");
                Date tanggalSewa = rs.getDate("tanggal_sewa");
                Date tanggalKembali = rs.getDate("tanggal_kembali");
                Date tanggalDikembalikan = rs.getDate("tanggal_dikembalikan");
                double totalBiaya = rs.getDouble("total_biaya");
                double denda = rs.getDouble("denda");

                // Menambahkan data ke tabel dengan format yang diinginkan
                model.addRow(new Object[]{
                    idTransaksi,
                    idPelanggan,
                    idMobil,
                    tanggalSewa != null ? tanggalSewa.toString() : "-", // Konversi ke String atau tanda "-"
                    tanggalKembali != null ? tanggalKembali.toString() : "-",
                    tanggalDikembalikan != null ? tanggalDikembalikan.toString() : "-",
                    formatToCurrency(totalBiaya), // Format harga menggunakan fungsi formatToCurrency
                    formatToCurrency(denda)      // Format harga denda
                });
            }

            con.close(); // Menutup koneksi setelah selesai
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data transaksi: " + e.getMessage());
        }
    }

    private String formatToCurrency(double price) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID")); // Menggunakan locale Indonesia
        return formatter.format(price);
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_transaksi = new rojerusan.RSTableMetro();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tbl_transaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Transaksi", "ID Pelanggan", "ID Mobil", "Tgl. Sewa", "Tgl. Kembali", "Tgl. Dikembalikan", "Total Biaya", "Denda"
            }
        ));
        tbl_transaksi.setColorBackgoundHead(new java.awt.Color(51, 23, 185));
        tbl_transaksi.setRowHeight(25);
        tbl_transaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_transaksiMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_transaksi);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(42, 146, 816, 373));

        jLabel4.setBackground(new java.awt.Color(51, 23, 185));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 23, 185));
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/ticket.png"))); // NOI18N
        jLabel4.setText("Laporan Transaksi Penyewaan");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 70, -1, -1));

        jLabel7.setBackground(new java.awt.Color(102, 102, 255));
        jLabel7.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Back_40Px2.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, 50));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 920, 620));

        setSize(new java.awt.Dimension(936, 628));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tbl_transaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_transaksiMouseClicked

    }//GEN-LAST:event_tbl_transaksiMouseClicked

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        // TODO add your handling code here:
        HomePage home = new HomePage();
        home.setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel7MouseClicked

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
            java.util.logging.Logger.getLogger(LaporanTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LaporanTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LaporanTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LaporanTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LaporanTransaksi().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private rojerusan.RSTableMetro tbl_transaksi;
    // End of variables declaration//GEN-END:variables
}
