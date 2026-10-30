package com.creatinaboys;

import com.creatinaboys.repository.FileAthleteRepository;
import com.creatinaboys.repository.FileCoachRepository;
import com.creatinaboys.repository.FilePaymentRepository;
import com.creatinaboys.repository.Fileuserrepository;
import com.creatinaboys.repository.IAthleteRepository;
import com.creatinaboys.repository.ICoachRepository;
import com.creatinaboys.repository.IPaymentRepository;
import com.creatinaboys.service.AthleteService;
import com.creatinaboys.service.Authservice;
import com.creatinaboys.service.BillingService;
import com.creatinaboys.service.CoachService;
import com.creatinaboys.service.DisciplineService;
import com.creatinaboys.service.IAuthService;
import com.creatinaboys.service.IBillingService;
import com.creatinaboys.service.ICoachService;
import com.creatinaboys.service.IDisciplineService;
import com.creatinaboys.service.ISaleService;
import com.creatinaboys.service.PaymentDisciplineMigration;
import com.creatinaboys.service.SaleService;
import com.creatinaboys.ui.LoginFrame;
import com.creatinaboys.ui.PrincipalFrames;

import javax.swing.*;
import java.util.function.Supplier;

/**
 * Punto de entrada principal de la aplicación Creatina-Boys.
 * 
 * @author Milton Andrés Maldonado Salcedo
 */
public class Main {

    public static void main(String[] args) {
        // Ajuste de apariencia moderna (System L&F)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Arranque en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Instanciar repositorios y servicios (DIP / Inyección)
            IAthleteRepository repository = new FileAthleteRepository();
            IPaymentRepository paymentRepository = new FilePaymentRepository();
            IBillingService billingService = new BillingService();
            IDisciplineService disciplineService = new DisciplineService();

            // Asigna la disciplina a los pagos antiguos (idempotente: solo escribe si algo cambió)
            try {
                new PaymentDisciplineMigration(paymentRepository, repository).run();
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo actualizar el historial de pagos:\n" + e.getMessage(),
                        "Creatina-Boys", JOptionPane.WARNING_MESSAGE);
            }

            AthleteService athleteService = new AthleteService(repository, billingService);
            ISaleService saleService = new SaleService(repository, billingService, paymentRepository);

            ICoachRepository coachRepository = new FileCoachRepository();
            ICoachService coachService = new CoachService(coachRepository);

            // Autenticación: crea el usuario inicial la primera vez
            IAuthService authService = new Authservice(new Fileuserrepository());
            try {
                authService.ensureDefaultUser();
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(null, "No se pudo preparar el archivo de usuarios:\n" + e.getMessage(),
                        "Creatina-Boys", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Cada inicio de sesión abre una ventana principal nueva con los mismos servicios
            Supplier<PrincipalFrames> frameFactory = () -> new PrincipalFrames(athleteService, saleService,
                    billingService, disciplineService, coachService);

            showLogin(authService, frameFactory);
        });
    }

    /** Muestra el login; al autenticar abre la ventana principal. Cerrar sesión vuelve aquí. */
    private static void showLogin(IAuthService authService, Supplier<PrincipalFrames> frameFactory) {
        new LoginFrame(authService, username -> {
            PrincipalFrames frame = frameFactory.get();
            frame.setOnLogout(() -> showLogin(authService, frameFactory));
            frame.setCurrentUser(username);
            frame.setVisible(true);
        }).setVisible(true);
    }
}