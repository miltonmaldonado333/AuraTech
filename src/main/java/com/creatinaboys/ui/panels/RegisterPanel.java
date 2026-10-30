package com.creatinaboys.ui;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.exceptions.ValidationException;
import com.creatinaboys.service.IAuthService;
import com.creatinaboys.ui.components.InputBox;
import com.creatinaboys.ui.components.LinkLabel;
import com.creatinaboys.ui.components.ModernButton;
import com.creatinaboys.ui.components.PlaceholderPasswordField;
import com.creatinaboys.ui.components.PlaceholderTextField;
import com.creatinaboys.ui.components.VectorIcon;
import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Formulario de registro de usuarios. Pide la contraseña dos veces y solo crea la cuenta
 * si ambas coinciden. Se muestra dentro de {@link LoginFrame}.
 */
public class RegisterPanel extends JPanel {

    private final IAuthService authService;
    private final Consumer<String> onRegistered;
    private final Runnable onBack;

    private final PlaceholderTextField txtUser = new PlaceholderTextField("Nuevo usuario");
    private final PlaceholderPasswordField txtPassword = new PlaceholderPasswordField("Contraseña");
    private final PlaceholderPasswordField txtConfirm = new PlaceholderPasswordField("Repetir contraseña");
    private final JLabel lblError = new JLabel(" ");
    private final ModernButton btnRegister = new ModernButton("Crear cuenta", null, ModernButton.Style.PRIMARY, 320);

    /**
     * @param onRegistered recibe el nombre de usuario recién creado.
     * @param onBack       se invoca al pulsar «Volver a iniciar sesión».
     */
    public RegisterPanel(IAuthService authService, Consumer<String> onRegistered, Runnable onBack) {
        this.authService = authService;
        this.onRegistered = onRegistered;
        this.onBack = onBack;

        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 44, 30, 44));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;

        JLabel title = new JLabel("Crear Cuenta");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(30, 58, 110));
        c.gridy = 0;
        c.insets = new Insets(0, 0, 20, 0);
        add(title, c);

        c.gridy = 1;
        c.insets = new Insets(0, 0, 12, 0);
        add(new InputBox(VectorIcon.Type.USER, txtUser), c);

        c.gridy = 2;
        add(new InputBox(VectorIcon.Type.LOCK, txtPassword), c);

        c.gridy = 3;
        c.insets = new Insets(0, 0, 6, 0);
        add(new InputBox(VectorIcon.Type.LOCK, txtConfirm), c);

        lblError.setFont(ThemeConstants.FONT_SMALL);
        lblError.setForeground(ThemeConstants.DANGER_RED);
        c.gridy = 4;
        c.insets = new Insets(0, 4, 10, 0);
        add(lblError, c);

        c.gridy = 5;
        c.insets = new Insets(0, 0, 14, 0);
        add(btnRegister, c);

        c.gridy = 6;
        c.insets = new Insets(0, 0, 0, 0);
        add(new LinkLabel("Volver a iniciar sesión", onBack), c);

        btnRegister.addActionListener(e -> attemptRegister());
    }

    /** Botón que debe responder a Enter mientras este formulario está visible. */
    public JButton getDefaultButton() {
        return btnRegister;
    }

    /** Deja el formulario limpio y con el foco en el usuario. */
    public void reset() {
        txtUser.setText("");
        txtPassword.setText("");
        txtConfirm.setText("");
        lblError.setText(" ");
        setBusy(false);
        SwingUtilities.invokeLater(txtUser::requestFocusInWindow);
    }

    private void attemptRegister() {
        String user = txtUser.getText().trim();
        char[] password = txtPassword.getPassword();
        char[] confirm = txtConfirm.getPassword();

        setBusy(true);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                authService.register(user, password, confirm);   // PBKDF2: fuera del hilo de la UI
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    onRegistered.accept(user);
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    if (cause instanceof ValidationException) {
                        lblError.setText(cause.getMessage());
                    } else if (cause instanceof PersistenceException) {
                        lblError.setText("No se pudo guardar el usuario.");
                    } else {
                        lblError.setText("No se pudo crear la cuenta. Intente de nuevo.");
                    }
                    txtPassword.setText("");
                    txtConfirm.setText("");
                    setBusy(false);
                    txtPassword.requestFocusInWindow();
                }
            }
        }.execute();
    }

    private void setBusy(boolean busy) {
        btnRegister.setEnabled(!busy);
        txtUser.setEnabled(!busy);
        txtPassword.setEnabled(!busy);
        txtConfirm.setEnabled(!busy);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }
}