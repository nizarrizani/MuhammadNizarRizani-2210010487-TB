/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Jframe;



//import static java.awt.Color.
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;

public class HomePage extends javax.swing.JFrame {
    
 
    public HomePage() {
        initComponents();
        setTitle("Dashboard"); // Menambahkan judul aplikasi
        
        setPelangganToTabel();
        setMobilToTabel();
        countMobilTersedia();
        countMobilDisewa();
        countPelanggan();
        setTotalPenjualan();
    }
    
    public void setPelangganToTabel() {
    try {
        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();

        // Query untuk mengambil semua data pelanggan
        String query = "SELECT * FROM pelanggan";
        ResultSet rs = st.executeQuery(query);

        // Mendapatkan model tabel pelanggan
        DefaultTableModel model = (DefaultTableModel) tbl_pelanggan.getModel();
        model.setRowCount(0); // Membersihkan tabel sebelum memasukkan data baru

        while (rs.next()) {
            String idPelanggan = rs.getString("id_pelanggan");
            String nama = rs.getString("nama");
            String alamat = rs.getString("alamat");
            String telepon = rs.getString("no_telepon");
            String email = rs.getString("email");

            // Menambahkan data ke tabel
            model.addRow(new Object[]{idPelanggan, nama, alamat, telepon, email});
        }

        con.close(); // Menutup koneksi setelah selesai
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Gagal memuat data pelanggan: " + e.getMessage());
    }
}

public void setMobilToTabel() {
    try {
        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();

        // Query untuk mengambil data mobil dengan kolom yang diinginkan
        String query = "SELECT id_mobil, merk, model, tipe, harga_sewa_per_hari FROM mobil";
        ResultSet rs = st.executeQuery(query);

        // Mendapatkan model tabel mobil
        DefaultTableModel model = (DefaultTableModel) tbl_mobil.getModel();
        model.setRowCount(0); // Membersihkan tabel sebelum memasukkan data baru

        while (rs.next()) {
            String idMobil = rs.getString("id_mobil");
            String merk = rs.getString("merk");
            String modelMobil = rs.getString("model");
            String tipe = rs.getString("tipe");
            int hargaSewa = rs.getInt("harga_sewa_per_hari");
            String formattedHarga = formatToCurrency(hargaSewa);

            // Menambahkan data ke tabel
            model.addRow(new Object[]{idMobil, merk, modelMobil, tipe, formattedHarga});
        }

        con.close(); // Menutup koneksi setelah selesai
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Gagal memuat data mobil: " + e.getMessage());
    }
}

public void countMobilTersedia() {
    try {
        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();

        // Query untuk menghitung jumlah mobil dengan status 'Tersedia'
        String query = "SELECT COUNT(*) AS total FROM mobil WHERE status = 'Tersedia'";
        ResultSet rs = st.executeQuery(query);

        if (rs.next()) {
            int totalTersedia = rs.getInt("total");

            // Menampilkan jumlah mobil tersedia di label atau komponen lain
            lbl_mobilCount.setText(String.valueOf(totalTersedia));
        }

        con.close(); // Menutup koneksi setelah selesai
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Gagal menghitung mobil tersedia: " + e.getMessage());
    }
}

public void countMobilDisewa() {
    try {
        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();

        // Query untuk menghitung jumlah mobil dengan status 'Disewa'
        String query = "SELECT COUNT(*) AS total FROM mobil WHERE status = 'Disewa'";
        ResultSet rs = st.executeQuery(query);

        if (rs.next()) {
            int totalDisewa = rs.getInt("total");

            // Menampilkan jumlah mobil disewa di label atau komponen lain
            lbl_mobilDisewa.setText(String.valueOf(totalDisewa));
        }

        con.close(); // Menutup koneksi setelah selesai
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Gagal menghitung mobil disewa: " + e.getMessage());
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

    public void setTotalPenjualan() {
        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT SUM(total_biaya) AS total_penghasilan FROM transaksi");

            if (rs.next()) {
                int totalPenghasilan = rs.getInt("total_penghasilan");
                
                String formattedHarga = formatToCurrency(totalPenghasilan); // Format harga menjadi Rp

                // Set total penjualan ke lbl_totalPenjualan
                lbl_totalPenghasilan.setText(String.valueOf(formattedHarga));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
}

    
    
    public void countPelanggan() {

        int noOfCustomers = 0;
        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from pelanggan");
            while (rs.next()) {
                noOfCustomers++;

            }
            lbl_pelangganCount.setText(Integer.toString(noOfCustomers));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }






    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel1 = new java.awt.Panel();
        label2 = new java.awt.Label();
        label1 = new java.awt.Label();
        jPanel1 = new javax.swing.JPanel();
        pengembalian = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        laporan_mobil = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        mobil = new javax.swing.JLabel();
        pelanggan = new javax.swing.JLabel();
        penyewaan = new javax.swing.JLabel();
        laporan_transaksi = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        lbl_mobilCount = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        lbl_mobilDisewa = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbl_mobil = new rojerusan.RSTableMetro();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbl_pelanggan = new rojerusan.RSTableMetro();
        jPanel8 = new javax.swing.JPanel();
        lbl_pelangganCount = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        lbl_totalPenghasilan = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(1200, 710));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panel1.setBackground(new java.awt.Color(14, 40, 94));
        panel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        label2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        label2.setForeground(new java.awt.Color(255, 255, 255));
        label2.setText("MOBILIN - Rental Mobil");
        panel1.add(label2, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 10, 410, 50));
        label2.getAccessibleContext().setAccessibleName("Water Blue Library");

        label1.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        label1.setForeground(new java.awt.Color(255, 255, 255));
        label1.setText("Selamat Datang, Admin");
        label1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                label1PropertyChange(evt);
            }
        });
        panel1.add(label1, new org.netbeans.lib.awtextra.AbsoluteConstraints(914, 10, -1, 50));
        label1.getAccessibleContext().setAccessibleName("Admin");

        getContentPane().add(panel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 1, 1190, 70));

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pengembalian.setBackground(new java.awt.Color(51, 51, 51));
        pengembalian.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        pengembalian.setForeground(new java.awt.Color(255, 255, 255));
        pengembalian.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/return.png"))); // NOI18N
        pengembalian.setText("   Pengembalian");
        pengembalian.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pengembalianMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pengembalianMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pengembalianMouseExited(evt);
            }
        });
        jPanel1.add(pengembalian, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 350, 200, 40));

        jLabel7.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Fitur");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, -1));

        laporan_mobil.setFont(new java.awt.Font("Segoe UI Semibold", 0, 16)); // NOI18N
        laporan_mobil.setForeground(new java.awt.Color(255, 255, 255));
        laporan_mobil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_View_Details_26px.png"))); // NOI18N
        laporan_mobil.setText("   Laporan Mobil Tersedia");
        laporan_mobil.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                laporan_mobilMouseClicked(evt);
            }
        });
        jPanel1.add(laporan_mobil, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 450, -1, 40));

        jPanel2.setBackground(new java.awt.Color(51, 153, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 552, -1, -1));

        jButton3.setBackground(new java.awt.Color(204, 0, 0));
        jButton3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_Exit_26px_2.png"))); // NOI18N
        jButton3.setText("Logout");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 540, 230, 40));

        jPanel6.setBackground(new java.awt.Color(204, 204, 255));

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_Home_26px_2.png"))); // NOI18N
        jLabel2.setText(" Home Page");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addComponent(jLabel2)
                .addContainerGap(72, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 270, 60));

        mobil.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        mobil.setForeground(new java.awt.Color(255, 255, 255));
        mobil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/mobility.png"))); // NOI18N
        mobil.setText("   Data Mobil");
        mobil.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mobilMouseClicked(evt);
            }
        });
        jPanel1.add(mobil, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, -1, 40));

        pelanggan.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        pelanggan.setForeground(new java.awt.Color(255, 255, 255));
        pelanggan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_Conference_26px.png"))); // NOI18N
        pelanggan.setText("        Data Pelanggan");
        pelanggan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pelangganMouseClicked(evt);
            }
        });
        jPanel1.add(pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 230, -1, 40));

        penyewaan.setBackground(new java.awt.Color(51, 51, 51));
        penyewaan.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        penyewaan.setForeground(new java.awt.Color(255, 255, 255));
        penyewaan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/rental-car.png"))); // NOI18N
        penyewaan.setText("   Penyewaan");
        penyewaan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                penyewaanMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                penyewaanMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                penyewaanMouseExited(evt);
            }
        });
        jPanel1.add(penyewaan, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 290, 200, 40));

        laporan_transaksi.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        laporan_transaksi.setForeground(new java.awt.Color(255, 255, 255));
        laporan_transaksi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_View_Details_26px.png"))); // NOI18N
        laporan_transaksi.setText("        Laporan");
        laporan_transaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                laporan_transaksiMouseClicked(evt);
            }
        });
        jPanel1.add(laporan_transaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 400, -1, 40));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 265, 640));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(96, 123, 179)));
        jPanel5.setToolTipText("");

        lbl_mobilCount.setFont(new java.awt.Font("Segoe UI Black", 1, 35)); // NOI18N
        lbl_mobilCount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/mobility.png"))); // NOI18N
        lbl_mobilCount.setText(" 10");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_mobilCount, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_mobilCount, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 120, 200, 130));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setText("Detail Mobil");
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 480, 120, 30));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setText("Total Mobil Tersedia");
        getContentPane().add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 90, 160, 30));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(96, 123, 179)));

        lbl_mobilDisewa.setFont(new java.awt.Font("Segoe UI Black", 1, 50)); // NOI18N
        lbl_mobilDisewa.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_mobilDisewa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/rental-car.png"))); // NOI18N
        lbl_mobilDisewa.setText(" 10");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lbl_mobilDisewa, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_mobilDisewa, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 120, 200, 130));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setText("Total Mobil Disewa");
        getContentPane().add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 90, 140, 30));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel22.setText("Detail Pelanggan");
        getContentPane().add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 260, 130, 30));

        tbl_mobil.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Merk", "Model", "Tipe", "Harga Sewa/Hari"
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

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 510, 850, 150));

        tbl_pelanggan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nama", "Alamat", "No. Telp", "Email"
            }
        ));
        tbl_pelanggan.setColorBackgoundHead(new java.awt.Color(96, 123, 179));
        tbl_pelanggan.setRowHeight(25);
        tbl_pelanggan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_pelangganMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tbl_pelanggan);

        getContentPane().add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 290, 850, 170));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(96, 123, 179)));
        jPanel8.setToolTipText("");

        lbl_pelangganCount.setFont(new java.awt.Font("Segoe UI Black", 1, 50)); // NOI18N
        lbl_pelangganCount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_People_50px.png"))); // NOI18N
        lbl_pelangganCount.setText(" 10");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(lbl_pelangganCount, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_pelangganCount, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 120, 200, 130));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setText("Total Pelanggan");
        getContentPane().add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 90, 130, 30));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(96, 123, 179)));
        jPanel9.setToolTipText("");

        lbl_totalPenghasilan.setFont(new java.awt.Font("Segoe UI Black", 1, 30)); // NOI18N
        lbl_totalPenghasilan.setText(" 10");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_totalPenghasilan, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_totalPenghasilan, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 120, 200, 130));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setText("Total Penghasilan");
        getContentPane().add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 90, 160, 30));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void label1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_label1PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_label1PropertyChange

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked
    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        LogIn log = new LogIn();
        log.setVisible(true);
        this.dispose();
}

    }//GEN-LAST:event_jButton3MouseClicked

    private void pengembalianMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pengembalianMouseExited
        // TODO add your handling code here:
        //jPanel_returnbook.setBackground(mouseExitColor);
    }//GEN-LAST:event_pengembalianMouseExited

    private void pengembalianMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pengembalianMouseEntered
        // TODO add your handling code here:
        //jPanel_returnbook.setBackground(mouseEnterColor);
    }//GEN-LAST:event_pengembalianMouseEntered

    private void mobilMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mobilMouseClicked
        // TODO add your handling code here:
        DataMobil dataMobil = new DataMobil();
        dataMobil.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_mobilMouseClicked

    private void tbl_mobilMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_mobilMouseClicked

    }//GEN-LAST:event_tbl_mobilMouseClicked

    private void tbl_pelangganMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_pelangganMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbl_pelangganMouseClicked

    private void pengembalianMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pengembalianMouseClicked
        Pengembalian pengembalian = new Pengembalian();
        pengembalian.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_pengembalianMouseClicked

    private void pelangganMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pelangganMouseClicked
        DataPelanggan manage = new DataPelanggan();
        manage.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_pelangganMouseClicked

    private void laporan_mobilMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_laporan_mobilMouseClicked
        LaporanMobil laporanMobil = new LaporanMobil();
        laporanMobil.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_laporan_mobilMouseClicked

    private void penyewaanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_penyewaanMouseClicked
       Penyewaan Penyewaan = new Penyewaan();
        Penyewaan.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_penyewaanMouseClicked

    private void penyewaanMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_penyewaanMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_penyewaanMouseEntered

    private void penyewaanMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_penyewaanMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_penyewaanMouseExited

    private void laporan_transaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_laporan_transaksiMouseClicked
       LaporanTransaksi laporanTransaksi = new LaporanTransaksi();
        laporanTransaksi.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_laporan_transaksiMouseClicked

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
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomePage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HomePage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private java.awt.Label label1;
    private java.awt.Label label2;
    private javax.swing.JLabel laporan_mobil;
    private javax.swing.JLabel laporan_transaksi;
    private javax.swing.JLabel lbl_mobilCount;
    private javax.swing.JLabel lbl_mobilDisewa;
    private javax.swing.JLabel lbl_pelangganCount;
    private javax.swing.JLabel lbl_totalPenghasilan;
    private javax.swing.JLabel mobil;
    private java.awt.Panel panel1;
    private javax.swing.JLabel pelanggan;
    private javax.swing.JLabel pengembalian;
    private javax.swing.JLabel penyewaan;
    private rojerusan.RSTableMetro tbl_mobil;
    private rojerusan.RSTableMetro tbl_pelanggan;
    // End of variables declaration//GEN-END:variables
}
