package com.creatinaboys;

import com.creatinaboys.repository.FileCoachRepository;
import com.creatinaboys.repository.FileHealthRepository;
import com.creatinaboys.repository.FilePaymentRepository;
import com.creatinaboys.repository.Fileuserrepository;
import com.creatinaboys.repository.IAthleteRepository;
import com.creatinaboys.repository.ICoachRepository;
import com.creatinaboys.repository.IHealthRepository;
import com.creatinaboys.repository.IPaymentRepository;
import com.creatinaboys.repository.PostgresAthleteRepository;
import com.creatinaboys.repository.PostgresCategoryRepository;
import com.creatinaboys.repository.PostgresDisciplineRepository;

import com.creatinaboys.service.AthleteService;
import com.creatinaboys.service.Authservice;
import com.creatinaboys.service.BillingService;
import com.creatinaboys.service.CategoryService;
import com.creatinaboys.service.CoachService;
import com.creatinaboys.service.DashboardService;
import com.creatinaboys.service.DisciplineService;
import com.creatinaboys.service.HealthService;
import com.creatinaboys.service.IAuthService;
import com.creatinaboys.service.IBillingService;
import com.creatinaboys.service.ICoachService;
import com.creatinaboys.service.IDashBoardService;
import com.creatinaboys.service.IDisciplineService;
import com.creatinaboys.service.IHealthService;
import com.creatinaboys.service.ISaleService;
import com.creatinaboys.service.PaymentDisciplineMigration;
import com.creatinaboys.service.SaleService;

import com.creatinaboys.ui.LoginFrame;
import com.creatinaboys.ui.PrincipalFrames;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.function.Supplier;

/**
 * Application entry point.
 *
 * @author Milton Andres Maldonado Salcedo
 */
public class Main {

    public static void main(String[] args) {

        // Configure the system look and feel.
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {

            // Initialize repositories.
            IAthleteRepository repository =
                    new PostgresAthleteRepository();

            IPaymentRepository paymentRepository =
                    new FilePaymentRepository();

            ICoachRepository coachRepository =
                    new FileCoachRepository();

            IHealthRepository healthRepository =
                    new FileHealthRepository();

            // Initialize shared services.
            CategoryService categoryService =
                    new CategoryService(new PostgresCategoryRepository());

            IBillingService billingService =
                    new BillingService(categoryService);

            IDisciplineService disciplineService =
                    new DisciplineService(new PostgresDisciplineRepository());

            // Migrate legacy payment disciplines.
            try {
                new PaymentDisciplineMigration(
                        paymentRepository,
                        repository
                ).run();
            } catch (RuntimeException exception) {
                JOptionPane.showMessageDialog(
                        null,
                        "Unable to update payment history:\n" + exception.getMessage(),
                        "AuraTech",
                        JOptionPane.WARNING_MESSAGE
                );
            }

            AthleteService athleteService =
                    new AthleteService(repository, billingService);

            ISaleService saleService =
                    new SaleService(
                            repository,
                            billingService,
                            paymentRepository
                    );

            ICoachService coachService =
                    new CoachService(coachRepository);

            IHealthService healthService =
                    new HealthService(healthRepository, repository);

            IDashBoardService dashboardService =
                    new DashboardService(
                            repository,
                            paymentRepository,
                            coachService,
                            categoryService
                    );

            IAuthService authService =
                    new Authservice(new Fileuserrepository());

            try {
                authService.ensureDefaultUser();
            } catch (RuntimeException exception) {
                JOptionPane.showMessageDialog(
                        null,
                        "Unable to initialize user accounts:\n" + exception.getMessage(),
                        "AuraTech",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            Supplier<PrincipalFrames> frameFactory = () ->
                    new PrincipalFrames(
                            athleteService,
                            saleService,
                            categoryService,
                            disciplineService,
                            coachService,
                            healthService,
                            dashboardService
                    );

            showLogin(authService, frameFactory);
        });
    }

    private static void showLogin(
            IAuthService authService,
            Supplier<PrincipalFrames> frameFactory) {

        new LoginFrame(authService, username -> {
            PrincipalFrames frame = frameFactory.get();

            frame.setOnLogout(
                    () -> showLogin(authService, frameFactory)
            );

            frame.setCurrentUser(username);
            frame.setVisible(true);
        }).setVisible(true);
    }
}