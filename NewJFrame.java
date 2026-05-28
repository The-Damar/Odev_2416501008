/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author Ali Özdamar
 */
public class NewJFrame extends javax.swing.JFrame {
    
    // Sabit Sınıf Değişkeni Dosya Yolları (8. ve 9. Maddeler)
    private static final String RESIM_YOLU = "C:\\P2Oyun\\Resimler\\";
    private static final String TXT_YOLU = "C:\\P2Oyun\\TXTDosyalar\\";

    private static final String SIFRE_DOSYASI = TXT_YOLU + "sifre.txt";
    private static final String LOG_DOSYASI = TXT_YOLU + "log.txt";
    private static final String KELIME_DOSYASI = TXT_YOLU + "kelimeler.txt";
    private static final String OYUNLAR_DOSYASI = TXT_YOLU + "oyunlar.txt";

    // Oyun İçin Gerekli Değişkenler
    private String secilenKelime = "";
    private int yanlisTahminSayisi = 0;
    private int gecenSureSaniye = 0;
    private javax.swing.Timer oyunTimer;
    private java.util.List<javax.swing.JLabel> harfLabelListesi = new java.util.ArrayList<>();
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(NewJFrame.class.getName());

    /**
     * Creates new form NewJFrame
     */
    public NewJFrame() {
        initComponents();
    }
    
    // 5. Madde: Tarih, Saat ve Etiket ile Loglama İşlemi
    private static void logYaz(String etiket) {
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(LOG_DOSYASI, true))) {
            java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String zaman = dtf.format(java.time.LocalDateTime.now());
            bw.write("[" + zaman + "] " + etiket);
            bw.newLine();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    // 7.a.i: Yeni Oyun Başlatma Mantığı (Dinamik Label Oluşturma dahil)
    public void yeniOyunBaslat() {
        java.util.List<String> kelimeler = new java.util.ArrayList<>();
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(KELIME_DOSYASI))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                if (satir.trim().length() >= 6) kelimeler.add(satir.trim().toUpperCase());
            }
        } catch (java.io.IOException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "kelimeler.txt okunamadi!");
            return;
        }

        if (kelimeler.isEmpty()) return;
        secilenKelime = kelimeler.get(new java.util.Random().nextInt(kelimeler.size()));
        yanlisTahminSayisi = 0;
        gecenSureSaniye = 0;
        lblSure.setText("Süre: 0 sn");
        resimGuncelle(1);

        // Dinamik JLabel ekleme (* görünümü)
        pnlHarfler.removeAll();
        harfLabelListesi.clear();
        for (int i = 0; i < secilenKelime.length(); i++) {
            javax.swing.JLabel lbl = new javax.swing.JLabel("*");
            lbl.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
            lbl.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, java.awt.Color.BLACK));
            pnlHarfler.add(lbl);
            harfLabelListesi.add(lbl);
        }
        pnlHarfler.revalidate();
        pnlHarfler.repaint();

        // 7.a.vi: Süre sayacını saniyelik başlatma
        if (oyunTimer == null) {
            oyunTimer = new javax.swing.Timer(1000, e -> {
                gecenSureSaniye++;
                lblSure.setText("Süre: " + gecenSureSaniye + " sn");
            });
        }
        oyunTimer.restart();
        tablolariYenile();
        this.pack();
    }

    // 7.a.iv: Resimleri sırayla ekranda açma fonksiyonu
    private void resimGuncelle(int index) {
        String tamResimYolu = RESIM_YOLU + index + ".jpg";
        if (new java.io.File(tamResimYolu).exists()) {
            javax.swing.ImageIcon icon = new javax.swing.ImageIcon(tamResimYolu);
            java.awt.Image img = icon.getImage().getScaledInstance(lblResim.getWidth() > 0 ? lblResim.getWidth() : 200, lblResim.getHeight() > 0 ? lblResim.getHeight() : 200, java.awt.Image.SCALE_SMOOTH);
            lblResim.setIcon(new javax.swing.ImageIcon(img));
        }
    }

    // 7.a.vii: Oyun bitiş verilerini kaydetme
    private void oyunBitir(boolean kazanildi) {
        oyunTimer.stop();
        String sonucStr = kazanildi ? "Kazandi" : "Kaybetti";
        javax.swing.JOptionPane.showMessageDialog(this, "Oyun Bitti: " + sonucStr);

        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(OYUNLAR_DOSYASI, true))) {
            java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            bw.write(dtf.format(java.time.LocalDateTime.now()) + "," + secilenKelime + "," + gecenSureSaniye + "," + sonucStr);
            bw.newLine();
        } catch (java.io.IOException e) { e.printStackTrace(); }

        tablolariYenile();
    }

    // Tablolara verileri basma
    private void tablolariYenile() {
        // Skor tablosunu yenileme
        javax.swing.table.DefaultTableModel modelSkor = (javax.swing.table.DefaultTableModel) tabloSkorlar.getModel();
        modelSkor.setRowCount(0);
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(OYUNLAR_DOSYASI))) {
            String s; while ((s = br.readLine()) != null) modelSkor.addRow(s.split(","));
        } catch (Exception e) {}

        // Log tablosunu yenileme
        javax.swing.table.DefaultTableModel modelLog = (javax.swing.table.DefaultTableModel) tabloLoglar.getModel();
        modelLog.setRowCount(0);
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(LOG_DOSYASI))) {
            String s; while ((s = br.readLine()) != null) modelLog.addRow(new Object[]{s});
        } catch (Exception e) {}
    }

    // Şifreli dosya temizleme (7.b.ii ve 7.c.ii maddeleri)
    private void dosyaTemizleSifreli(String dosyaYolu) {
        String girilenSifre = javax.swing.JOptionPane.showInputDialog(this, "Şifre giriniz:", "Onay", javax.swing.JOptionPane.QUESTION_MESSAGE);
        if (girilenSifre == null) return;
        try {
            String gercekSifre = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(SIFRE_DOSYASI))).trim();
            if (girilenSifre.equals(gercekSifre)) {
                new java.io.FileWriter(dosyaYolu, false).close();
                javax.swing.JOptionPane.showMessageDialog(this, "Temizlendi.");
                logYaz("Dosya temizlendi: " + dosyaYolu);
                tablolariYenile();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Hatalı şifre!");
                logYaz("Temizleme isleminde hatali sifre girildi.");
            }
        } catch (Exception e) {}
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        lblSure = new javax.swing.JLabel();
        pnlHarfler = new javax.swing.JPanel();
        lblResim = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtHarfTahmin = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtKelimeTahmin = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabloSkorlar = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabloLoglar = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();

        jMenu1.setText("Seçenekler");

        jMenuItem2.setText("Oyuna Başla / Yeniden Başlat");
        jMenuItem2.addActionListener(this::jMenuItem2ActionPerformed);
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblSure.setText("Süre: 0 sn");

        jLabel1.setText("Harf Tahmini:");

        jButton1.setText("Harf Dene");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jLabel2.setText("Kelime Tahmini:");

        txtKelimeTahmin.addActionListener(this::txtKelimeTahminActionPerformed);

        jButton2.setText("Kelime Dene");
        jButton2.addActionListener(this::jButton2ActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlHarfler, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblSure)
                .addGap(19, 19, 19))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtHarfTahmin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtKelimeTahmin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(140, 140, 140)
                        .addComponent(lblResim, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(86, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblSure)
                        .addGap(0, 88, Short.MAX_VALUE))
                    .addComponent(pnlHarfler, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblResim, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtHarfTahmin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtKelimeTahmin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addGap(5, 5, 5))
        );

        jTabbedPane1.addTab("Oyun Oyna", jPanel1);

        tabloSkorlar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Tarih / Saat", "Kelime", "Süre", "Sonuç"
            }
        ));
        jScrollPane1.setViewportView(tabloSkorlar);

        jButton3.setText("Skorları Temizle");
        jButton3.addActionListener(this::jButton3ActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(jButton3)))
                .addContainerGap(87, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addContainerGap(109, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Eski Skorlar", jPanel2);

        tabloLoglar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Log Kayıtları"
            }
        ));
        jScrollPane2.setViewportView(tabloLoglar);

        jButton4.setText("Logları Temizle");
        jButton4.addActionListener(this::jButton4ActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(117, 117, 117)
                        .addComponent(jButton4)))
                .addContainerGap(89, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addContainerGap(89, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Loglar", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(340, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtKelimeTahminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKelimeTahminActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKelimeTahminActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
dosyaTemizleSifreli(OYUNLAR_DOSYASI);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
String girdi = txtHarfTahmin.getText().toUpperCase().trim();
        txtHarfTahmin.setText("");
        if (girdi.length() != 1) return;

        char tahminHarf = girdi.charAt(0);
        boolean harfBulundu = false;
        for (int i = 0; i < secilenKelime.length(); i++) {
            if (secilenKelime.charAt(i) == tahminHarf) {
                harfLabelListesi.get(i).setText(String.valueOf(tahminHarf));
                harfBulundu = true;
            }
        }

        if (!harfBulundu) {
            yanlisTahminSayisi++;
            resimGuncelle(yanlisTahminSayisi + 1);
            if (yanlisTahminSayisi >= 11) {
                oyunBitir(false); // 11 defa yanlışta elenir
                return;
            }
        }

        boolean bitti = true;
        for (javax.swing.JLabel l : harfLabelListesi) if (l.getText().equals("*")) bitti = false;
        if (bitti) oyunBitir(true);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
String girdi = txtKelimeTahmin.getText().toUpperCase().trim();
        txtKelimeTahmin.setText("");
        if (girdi.isEmpty()) return;

        if (girdi.equals(secilenKelime)) {
            for (int i = 0; i < secilenKelime.length(); i++) harfLabelListesi.get(i).setText(String.valueOf(secilenKelime.charAt(i)));
            oyunBitir(true);
        } else {
            yanlisTahminSayisi++;
            resimGuncelle(yanlisTahminSayisi + 1);
            if (yanlisTahminSayisi >= 11) oyunBitir(false);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
dosyaTemizleSifreli(LOG_DOSYASI);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
yeniOyunBaslat();        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem2ActionPerformed

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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        /* Create and display the form */
    // --- GİRİŞ VE DOSYA KONTROLLERİ ---
    try {
        java.nio.file.Files.createDirectories(java.nio.file.Paths.get(TXT_YOLU));
        java.nio.file.Files.createDirectories(java.nio.file.Paths.get(RESIM_YOLU));
        new java.io.File(LOG_DOSYASI).createNewFile();
        new java.io.File(OYUNLAR_DOSYASI).createNewFile();
    } catch (Exception e) {}

    java.io.File sifreDosyasi = new java.io.File(SIFRE_DOSYASI);

    if (!sifreDosyasi.exists() || sifreDosyasi.length() == 0) {
        String yeniSifre = javax.swing.JOptionPane.showInputDialog(null, "Sistemde kayıtlı şifre bulunamadı.\nYeni bir giriş şifresi belirleyin:", "İlk Kurulum", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        if (yeniSifre == null || yeniSifre.trim().isEmpty()) {
            System.exit(0);
        }
        try (java.io.FileWriter fw = new java.io.FileWriter(sifreDosyasi)) {
            fw.write(yeniSifre.trim());
            logYaz("Ilk sifre olusturuldu.");
            javax.swing.JOptionPane.showMessageDialog(null, "Şifre başarıyla kaydedildi! Şimdi giriş yapabilirsiniz.");
        } catch (Exception e) {
            System.exit(0);
        }
    }

    int hak = 3;
    boolean girisBasarili = false;
    try {
        String kayitliSifre = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(SIFRE_DOSYASI))).trim();
        while (hak > 0) {
            String girisSifresi = javax.swing.JOptionPane.showInputDialog(null, "Lütfen Giriş Şifresini Yazınız (Kalan Hak: " + hak + "):", "Giriş Paneli", javax.swing.JOptionPane.QUESTION_MESSAGE);
            
            if (girisSifresi == null) {
                logYaz("Kullanici giris yapmadan pencereyi kapatti.");
                System.exit(0);
            }

            logYaz("Sifre denemesi yapildi.");

            if (girisSifresi.trim().equals(kayitliSifre)) {
                girisBasarili = true;
                logYaz("Giris Basarili.");
                break;
            } else {
                hak--;
                javax.swing.JOptionPane.showMessageDialog(null, "Hatalı şifre girdiniz!", "Hata", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }

        if (!girisBasarili) {
            logYaz("3 defa hatali sifre girildi, program sonlandirildi.");
            javax.swing.JOptionPane.showMessageDialog(null, "3 defa hatalı şifre girdiniz. Program açılmayacak.");
            System.exit(0);
        }

    } catch (Exception e) {
        System.exit(0);
    }

    /* Giriş başarılı ise ekranı aç ve oyunu tetikle */
    java.awt.EventQueue.invokeLater(() -> {
        NewJFrame anaEkran = new NewJFrame();
        anaEkran.setVisible(true);
        anaEkran.yeniOyunBaslat();
    });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblResim;
    private javax.swing.JLabel lblSure;
    private javax.swing.JPanel pnlHarfler;
    private javax.swing.JTable tabloLoglar;
    private javax.swing.JTable tabloSkorlar;
    private javax.swing.JTextField txtHarfTahmin;
    private javax.swing.JTextField txtKelimeTahmin;
    // End of variables declaration//GEN-END:variables
}
