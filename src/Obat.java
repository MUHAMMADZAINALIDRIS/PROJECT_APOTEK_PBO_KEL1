/*
 * Obat.java - Redesign UI Modern Apotek
 * Fitur: Placeholder, animasi button (ripple), gradient header,
 *        card panel shadow, focus border, tabel striped modern
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class Obat extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(Obat.class.getName());

    // ═══════════════════════════════════════════════
    // PALET WARNA MODERN APOTEK
    // ═══════════════════════════════════════════════
    private static final Color C_PRIMARY       = new Color(22, 101, 52);
    private static final Color C_TAMBAH        = new Color(21, 128, 61);
    private static final Color C_TAMBAH_HOVER  = new Color(22, 163, 74);
    private static final Color C_EDIT          = new Color(29, 78, 216);
    private static final Color C_EDIT_HOVER    = new Color(37, 99, 235);
    private static final Color C_HAPUS         = new Color(185, 28, 28);
    private static final Color C_HAPUS_HOVER   = new Color(220, 38, 38);
    private static final Color C_RESET         = new Color(100, 116, 139);
    private static final Color C_RESET_HOVER   = new Color(71, 85, 105);
    private static final Color C_BG            = new Color(240, 253, 244);
    private static final Color C_TEXT_DARK     = new Color(15, 23, 42);
    private static final Color C_ROW_ODD       = new Color(240, 253, 244);
    private static final Color C_ROW_SEL       = new Color(187, 247, 208);
    private static final Color C_ROW_SEL_FG    = new Color(5, 46, 22);
    private static final Color C_HEADER_TBL    = new Color(22, 101, 52);

    DefaultTableModel model;

    // ═══════════════════════════════════════════════
    // PLACEHOLDER TEXT FIELD
    // ═══════════════════════════════════════════════
    private static class PlaceholderTextField extends JTextField {
        private final String placeholder;
        private boolean showingPlaceholder = true;

        PlaceholderTextField(String ph) {
            this.placeholder = ph;
            setForeground(new Color(148, 163, 184));
            setText(ph);

            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) {
                    if (showingPlaceholder) {
                        setText("");
                        setForeground(new Color(15, 23, 42));
                        showingPlaceholder = false;
                    }
                }
                @Override public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) {
                        setText(placeholder);
                        setForeground(new Color(148, 163, 184));
                        showingPlaceholder = true;
                    }
                }
            });
        }

        public String getRealText() {
            return showingPlaceholder ? "" : getText();
        }

        public void clearField() {
            setText(placeholder);
            setForeground(new Color(148, 163, 184));
            showingPlaceholder = true;
        }

        public void setRealText(String t) {
            showingPlaceholder = false;
            setText(t);
            setForeground(new Color(15, 23, 42));
        }
    }

    // ═══════════════════════════════════════════════
    // ANIMATED BUTTON (Ripple + Hover + Press)
    // ═══════════════════════════════════════════════
    private static class AnimatedButton extends JButton {
        private final Color baseColor, hoverColor;
        private boolean hovered = false, pressed = false;
        private float rippleRadius = 0, rippleAlpha = 0f;
        private int rippleX = 0, rippleY = 0;
        private Timer rippleTimer;

        AnimatedButton(String text, Color base, Color hover) {
            super(text);
            this.baseColor = base;
            this.hoverColor = hover;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setOpaque(false);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hovered = false; pressed = false; repaint(); }
                @Override public void mousePressed(MouseEvent e) {
                    pressed = true;
                    startRipple(e.getX(), e.getY());
                    repaint();
                }
                @Override public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
            });
        }

        private void startRipple(int x, int y) {
            rippleX = x; rippleY = y; rippleRadius = 0; rippleAlpha = 0.45f;
            if (rippleTimer != null && rippleTimer.isRunning()) rippleTimer.stop();
            rippleTimer = new Timer(16, e -> {
                rippleRadius += 9;
                rippleAlpha -= 0.025f;
                if (rippleAlpha <= 0) { rippleAlpha = 0; ((Timer)e.getSource()).stop(); }
                repaint();
            });
            rippleTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int arc = 12, w = getWidth(), h = getHeight();

            if (pressed) {
                g2.translate(w/2.0, h/2.0);
                g2.scale(0.94, 0.94);
                g2.translate(-w/2.0, -h/2.0);
            }

            Color top = hovered ? hoverColor : baseColor;
            g2.setPaint(new GradientPaint(0, 0, top.brighter(), 0, h, top));
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, arc, arc));

            if (!pressed) {
                g2.setColor(new Color(0,0,0,25));
                g2.fill(new RoundRectangle2D.Float(2, h-4, w-4, 6, arc, arc));
            }

            if (rippleAlpha > 0) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, rippleAlpha));
                g2.setColor(Color.WHITE);
                g2.fillOval((int)(rippleX-rippleRadius), (int)(rippleY-rippleRadius),
                            (int)(rippleRadius*2), (int)(rippleRadius*2));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }

            FontMetrics fm = g2.getFontMetrics(getFont());
            g2.setFont(getFont()); g2.setColor(getForeground());
            g2.drawString(getText(), (w-fm.stringWidth(getText()))/2,
                          (h-fm.getHeight())/2+fm.getAscent());
            g2.dispose();
        }
    }

    // ═══════════════════════════════════════════════
    // CARD PANEL (rounded + shadow)
    // ═══════════════════════════════════════════════
    private static class CardPanel extends JPanel {
        CardPanel() { setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int r = 16, w = getWidth()-8, h = getHeight()-8;
            for (int i = 6; i > 0; i--)
                { g2.setColor(new Color(0,0,0,5)); g2.fill(new RoundRectangle2D.Float(i,i+2,w-i,h-i,r,r)); }
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0,0,w,h,r,r));
            g2.setColor(new Color(187,247,208));
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(0,0,w,h,r,r));
            g2.dispose();
        }
    }

    // ═══════════════════════════════════════════════
    // HEADER PANEL (gradient hijau)
    // ═══════════════════════════════════════════════
    private static class HeaderPanel extends JPanel {
        HeaderPanel() { setOpaque(false); setPreferredSize(new Dimension(0,90)); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0,0,new Color(5,46,22),getWidth(),getHeight(),new Color(21,128,61)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(new Color(255,255,255,18));
            g2.fillOval(-30,-30,160,160);
            g2.fillOval(getWidth()-100,-20,200,200);
            g2.dispose();
        }
    }

    // ═══════════════════════════════════════════════
    // ROUND BORDER
    // ═══════════════════════════════════════════════
    private static class RoundBorder extends AbstractBorder {
        private final Color color; private final int thickness, radius;
        RoundBorder(Color c, int t, int r) { color=c; thickness=t; radius=r; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x,y,w-1,h-1,radius,radius); g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(thickness+2,thickness+2,thickness+2,thickness+2); }
    }

    // ═══════════════════════════════════════════════
    // STYLE FIELD
    // ═══════════════════════════════════════════════
    private void styleField(PlaceholderTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(Color.WHITE);
        tf.setPreferredSize(new Dimension(210, 36));
        Border normal = BorderFactory.createCompoundBorder(
            new RoundBorder(new Color(187,247,208),1,8), BorderFactory.createEmptyBorder(6,12,6,12));
        Border focus  = BorderFactory.createCompoundBorder(
            new RoundBorder(new Color(22,163,74),2,8), BorderFactory.createEmptyBorder(6,12,6,12));
        tf.setBorder(normal);
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { tf.setBorder(focus); }
            @Override public void focusLost(FocusEvent e)   { tf.setBorder(normal); }
        });
    }

    // ═══════════════════════════════════════════════
    // KOMPONEN
    // ═══════════════════════════════════════════════
    private PlaceholderTextField kode_obat, nama_obat;
    private JComboBox<String> cb_kategori, cb_satuan;
    private AnimatedButton btntambah, btnedit, btnhapus, btnreset;
    private JTable tabel;
    private JScrollPane jScrollPane1;

    public Obat() {
        setTitle("Sistem Apotek - Data Obat");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 580));

        kode_obat   = new PlaceholderTextField("Contoh: OBT001");
        nama_obat   = new PlaceholderTextField("Masukkan Nama Obat");
        cb_kategori = new JComboBox<>();
        cb_satuan   = new JComboBox<>();

        btntambah = new AnimatedButton("Tambah", C_TAMBAH, C_TAMBAH_HOVER);
        btnedit   = new AnimatedButton("Edit",   C_EDIT,   C_EDIT_HOVER);
        btnhapus  = new AnimatedButton("Hapus",  C_HAPUS,  C_HAPUS_HOVER);
        btnreset  = new AnimatedButton("Reset",  C_RESET,  C_RESET_HOVER);

        styleField(kode_obat);
        styleField(nama_obat);
        styleComboBox(cb_kategori);
        styleComboBox(cb_satuan);

        Dimension btnSz = new Dimension(130,38);
        for (AnimatedButton b : new AnimatedButton[]{btntambah,btnedit,btnhapus,btnreset})
            b.setPreferredSize(btnSz);

        tabel = new JTable();
        jScrollPane1 = new JScrollPane(tabel);

        String[] cols = {"Kode Obat","Nama Obat","Kategori","Satuan"};
        model = new DefaultTableModel(cols,0) {
            @Override public boolean isCellEditable(int r,int c) { return false; }
        };
        tabel.setModel(model);
        tabel.setFont(new Font("Segoe UI",Font.PLAIN,13));
        tabel.setRowHeight(32);
        tabel.setGridColor(new Color(220,252,231));
        tabel.setSelectionBackground(C_ROW_SEL);
        tabel.setSelectionForeground(C_ROW_SEL_FG);
        tabel.setShowVerticalLines(false);
        tabel.setIntercellSpacing(new Dimension(0,1));
        tabel.setBackground(Color.WHITE);

        JTableHeader th = tabel.getTableHeader();
        th.setBackground(C_HEADER_TBL);
        th.setForeground(Color.WHITE);
        th.setFont(new Font("Segoe UI",Font.BOLD,13));
        th.setReorderingAllowed(false);
        th.setPreferredSize(new Dimension(0,36));
        th = tabel.getTableHeader();
        th.setBackground(new Color(22, 101, 52));
        th.setForeground(Color.WHITE);
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setReorderingAllowed(false);
        th.setPreferredSize(new Dimension(0, 36));
        th.setOpaque(true);
        th.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                setText(v == null ? "" : v.toString());
                setBackground(new Color(22, 101, 52));
                setForeground(Color.WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setHorizontalAlignment(JLabel.CENTER);
                setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(134, 239, 172)));
                setOpaque(true);
                return this;
            }
        });

        tabel.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                setHorizontalAlignment(CENTER);
                setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
                if (!sel) { setBackground(row%2==0 ? Color.WHITE : C_ROW_ODD); setForeground(C_TEXT_DARK); }
                return this;
            }
        });

        jScrollPane1.setBorder(BorderFactory.createLineBorder(new Color(187,247,208),1));
        jScrollPane1.getViewport().setBackground(Color.WHITE);

        tabel.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = tabel.getSelectedRow(); if (row<0) return;
                kode_obat.setRealText(model.getValueAt(row,0).toString());
                nama_obat.setRealText(model.getValueAt(row,1).toString());
                selectComboByValue(cb_kategori, model.getValueAt(row,2).toString());
                selectComboByValue(cb_satuan,   model.getValueAt(row,3).toString());
                kode_obat.setEditable(false);
            }
        });

        btntambah.addActionListener(e -> btntambahAction());
        btnedit.addActionListener(e -> btneditAction());
        btnhapus.addActionListener(e -> btnhapusAction());
        btnreset.addActionListener(e -> resetForm());

        loadKategori();
        loadSatuan();
        buildUI();
        tampilkan();
        pack();
        setLocationRelativeTo(null);
    }


    private void styleComboBox(JComboBox<String> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
        cb.setForeground(new Color(15, 23, 42));
        cb.setPreferredSize(new Dimension(210, 36));
        Border normalCb = BorderFactory.createCompoundBorder(
            new RoundBorder(new Color(187,247,208),1,8), BorderFactory.createEmptyBorder(2,8,2,8));
        Border focusCb  = BorderFactory.createCompoundBorder(
            new RoundBorder(new Color(22,163,74),2,8), BorderFactory.createEmptyBorder(2,8,2,8));
        cb.setBorder(normalCb);
        cb.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { cb.setBorder(focusCb); }
            @Override public void focusLost(FocusEvent e)   { cb.setBorder(normalCb); }
        });
    }

    private void loadKategori() {
        cb_kategori.removeAllItems();
        cb_kategori.addItem("-- Pilih Kategori --");
        try (Connection cn=getConnection();
             ResultSet rs=cn.createStatement().executeQuery("SELECT id_kategori, nama_kategori FROM kategori ORDER BY nama_kategori")) {
            while (rs.next())
                cb_kategori.addItem(rs.getString("id_kategori")+" - "+rs.getString("nama_kategori"));
        } catch (SQLException ex) { logger.log(java.util.logging.Level.SEVERE,null,ex); }
    }

    private void loadSatuan() {
        cb_satuan.removeAllItems();
        cb_satuan.addItem("-- Pilih Satuan --");
        try (Connection cn=getConnection();
             ResultSet rs=cn.createStatement().executeQuery("SELECT kode_satuan, nama_satuan FROM satuan ORDER BY nama_satuan")) {
            while (rs.next())
                cb_satuan.addItem(rs.getString("kode_satuan")+" - "+rs.getString("nama_satuan"));
        } catch (SQLException ex) { logger.log(java.util.logging.Level.SEVERE,null,ex); }
    }

    private void selectComboByValue(JComboBox<String> cb, String value) {
        for (int i=0; i<cb.getItemCount(); i++) {
            if (cb.getItemAt(i).contains(value)) { cb.setSelectedIndex(i); return; }
        }
    }

    private String getKodeFromCombo(JComboBox<String> cb) {
        String sel = (String) cb.getSelectedItem();
        if (sel==null || sel.startsWith("--")) return "";
        return sel.split(" - ")[0].trim();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_BG);
        setContentPane(root);

        // Header
        HeaderPanel header = new HeaderPanel();
        header.setLayout(new GridBagLayout());
        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.CENTER,10,0));
        titleBox.setOpaque(false);

        JLabel iconLbl = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(134,239,172));
                g2.setStroke(new BasicStroke(4,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                g2.drawLine(16,8,16,24); g2.drawLine(8,16,24,16);
                g2.setStroke(new BasicStroke(2.5f)); g2.drawOval(4,4,24,24);
                g2.dispose();
            }
        };
        iconLbl.setPreferredSize(new Dimension(32,32));

        JLabel titleLbl = new JLabel("DATA OBAT");
        titleLbl.setFont(new Font("Segoe UI",Font.BOLD,26));
        titleLbl.setForeground(Color.WHITE);

        JLabel subLbl = new JLabel("Sistem Manajemen Apotek");
        subLbl.setFont(new Font("Segoe UI",Font.PLAIN,13));
        subLbl.setForeground(new Color(134,239,172));

        JPanel titleStack = new JPanel();
        titleStack.setOpaque(false);
        titleStack.setLayout(new BoxLayout(titleStack,BoxLayout.Y_AXIS));
        titleLbl.setAlignmentX(CENTER_ALIGNMENT);
        subLbl.setAlignmentX(CENTER_ALIGNMENT);
        titleStack.add(titleLbl); titleStack.add(subLbl);
        titleBox.add(iconLbl); titleBox.add(titleStack);
        header.add(titleBox);
        root.add(header, BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel(new BorderLayout(12,12));
        body.setBackground(C_BG);
        body.setBorder(BorderFactory.createEmptyBorder(16,20,16,20));
        root.add(body, BorderLayout.CENTER);

        // Form card
        CardPanel card = new CardPanel();
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20,28,20,28));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8,8,8,8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;

        Font lf = new Font("Segoe UI",Font.BOLD,13);
        Color lc = C_PRIMARY;

        JLabel l3=new JLabel("Kode Obat"); l3.setFont(lf); l3.setForeground(lc);
        JLabel l4=new JLabel("Nama Obat"); l4.setFont(lf); l4.setForeground(lc);
        JLabel l5=new JLabel("Kategori");    l5.setFont(lf); l5.setForeground(lc);
        JLabel l6=new JLabel("Satuan");      l6.setFont(lf); l6.setForeground(lc);

        gc.gridx=0; gc.gridy=0; gc.weightx=0; card.add(l3,gc);
        gc.gridx=1; gc.weightx=1; card.add(kode_obat,gc);
        gc.gridx=2; gc.weightx=0; card.add(l4,gc);
        gc.gridx=3; gc.weightx=1; card.add(nama_obat,gc);

        gc.gridx=0; gc.gridy=1; gc.weightx=0; card.add(l5,gc);
        gc.gridx=1; gc.weightx=1; card.add(cb_kategori,gc);
        gc.gridx=2; gc.weightx=0; card.add(l6,gc);
        gc.gridx=3; gc.weightx=1; card.add(cb_satuan,gc);

        JPanel btnPnl = new JPanel(new FlowLayout(FlowLayout.CENTER,12,0));
        btnPnl.setOpaque(false);
        btnPnl.add(btntambah); btnPnl.add(btnedit); btnPnl.add(btnhapus); btnPnl.add(btnreset);

        gc.gridx=0; gc.gridy=2; gc.gridwidth=4; gc.weightx=1;
        gc.fill=GridBagConstraints.NONE; gc.anchor=GridBagConstraints.CENTER;
        gc.insets=new Insets(16,8,4,8);
        card.add(btnPnl,gc);
        body.add(card, BorderLayout.NORTH);

        // Table card
        CardPanel tcard = new CardPanel();
        tcard.setLayout(new BorderLayout());
        tcard.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JLabel tlbl = new JLabel("  Daftar Obat");
        tlbl.setFont(new Font("Segoe UI",Font.BOLD,14));
        tlbl.setForeground(C_PRIMARY);
        tlbl.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));
        tcard.add(tlbl, BorderLayout.NORTH);
        jScrollPane1.setPreferredSize(new Dimension(0,210));
        tcard.add(jScrollPane1, BorderLayout.CENTER);
        body.add(tcard, BorderLayout.CENTER);
    }

    // ═══════════════════════════════════════════════
    // DB CRUD
    // ═══════════════════════════════════════════════
    private boolean inputValid() {
        if (kode_obat.getRealText().isEmpty() || nama_obat.getRealText().isEmpty()) {
            JOptionPane.showMessageDialog(this,"Kode dan nama obat harus diisi!","Peringatan",JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (getKodeFromCombo(cb_kategori).isEmpty()) {
            JOptionPane.showMessageDialog(this,"Pilih kategori terlebih dahulu!","Peringatan",JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (getKodeFromCombo(cb_satuan).isEmpty()) {
            JOptionPane.showMessageDialog(this,"Pilih satuan terlebih dahulu!","Peringatan",JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void tampilkan() {
        model.setRowCount(0);
        try (Connection cn=getConnection();
             ResultSet rs=cn.createStatement().executeQuery(
                 "SELECT o.kode_obat, o.nama_obat, k.nama_kategori, s.nama_satuan " +
                 "FROM obat o " +
                 "JOIN kategori k ON o.id_kategori = k.id_kategori " +
                 "JOIN satuan s ON o.kode_satuan = s.kode_satuan")) {
            while (rs.next())
                model.addRow(new String[]{
                    rs.getString("kode_obat"), rs.getString("nama_obat"),
                    rs.getString("nama_kategori"), rs.getString("nama_satuan")});
        } catch (SQLException ex) { logger.log(java.util.logging.Level.SEVERE,null,ex); }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost/apotek","root","");
    }

    private void btntambahAction() {
        if (!inputValid()) return;
        try (Connection cn=getConnection();
             PreparedStatement ps=cn.prepareStatement(
                 "INSERT INTO obat (kode_obat,nama_obat,id_kategori,kode_satuan) VALUES(?,?,?,?)")) {
            ps.setString(1,kode_obat.getRealText()); ps.setString(2,nama_obat.getRealText());
            ps.setString(3,getKodeFromCombo(cb_kategori)); ps.setString(4,getKodeFromCombo(cb_satuan));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"Data berhasil ditambahkan!","Sukses",JOptionPane.INFORMATION_MESSAGE);
            tampilkan(); resetForm();
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE,null,ex);
            JOptionPane.showMessageDialog(this,"Gagal tambah: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btneditAction() {
        if (tabel.getSelectedRow()==-1) {
            JOptionPane.showMessageDialog(this,"Pilih data dari tabel!","Peringatan",JOptionPane.WARNING_MESSAGE); return;
        }
        if (!inputValid()) return;
        try (Connection cn=getConnection();
             PreparedStatement ps=cn.prepareStatement(
                 "UPDATE obat SET nama_obat=?,id_kategori=?,kode_satuan=? WHERE kode_obat=?")) {
            ps.setString(1,nama_obat.getRealText()); ps.setString(2,getKodeFromCombo(cb_kategori));
            ps.setString(3,getKodeFromCombo(cb_satuan)); ps.setString(4,kode_obat.getRealText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"Data berhasil diupdate!","Sukses",JOptionPane.INFORMATION_MESSAGE);
            tampilkan(); resetForm();
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE,null,ex);
            JOptionPane.showMessageDialog(this,"Gagal update: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnhapusAction() {
        if (tabel.getSelectedRow()==-1) {
            JOptionPane.showMessageDialog(this,"Pilih data dari tabel!","Peringatan",JOptionPane.WARNING_MESSAGE); return;
        }
        int ok=JOptionPane.showConfirmDialog(this,
            "Yakin ingin menghapus \""+kode_obat.getRealText()+"\"?","Konfirmasi Hapus",JOptionPane.YES_NO_OPTION);
        if (ok!=JOptionPane.YES_OPTION) return;
        try (Connection cn=getConnection();
             PreparedStatement ps=cn.prepareStatement("DELETE FROM obat WHERE kode_obat=?")) {
            ps.setString(1,kode_obat.getRealText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"Data berhasil dihapus!","Sukses",JOptionPane.INFORMATION_MESSAGE);
            tampilkan(); resetForm();
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE,null,ex);
            JOptionPane.showMessageDialog(this,"Gagal hapus: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        kode_obat.clearField(); nama_obat.clearField();
        cb_kategori.setSelectedIndex(0); cb_satuan.setSelectedIndex(0);
        kode_obat.setEditable(true);
        tabel.clearSelection();
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        java.awt.EventQueue.invokeLater(() -> new Obat().setVisible(true));
    }
}