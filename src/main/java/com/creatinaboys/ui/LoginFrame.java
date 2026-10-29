package com.creatinaboys.ui;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.service.IAuthService;
import com.creatinaboys.ui.components.ClubLogo;
import com.creatinaboys.ui.components.InputBox;
import com.creatinaboys.ui.components.ModernButton;
import com.creatinaboys.ui.components.PlaceholderPasswordField;
import com.creatinaboys.ui.components.PlaceholderTextField;
import com.creatinaboys.ui.components.VectorIcon;
import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Pantalla de inicio de sesión (Mockup 1). Al autenticar correctamente se cierra
 * y entrega el nombre de usuario a {@code onSuccess}.
 */
public class LoginFrame extends JFrame {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_SECONDS = 15;

    private final IAuthService authService;
    private final Consumer<String> onSuccess;

    private final PlaceholderTextField txtUser = new PlaceholderTextField("Usuario");
    private final PlaceholderPasswordField txtPassword = new PlaceholderPasswordField("Contraseña");
    private final JLabel lblError = new JLabel(" ");
    private final ModernButton btnLogin = new ModernButton("Ingresar", null, ModernButton.Style.PRIMARY, 320);

    private int failedAttempts = 0;
    private Timer lockTimer;

    public LoginFrame(IAuthService authService, Consumer<String> onSuccess) {
        this.authService = authService;
        this.onSuccess = onSuccess;

        setTitle("Creatina-Boys");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(880, 540);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.add(buildBrandSide());
        root.add(buildFormSide());
        setContentPane(root);

        getRootPane().setDefaultButton(btnLogin);   // Enter = Ingresar
        btnLogin.addActionListener(e -> attemptLogin());
    }

    private JPanel buildBrandSide() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, ThemeConstants.PRIMARY_LIGHT, 0, getHeight(), Color.WHITE));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(ThemeConstants.BORDER_COLOR);
                g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
                g2.dispose();
            }
        };
        p.setBorder(new EmptyBorder(30, 20, 28, 20));

        JPanel logoWrap = new JPanel(new GridBagLayout());
        logoWrap.setOpaque(false);
        logoWrap.add(new ClubLogo());
        p.add(logoWrap, BorderLayout.CENTER);

        JLabel tagline = new JLabel("Formación  •  Disciplina  •  Grandes Logros", SwingConstants.CENTER);
        tagline.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tagline.setForeground(new Color(30, 58, 110));
        p.add(tagline, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildFormSide() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(30, 44, 30, 44));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;

        JLabel title = new JLabel("Iniciar Sesión");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(30, 58, 110));
        c.gridy = 0;
        c.insets = new Insets(0, 0, 22, 0);
        p.add(title, c);

        c.gridy = 1;
        c.insets = new Insets(0, 0, 14, 0);
        p.add(new InputBox(VectorIcon.Type.USER, txtUser), c);

        c.gridy = 2;
        c.insets = new Insets(0, 0, 6, 0);
        p.add(new InputBox(VectorIcon.Type.LOCK, txtPassword), c);

        lblError.setFont(ThemeConstants.FONT_SMALL);
        lblError.setForeground(ThemeConstants.DANGER_RED);
        c.gridy = 3;
        c.insets = new Insets(0, 4, 10, 0);
        p.add(lblError, c);

        c.gridy = 4;
        c.insets = new Insets(0, 0, 0, 0);
        p.add(btnLogin, c);
        return p;
    }

    private void attemptLogin() {
        String user = txtUser.getText().trim();
        char[] password = txtPassword.getPassword();

        if (user.isEmpty() || password.length == 0) {
            Arrays.fill(password, '\0');
            showError("Ingrese su usuario y contraseña.");
            (user.isEmpty() ? txtUser : txtPassword).requestFocusInWindow();
            return;
        }

        setBusy(true);
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    return authService.authenticate(user, password);   // PBKDF2: fuera del hilo de la UI
                } finally {
                    Arrays.fill(password, '\0');
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        dispose();
                        onSuccess.accept(user);
                        return;
                    }
                    onFailedAttempt();
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    showError(cause instanceof PersistenceException
                            ? "No se pudo leer el archivo de usuarios."
                            : "No se pudo iniciar sesión. Intente de nuevo.");
                    setBusy(false);
                }
            }
        }.execute();
    }

    private void onFailedAttempt() {
        txtPassword.setText("");
        failedAttempts++;
        if (failedAttempts >= MAX_ATTEMPTS) {
            startLock();
            return;
        }
        showError("Usuario o contraseña incorrectos.");
        setBusy(false);
        txtPassword.requestFocusInWindow();
    }

    /** Bloqueo breve tras demasiados intentos fallidos. */
    private void startLock() {
        final int[] left = {LOCK_SECONDS};
        btnLogin.setEnabled(false);
        txtUser.setEnabled(false);
        txtPassword.setEnabled(false);
        showError("Demasiados intentos. Espere " + left[0] + " s.");
        lockTimer = new Timer(1000, e -> {
            left[0]--;
            if (left[0] <= 0) {
                lockTimer.stop();
                failedAttempts = 0;
                lblError.setText(" ");
                setBusy(false);
                txtPassword.requestFocusInWindow();
            } else {
                showError("Demasiados intentos. Espere " + left[0] + " s.");
            }
        });
        lockTimer.start();
    }

    private void setBusy(boolean busy) {
        btnLogin.setEnabled(!busy);
        txtUser.setEnabled(!busy);
        txtPassword.setEnabled(!busy);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    private void showError(String message) {
        lblError.setText(message);
    }
}