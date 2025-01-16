package Jframe;
        
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.Date;
import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class Penyewaan extends javax.swing.JFrame {

    public Penyewaan() {
        initComponents();
        setTitle("Sewa Mobil");
        tampil_combo();
        tampil_combo2();
        
        // Tambahkan listener ke tgl_kembali
        tgl_kembali.addPropertyChangeListener("date", evt -> setTotalBiaya());
    }
    
    
   
    public void tampil_combo() {
    try {
        // Membuat koneksi ke database
        Connection con = DBConnection.getConnection();

        // Query untuk mendapatkan mobil yang statusnya 'Tersedia'
        PreparedStatement pst = con.prepareStatement("SELECT * FROM mobil WHERE status = 'Tersedia'");
        ResultSet rs = pst.executeQuery();
        
        // Menambahkan data ke dalam combo box
        while (rs.next()) {
            // Menambahkan id_mobil ke combo box
            combo_idMobil.addItem(rs.getString("id_mobil"));
        }

    } catch (Exception e) {
        // Menangani exception jika ada error
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}

  public void tampil_combo2() {
    try {
        // Membuat koneksi ke database
        Connection con = DBConnection.getConnection();

        // Query untuk mengambil semua data dari tabel pelanggan
        PreparedStatement pst = con.prepareStatement("SELECT * FROM pelanggan");
        ResultSet rs = pst.executeQuery();
        
        // Menambahkan data ke dalam combo box
        while (rs.next()) {
            // Menambahkan id_pelanggan ke combo box
            combo_idPelanggan.addItem(rs.getString("id_pelanggan"));
        }

    } catch (Exception e) {
        // Menangani exception jika ada error
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}

public void getMobil() {
    int mobilId;
    try {
        // Parse the selected item to an integer
        mobilId = Integer.parseInt(combo_idMobil.getSelectedItem().toString());
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Error Tidak Ditemukan");
        return;
    }

    try {
        Connection con = DBConnection.getConnection();
        PreparedStatement pst = con.prepareStatement("SELECT * FROM mobil WHERE id_mobil=?");
        pst.setInt(1, mobilId);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            txt_idMobil.setText(rs.getString("id_mobil"));
            txt_merk.setText(rs.getString("merk"));
            txt_model.setText(rs.getString("model"));
            txt_tipe.setText(rs.getString("tipe"));
            txt_harga.setText(rs.getString("harga_sewa_per_hari"));
        } else {
            JOptionPane.showMessageDialog(null, "Error Tidak Ditemukan!");
            clear();
        }

        con.close(); // Close the connection after use
    } catch (Exception ex) {
        ex.printStackTrace();
        // Handle the error, such as displaying an error message or logging to the console
        JOptionPane.showMessageDialog(null, "An error occurred while fetching the book details.");
    }
}

 
    
    public void getPelanggan() {
    String pelangganID = combo_idPelanggan.getSelectedItem().toString();

    try {
        Connection con = DBConnection.getConnection();
        PreparedStatement pst = con.prepareStatement("SELECT * FROM pelanggan WHERE id_pelanggan=?");
        pst.setString(1, pelangganID);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            txt_idPelanggan.setText(rs.getString("id_pelanggan"));
            txt_nama.setText(rs.getString("nama"));
            txt_alamat.setText(rs.getString("alamat"));
            txt_noTelp.setText(rs.getString("no_telepon"));
            txt_email.setText(rs.getString("email"));
        } else {
            JOptionPane.showMessageDialog(this, "ID Pelanggan Tidak Ditemukan!");
            clear();
        }

        con.close(); // Menutup koneksi setelah selesai menggunakan database
    } catch (Exception e) {
        e.printStackTrace();
        // Menambahkan penanganan kesalahan, seperti menampilkan pesan error atau log ke konsol
    }
}

    // Insert Penyewaan
public void SewaMobil() {
    // Ambil data dari form
    int mobilID = Integer.parseInt(combo_idMobil.getSelectedItem().toString());
    String pelangganID = combo_idPelanggan.getSelectedItem().toString();

    Date uIssueDate = tgl_sewa.getDate();  // Tanggal Sewa
    Date uDueDate = tgl_kembali.getDate();  // Tanggal Kembali

    if (uIssueDate != null && uDueDate != null) {
        // Validasi tanggal, pastikan tanggal sewa tidak lebih besar dari tanggal kembali
        if (uIssueDate.compareTo(uDueDate) > 0) {
            JOptionPane.showMessageDialog(this, "Tanggal Sewa tidak bisa lebih besar dari Tanggal Kembali.");
        } else {
            long time11 = uIssueDate.getTime();
            long time12 = uDueDate.getTime();

            // Mengubah Date menjadi java.sql.Date
            java.sql.Date sIssueDate = new java.sql.Date(time11);
            java.sql.Date sDueDate = new java.sql.Date(time12);

            // Hitung durasi sewa
            long diff = sDueDate.getTime() - sIssueDate.getTime();
            long days = diff / (1000 * 60 * 60 * 24); // Menghitung jumlah hari

            // Ambil harga sewa mobil
            int hargaSewaPerHari = getHargaSewaPerHari(mobilID);  // Misalnya ambil harga sewa mobil dari database berdasarkan mobilID

            // Hitung total biaya
            int totalBiaya = (int) (hargaSewaPerHari * days);

            // Menyimpan data ke dalam tabel transaksi
            try {
                Connection con = DBConnection.getConnection();
                String sql = "INSERT INTO transaksi (id_pelanggan, id_mobil, tanggal_sewa, tanggal_kembali, total_biaya) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pst = con.prepareStatement(sql);

                // Set parameter untuk query
                pst.setString(1, pelangganID);  // ID Pelanggan
                pst.setInt(2, mobilID);  // ID Mobil
                pst.setDate(3, sIssueDate);  // Tanggal Sewa
                pst.setDate(4, sDueDate);  // Tanggal Kembali
                pst.setInt(5, totalBiaya);  // Total Biaya

                // Eksekusi query
                int rowCount = pst.executeUpdate();
                if (rowCount > 0) {
                    JOptionPane.showMessageDialog(this, "Penyewaan berhasil dilakukan.");
                } else {
                    JOptionPane.showMessageDialog(this, "Penyewaan gagal dilakukan.");
                }
                con.close();  // Menutup koneksi setelah selesai
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan dalam penyimpanan data.");
            }
        }
    } else {
        JOptionPane.showMessageDialog(this, "Harap pilih tanggal sewa dan tanggal kembali.");
    }
}

// Fungsi untuk mendapatkan harga sewa per hari berdasarkan ID mobil
private int getHargaSewaPerHari(int mobilID) {
    int harga = 0;
    try {
        Connection con = DBConnection.getConnection();
        String sql = "SELECT harga_sewa_per_hari FROM mobil WHERE id_mobil = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, mobilID);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            harga = rs.getInt("harga_sewa_per_hari");
        }
        con.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return harga;
}

public void updateStatusMobil() {
    // Ambil ID mobil yang dipilih dari comboBox
    int mobilID = Integer.parseInt(combo_idMobil.getSelectedItem().toString());

    // Validasi agar ID mobil yang dipilih tidak kosong
    if (mobilID <= 0) {
        JOptionPane.showMessageDialog(this, "Pilih mobil yang valid.");
        return;
    }

    try {
        // Koneksi ke database
        Connection con = DBConnection.getConnection();

        // Query untuk mengubah status menjadi 'Disewa'
        String sql = "UPDATE mobil SET status = 'Disewa' WHERE id_mobil = ?";
        
        // Siapkan PreparedStatement
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, mobilID); // Set ID mobil yang dipilih sebagai parameter

        // Eksekusi query
        int rowCount = pst.executeUpdate();

        // Cek apakah query berhasil
        if (rowCount > 0) {
            JOptionPane.showMessageDialog(this, "Status mobil berhasil diperbarui menjadi Disewa.");
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui status mobil.");
        }

        con.close(); // Menutup koneksi setelah selesai

    } catch (SQLException e) {
        // Menangani error jika ada masalah koneksi atau query
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memperbarui status mobil.");
        e.printStackTrace();
    }
}

private void setTotalBiaya() {
    try {
        // Ambil nilai harga dari txt_harga
        int hargaPerHari = Integer.parseInt(txt_harga.getText());

        // Ambil tanggal dari tgl_sewa dan tgl_kembali
        Date tanggalSewa = tgl_sewa.getDate();
        Date tanggalKembali = tgl_kembali.getDate();
        
        // Validasi jika salah satu tanggal belum dipilih
        if (tanggalSewa == null || tanggalKembali == null) {
            JOptionPane.showMessageDialog(this, "Tanggal sewa atau tanggal kembali belum dipilih!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validasi jika tanggal sewa lebih besar dari tanggal kembali
        if (tanggalSewa.after(tanggalKembali)) {
            JOptionPane.showMessageDialog(this, 
                "Tanggal sewa tidak boleh lebih besar dari tanggal kembali!", 
                "Kesalahan", 
                JOptionPane.ERROR_MESSAGE);

            // Set tgl_sewa ke tanggal kembali
            tgl_sewa.setDate(tanggalKembali);
            return;
        }

        // Validasi jika tanggal kembali lebih kecil dari tanggal sewa
        if (tanggalKembali.before(tanggalSewa)) {
            JOptionPane.showMessageDialog(this, 
                "Tanggal kembali tidak boleh lebih kecil dari tanggal sewa!", 
                "Kesalahan", 
                JOptionPane.ERROR_MESSAGE);

            // Set tgl_kembali ke tanggal sewa
            tgl_kembali.setDate(tanggalSewa);
            return;
        }

        // Hitung selisih hari
        long diffInMillies = tanggalKembali.getTime() - tanggalSewa.getTime();
        long days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        // Jika selisih hari adalah 0, minimal dikenakan biaya untuk 1 hari
        if (days == 0) {
            days = 1;
        }

        // Hitung total biaya
        int totalBiaya = (int) days * hargaPerHari;
        
        String formattedHarga = formatToCurrency(totalBiaya); // Format harga menjadi Rp

        // Set total biaya ke field total_biaya
        total_biaya.setText(String.valueOf(formattedHarga));

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Harga sewa tidak valid!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
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

private void clear() {
    // Reset ComboBox untuk ID Mobil dan ID Pelanggan
    combo_idMobil.setSelectedItem(null);
    combo_idPelanggan.setSelectedItem(null);
    
    // Reset Tanggal Sewa dan Tanggal Kembali (misalnya menggunakan JDateChooser)
    tgl_sewa.setDate(null);  // Untuk JDateChooser
    tgl_kembali.setDate(null);  // Untuk JDateChooser
    
    // Reset Total Biaya
    total_biaya.setText("");  // Menghapus nilai dalam text field

    txt_idPelanggan.setText("");
    txt_nama.setText("");
    txt_alamat.setText("");
    txt_noTelp.setText("");
    txt_email.setText("");
    
    txt_idMobil.setText("");
    txt_merk.setText("");
    txt_model.setText("");
    txt_tipe.setText("");
    txt_harga.setText("");
    
}




    
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
        jdl_username = new javax.swing.JLabel();
        jdl_username1 = new javax.swing.JLabel();
        jdl_username2 = new javax.swing.JLabel();
        jdl_username3 = new javax.swing.JLabel();
        IssueBook = new javax.swing.JButton();
        tgl_kembali = new com.toedter.calendar.JDateChooser();
        tgl_sewa = new com.toedter.calendar.JDateChooser();
        combo_idPelanggan = new javax.swing.JComboBox<>();
        combo_idMobil = new javax.swing.JComboBox<>();
        total_biaya = new javax.swing.JTextField();
        jdl_username6 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lbl_bookError = new javax.swing.JLabel();
        txt_harga = new javax.swing.JTextField();
        txt_merk = new javax.swing.JTextField();
        txt_tipe = new javax.swing.JTextField();
        txt_idMobil = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jdl_username4 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txt_model = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        txt_idPelanggan = new javax.swing.JTextField();
        txt_alamat = new javax.swing.JTextField();
        lbl_studentError = new javax.swing.JLabel();
        jdl_username5 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txt_nama = new javax.swing.JTextField();
        txt_noTelp = new javax.swing.JTextField();
        txt_email = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 153));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/rental-car.png"))); // NOI18N
        jLabel2.setText("Sewa Mobil");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, 260, 50));

        jdl_username.setBackground(new java.awt.Color(255, 0, 0));
        jdl_username.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jdl_username.setForeground(new java.awt.Color(0, 0, 153));
        jdl_username.setText("Tanggal Sewa :");
        jPanel1.add(jdl_username, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 300, -1, -1));

        jdl_username1.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jdl_username1.setForeground(new java.awt.Color(0, 0, 153));
        jdl_username1.setText("Masukkan ID Mobil :");
        jPanel1.add(jdl_username1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 140, -1, -1));

        jdl_username2.setBackground(new java.awt.Color(153, 153, 255));
        jdl_username2.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jdl_username2.setForeground(new java.awt.Color(0, 0, 153));
        jdl_username2.setText("Masukkan ID Pelanggan :");
        jPanel1.add(jdl_username2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 220, -1, -1));

        jdl_username3.setBackground(new java.awt.Color(255, 0, 0));
        jdl_username3.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jdl_username3.setForeground(new java.awt.Color(0, 0, 153));
        jdl_username3.setText("Total Biaya :");
        jPanel1.add(jdl_username3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 470, -1, -1));

        IssueBook.setBackground(new java.awt.Color(153, 153, 255));
        IssueBook.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        IssueBook.setForeground(new java.awt.Color(255, 255, 255));
        IssueBook.setText("PROSES");
        IssueBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IssueBookActionPerformed(evt);
            }
        });
        jPanel1.add(IssueBook, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 560, 250, 40));

        tgl_kembali.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tgl_kembaliMouseClicked(evt);
            }
        });
        tgl_kembali.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tgl_kembaliKeyReleased(evt);
            }
        });
        jPanel1.add(tgl_kembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 410, 240, 40));
        jPanel1.add(tgl_sewa, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 330, 240, 40));

        combo_idPelanggan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--- Pilih ---" }));
        combo_idPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_idPelangganActionPerformed(evt);
            }
        });
        jPanel1.add(combo_idPelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 250, 240, 40));

        combo_idMobil.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--- Pilih ---" }));
        combo_idMobil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_idMobilActionPerformed(evt);
            }
        });
        jPanel1.add(combo_idMobil, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 170, 240, 40));

        total_biaya.setEditable(false);
        total_biaya.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        total_biaya.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                total_biayaActionPerformed(evt);
            }
        });
        jPanel1.add(total_biaya, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 500, 240, 40));

        jdl_username6.setBackground(new java.awt.Color(255, 0, 0));
        jdl_username6.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jdl_username6.setForeground(new java.awt.Color(0, 0, 153));
        jdl_username6.setText("Tanggal Kembali :");
        jPanel1.add(jdl_username6, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 380, -1, -1));

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 0, 380, 770));

        jPanel8.setBackground(new java.awt.Color(153, 153, 255));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/mobility.png"))); // NOI18N
        jLabel8.setText("Detail Mobil");
        jPanel8.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 130, 240, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Tipe");
        jPanel8.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 400, 80, 30));

        lbl_bookError.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_bookError.setForeground(new java.awt.Color(255, 204, 51));
        jPanel8.add(lbl_bookError, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 570, -1, -1));

        txt_harga.setEditable(false);
        txt_harga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_hargaActionPerformed(evt);
            }
        });
        jPanel8.add(txt_harga, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 450, 180, 30));

        txt_merk.setEditable(false);
        txt_merk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_merkActionPerformed(evt);
            }
        });
        jPanel8.add(txt_merk, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 300, 210, 30));

        txt_tipe.setEditable(false);
        txt_tipe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_tipeActionPerformed(evt);
            }
        });
        jPanel8.add(txt_tipe, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 400, 210, 30));

        txt_idMobil.setEditable(false);
        txt_idMobil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_idMobilActionPerformed(evt);
            }
        });
        jPanel8.add(txt_idMobil, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 250, 210, 30));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Harga Sewa/Hari");
        jPanel8.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 450, -1, 30));

        jLabel7.setBackground(new java.awt.Color(102, 102, 255));
        jLabel7.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Back_40Px2.png"))); // NOI18N
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });
        jPanel8.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        jButton1.setBackground(new java.awt.Color(255, 204, 102));
        jButton1.setText("Yes");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel8.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 570, 60, -1));

        jdl_username4.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jdl_username4.setForeground(new java.awt.Color(255, 255, 255));
        jdl_username4.setText("Cek Mobil?");
        jPanel8.add(jdl_username4, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 570, 110, -1));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Merk");
        jPanel8.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 300, 80, 30));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Model");
        jPanel8.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 350, 80, 30));

        txt_model.setEditable(false);
        txt_model.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_modelActionPerformed(evt);
            }
        });
        jPanel8.add(txt_model, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 350, 210, 30));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("ID Mobil");
        jPanel8.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 250, 80, 30));

        jPanel2.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 400, 770));

        jPanel3.setBackground(new java.awt.Color(96, 123, 179));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/adminIcons/icons8_People_50px.png"))); // NOI18N
        jLabel14.setText("Detail Pelanggan");
        jPanel3.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 130, -1, -1));

        txt_idPelanggan.setEditable(false);
        txt_idPelanggan.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_idPelangganFocusLost(evt);
            }
        });
        txt_idPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_idPelangganActionPerformed(evt);
            }
        });
        jPanel3.add(txt_idPelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 250, 190, 30));

        txt_alamat.setEditable(false);
        txt_alamat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_alamatActionPerformed(evt);
            }
        });
        jPanel3.add(txt_alamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 370, 190, 30));

        lbl_studentError.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_studentError.setForeground(new java.awt.Color(255, 204, 51));
        jPanel3.add(lbl_studentError, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 570, -1, -1));

        jdl_username5.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        jdl_username5.setForeground(new java.awt.Color(255, 255, 255));
        jdl_username5.setText("Cek Pelanggan?");
        jPanel3.add(jdl_username5, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 570, 120, -1));

        jButton2.setBackground(new java.awt.Color(255, 204, 102));
        jButton2.setText("Yes");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 570, 60, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Email");
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 480, 120, 30));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("ID Pelanggan");
        jPanel3.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 250, 120, 30));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Nama");
        jPanel3.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 310, 120, 30));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Alamat");
        jPanel3.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 370, 120, 30));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("No. Telp");
        jPanel3.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 430, 120, 30));

        txt_nama.setEditable(false);
        txt_nama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_namaActionPerformed(evt);
            }
        });
        jPanel3.add(txt_nama, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 310, 190, 30));

        txt_noTelp.setEditable(false);
        txt_noTelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_noTelpActionPerformed(evt);
            }
        });
        jPanel3.add(txt_noTelp, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 430, 190, 30));

        txt_email.setEditable(false);
        txt_email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_emailActionPerformed(evt);
            }
        });
        jPanel3.add(txt_email, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 480, 190, 30));

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 0, 400, 770));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1190, 620));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void IssueBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IssueBookActionPerformed
if (combo_idMobil.getSelectedItem().toString().isEmpty() || combo_idPelanggan.getSelectedItem().toString().isEmpty()) {
    JOptionPane.showMessageDialog(this, "ID Mobil & ID Pelanggan Tidak Bisa Kosong!");
    clear();
} else {
    SewaMobil();
    updateStatusMobil();
    
}


    }//GEN-LAST:event_IssueBookActionPerformed

    private void txt_hargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_hargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_hargaActionPerformed

    private void total_biayaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_total_biayaActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_total_biayaActionPerformed

    private void txt_merkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_merkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_merkActionPerformed

    private void txt_tipeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_tipeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_tipeActionPerformed

    private void txt_idMobilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_idMobilActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_idMobilActionPerformed

    private void txt_idPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_idPelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_idPelangganActionPerformed

    private void txt_alamatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_alamatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_alamatActionPerformed

    private void txt_idPelangganFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_idPelangganFocusLost
       
        
    }//GEN-LAST:event_txt_idPelangganFocusLost

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        // TODO add your handling code here:
        HomePage home = new HomePage();
        home.setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel7MouseClicked

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        // TODO add your handling code here:
        DataMobil DataMobil = new DataMobil();
        DataMobil.setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        // TODO add your handling code here:
        DataPelanggan DataPelanggan = new DataPelanggan();
        DataPelanggan.setVisible(true);
        dispose();
                
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void combo_idMobilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_idMobilActionPerformed
        getMobil();
    }//GEN-LAST:event_combo_idMobilActionPerformed

    private void combo_idPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_idPelangganActionPerformed
        getPelanggan();
    }//GEN-LAST:event_combo_idPelangganActionPerformed

    private void txt_modelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_modelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_modelActionPerformed

    private void txt_namaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_namaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_namaActionPerformed

    private void txt_noTelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_noTelpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_noTelpActionPerformed

    private void txt_emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_emailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_emailActionPerformed

    private void tgl_kembaliKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tgl_kembaliKeyReleased
    }//GEN-LAST:event_tgl_kembaliKeyReleased

    private void tgl_kembaliMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tgl_kembaliMouseClicked

    }//GEN-LAST:event_tgl_kembaliMouseClicked



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
            java.util.logging.Logger.getLogger(Penyewaan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Penyewaan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Penyewaan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Penyewaan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Penyewaan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton IssueBook;
    private javax.swing.JComboBox<String> combo_idMobil;
    private javax.swing.JComboBox<String> combo_idPelanggan;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JLabel jdl_username;
    private javax.swing.JLabel jdl_username1;
    private javax.swing.JLabel jdl_username2;
    private javax.swing.JLabel jdl_username3;
    private javax.swing.JLabel jdl_username4;
    private javax.swing.JLabel jdl_username5;
    private javax.swing.JLabel jdl_username6;
    private javax.swing.JLabel lbl_bookError;
    private javax.swing.JLabel lbl_studentError;
    private com.toedter.calendar.JDateChooser tgl_kembali;
    private com.toedter.calendar.JDateChooser tgl_sewa;
    private javax.swing.JTextField total_biaya;
    private javax.swing.JTextField txt_alamat;
    private javax.swing.JTextField txt_email;
    private javax.swing.JTextField txt_harga;
    private javax.swing.JTextField txt_idMobil;
    private javax.swing.JTextField txt_idPelanggan;
    private javax.swing.JTextField txt_merk;
    private javax.swing.JTextField txt_model;
    private javax.swing.JTextField txt_nama;
    private javax.swing.JTextField txt_noTelp;
    private javax.swing.JTextField txt_tipe;
    // End of variables declaration//GEN-END:variables
}
