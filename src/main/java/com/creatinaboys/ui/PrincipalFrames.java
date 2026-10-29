
package com.creatinaboys.ui;

import com.creatinaboys.service.IBillingService;
import com.creatinaboys.service.ICoachService;
import com.creatinaboys.service.IDisciplineService;
import com.creatinaboys.service.MembershipExpirationPolicy;
import com.creatinaboys.service.AthleteService;
import com.creatinaboys.service.ISaleService;
import com.creatinaboys.ui.components.BrandHeader;
import com.creatinaboys.ui.components.SidebarButton;
import com.creatinaboys.ui.panels.AthleteListPanel;
import com.creatinaboys.ui.panels.AthleteRegisterPanel;
import com.creatinaboys.ui.panels.CategoryPanel;
import com.creatinaboys.ui.panels.CoachPanel;
import com.creatinaboys.ui.panels.DisciplinePanel;
import com.creatinaboys.ui.panels.HomePanel;
import com.creatinaboys.ui.panels.PaymentPanel;
import com.creatinaboys.ui.panels.ReportsPanel;
import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana Principal del Sistema Creatina-Boys con Navegación Lateral y
 * CardLayout (Mockups 1-9).
 *
 * @author Milton Andrés Maldonado Salcedo
 */
public class PrincipalFrames extends JFrame {

    private final AthleteService athleteService;
    private final ISaleService saleService;
    private final IBillingService billingService;
    private final IDisciplineService disciplineService;
    private final ICoachService coachService;

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private List<SidebarButton> sidebarButtons;
    private final List<String> menuNames = new ArrayList<>();

    private HomePanel homePanel;

    private AthleteListPanel athleteListPanel;
    private CategoryPanel categoryPanel;
    private DisciplinePanel disciplinePanel;

    private CoachPanel coachPanel;

    private PaymentPanel paymentPanel;

    private ReportsPanel reportsPanel;

    /** Acción posterior al cerrar sesión (por defecto, salir de la aplicación). */
    private Runnable onLogout = () -> System.exit(0);

    public PrincipalFrames(AthleteService athleteService, ISaleService saleService,
            IBillingService billingService, IDisciplineService disciplineService,
            ICoachService coachService) {
        this.athleteService = athleteService;
        this.saleService = saleService;
        this.billingService = billingService;
        this.disciplineService = disciplineService;
        this.coachService = coachService;
        this.sidebarButtons = new ArrayList<>();

        initFrame();
        initSidebar();
        initMainContent();
    }

    /** Define qué ocurre al pulsar "Cerrar Sesión" (Main lo usa para volver al login). */
    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    /** Usuario que inició sesión (se muestra en el saludo del Inicio). */
    public void setCurrentUser(String username) {
        if (homePanel != null) {
            homePanel.setUsername(username);
        }
    }

    /** Va a una opción del menú lateral por su nombre (ej. "Registrar", "Pagos"). */
    private void navigateTo(String menuName) {
        int idx = menuNames.indexOf(menuName);
        if (idx >= 0) {
            sidebarButtons.get(idx).doClick();
        }
    }

    private void initFrame() {
        setTitle("Club Deportivo Creatina-Boys - Sistema de Gestión Integral");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setMinimumSize(new Dimension(1024, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(ThemeConstants.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(240, 0));

        // Logo / Header
        sidebar.add(new BrandHeader(), BorderLayout.NORTH);

        // Menú Opciones
        JPanel menuPanel = new JPanel(new GridLayout(8, 1, 0, 5));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] menuItems = {
            "Inicio", "Deportistas", "Registrar",
            "Categorías", "Disciplinas", "Entrenadores",
            "Pagos", "Reportes"
        };

        for (int i = 0; i < menuItems.length; i++) {
            String name = menuItems[i];
            SidebarButton btn = new SidebarButton(name);
            sidebarButtons.add(btn);
            menuNames.add(name);

            final String cardName = name.toUpperCase();
            btn.addActionListener(e -> {
                selectSidebarButton(btn);
                if ("DEPORTISTAS".equals(cardName) && athleteListPanel != null) {
                    // Recalcula el estado (Activo/Inactivo) antes de mostrar la tabla.
                    // Cambia esta regla por la que necesites:
                    //   a -> true                 -> todos activos
                    //   a -> a.getAge() > 10      -> prueba de insignia roja
                    //   a -> paymentService...    -> cuando exista el módulo de Pagos
                    athleteService.applyActivationPolicy(new MembershipExpirationPolicy(5));
                    athleteListPanel.loadAthleteData();
                }
                if ("INICIO".equals(cardName) && homePanel != null) {
                    homePanel.refresh();
                }
                if ("CATEGORÍAS".equals(cardName) && categoryPanel != null) {
                    categoryPanel.refresh();
                } else if ("DISCIPLINAS".equals(cardName) && disciplinePanel != null) {
                    disciplinePanel.refresh();

                } else if ("ENTRENADORES".equals(cardName) && coachPanel != null) {
                    coachPanel.loadCoaches();

                } else if ("PAGOS".equals(cardName) && paymentPanel != null) {
                    paymentPanel.refresh();

                } else if ("REPORTES".equals(cardName) && reportsPanel != null) {
                    reportsPanel.refresh();
                }
                cardLayout.show(mainContentPanel, cardName);
            });

            menuPanel.add(btn);
        }

        // Seleccionar primer botón
        selectSidebarButton(sidebarButtons.get(0));

        sidebar.add(menuPanel, BorderLayout.CENTER);

        // Botón Cerrar Sesión (con margen): vuelve a la pantalla de login
        SidebarButton btnLogout = new SidebarButton("Cerrar Sesión");
        btnLogout.addActionListener(e -> {
            dispose();
            onLogout.run();
        });

        JPanel logoutWrap = new JPanel(new BorderLayout());
        logoutWrap.setOpaque(false);
        logoutWrap.setBorder(new EmptyBorder(0, 10, 16, 10));
        logoutWrap.add(btnLogout, BorderLayout.CENTER);
        sidebar.add(logoutWrap, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);
    }

    private void initMainContent() {
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(ThemeConstants.MAIN_BG);

        // Instanciar vistas
        athleteListPanel = new AthleteListPanel(athleteService);
        AthleteRegisterPanel registerPanel = new AthleteRegisterPanel(athleteService);
        categoryPanel = new CategoryPanel(athleteService, billingService, disciplineService);
        disciplinePanel = new DisciplinePanel(athleteService, disciplineService);

        coachPanel = new CoachPanel(coachService);

        paymentPanel = new PaymentPanel(athleteService, saleService);

        reportsPanel = new ReportsPanel(athleteService, saleService);

        homePanel = new HomePanel(athleteService, saleService, coachService, this::navigateTo);

        mainContentPanel.add(homePanel, "INICIO");
        mainContentPanel.add(athleteListPanel, "DEPORTISTAS");
        mainContentPanel.add(registerPanel, "REGISTRAR");
        mainContentPanel.add(categoryPanel, "CATEGORÍAS");
        mainContentPanel.add(disciplinePanel, "DISCIPLINAS");

        mainContentPanel.add(coachPanel, "ENTRENADORES");

        mainContentPanel.add(paymentPanel, "PAGOS");

        mainContentPanel.add(reportsPanel, "REPORTES");

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private void selectSidebarButton(SidebarButton selected) {
        for (SidebarButton btn : sidebarButtons) {
            btn.setActive(btn == selected);
        }
    }
}
 