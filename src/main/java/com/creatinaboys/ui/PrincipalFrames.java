
package com.creatinaboys.ui;

import com.creatinaboys.service.IBillingService;
import com.creatinaboys.service.ICoachService;
import com.creatinaboys.service.IDisciplineService;
import com.creatinaboys.service.IHealthService;
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
import com.creatinaboys.ui.panels.HealthPanel;
import com.creatinaboys.ui.panels.HomePanel;
import com.creatinaboys.ui.panels.PaymentPanel;
import com.creatinaboys.ui.panels.ReportsPanel;
import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PrincipalFrames extends JFrame {

    private final AthleteService athleteService;
    private final ISaleService saleService;
    private final IBillingService billingService;
    private final IDisciplineService disciplineService;
    private final ICoachService coachService;
    private final IHealthService healthService;

    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    private final List<SidebarButton> sidebarButtons =
            new ArrayList<>();

    private final List<String> menuNames =
            new ArrayList<>();

    private HomePanel homePanel;
    private AthleteListPanel athleteListPanel;
    private AthleteRegisterPanel registerPanel;
    private CategoryPanel categoryPanel;
    private DisciplinePanel disciplinePanel;
    private CoachPanel coachPanel;
    private HealthPanel healthPanel;
    private PaymentPanel paymentPanel;
    private ReportsPanel reportsPanel;

    private Runnable onLogout = () -> System.exit(0);

    public PrincipalFrames(
            AthleteService athleteService,
            ISaleService saleService,
            IBillingService billingService,
            IDisciplineService disciplineService,
            ICoachService coachService,
            IHealthService healthService) {

        this.athleteService = athleteService;
        this.saleService = saleService;
        this.billingService = billingService;
        this.disciplineService = disciplineService;
        this.coachService = coachService;
        this.healthService = healthService;

        initFrame();
        initMainContent();
        initSidebar();
    }

    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    public void setCurrentUser(String username) {
        if (homePanel != null) {
            homePanel.setUsername(username);
        }
    }

    private void navigateTo(String menuName) {
        int index = menuNames.indexOf(menuName);

        if (index >= 0) {
            sidebarButtons.get(index).doClick();
        }
    }

    private void initFrame() {
        setTitle(
                "Creatina-Boys - Sports Club Management");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setMinimumSize(new Dimension(1024, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initSidebar() {

        JPanel sidebar = new JPanel(new BorderLayout());

        sidebar.setBackground(
                ThemeConstants.SIDEBAR_BG);

        sidebar.setPreferredSize(
                new Dimension(240, 0));

        sidebar.add(
                new BrandHeader(),
                BorderLayout.NORTH);

        JPanel menuPanel = new JPanel(
                new GridLayout(9, 1, 0, 5));

        menuPanel.setOpaque(false);

        menuPanel.setBorder(
                new EmptyBorder(10, 10, 10, 10));

        String[] menuItems = {
            "Inicio",
            "Deportistas",
            "Registrar",
            "Categorías",
            "Disciplinas",
            "Entrenadores",
            "Salud",
            "Pagos",
            "Reportes"
        };

        for (String name : menuItems) {

            SidebarButton button =
                    new SidebarButton(name);

            sidebarButtons.add(button);
            menuNames.add(name);

            String cardName = name.toUpperCase();

            button.addActionListener(e -> {

                try {
                    boolean newlyCreated =
                            ensurePanel(cardName);

                    selectSidebarButton(button);

                    cardLayout.show(
                            mainContentPanel, cardName);

                    if (!newlyCreated) {
                        refreshPanel(cardName);
                    }

                } catch (RuntimeException ex) {

                    ex.printStackTrace();

                    JOptionPane.showMessageDialog(
                            this,
                            "Unable to open module:\n"
                            + ex.getMessage(),
                            "Application Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });

            menuPanel.add(button);
        }

        selectSidebarButton(sidebarButtons.get(0));

        sidebar.add(menuPanel, BorderLayout.CENTER);

        SidebarButton logoutButton =
                new SidebarButton("Cerrar Sesión");

        logoutButton.addActionListener(e -> {
            dispose();
            onLogout.run();
        });

        JPanel logoutWrap =
                new JPanel(new BorderLayout());

        logoutWrap.setOpaque(false);

        logoutWrap.setBorder(
                new EmptyBorder(0, 10, 16, 10));

        logoutWrap.add(
                logoutButton,
                BorderLayout.CENTER);

        sidebar.add(
                logoutWrap,
                BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);
    }

    private void initMainContent() {

        cardLayout = new CardLayout();

        mainContentPanel =
                new JPanel(cardLayout);

        mainContentPanel.setBackground(
                ThemeConstants.MAIN_BG);

        // Load only the home panel at startup.
        homePanel = new HomePanel(
                athleteService,
                saleService,
                coachService,
                this::navigateTo
        );

        mainContentPanel.add(
                homePanel, "INICIO");

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private boolean ensurePanel(String cardName) {

        switch (cardName) {

            case "INICIO":
                return false;

            case "DEPORTISTAS":

                if (athleteListPanel == null) {

                    athleteService.applyActivationPolicy(
                            new MembershipExpirationPolicy(5));

                    athleteListPanel =
                            new AthleteListPanel(
                                    athleteService,
                                    saleService);

                    mainContentPanel.add(
                            athleteListPanel,
                            cardName);

                    return true;
                }
                return false;

            case "REGISTRAR":

                if (registerPanel == null) {

                    registerPanel =
                            new AthleteRegisterPanel(
                                    athleteService);

                    mainContentPanel.add(
                            registerPanel,
                            cardName);

                    return true;
                }
                return false;

            case "CATEGORÍAS":

                if (categoryPanel == null) {

                    categoryPanel =
                            new CategoryPanel(
                                    athleteService,
                                    billingService,
                                    disciplineService);

                    mainContentPanel.add(
                            categoryPanel,
                            cardName);

                    return true;
                }
                return false;

            case "DISCIPLINAS":

                if (disciplinePanel == null) {

                    disciplinePanel =
                            new DisciplinePanel(
                                    athleteService,
                                    disciplineService);

                    mainContentPanel.add(
                            disciplinePanel,
                            cardName);

                    return true;
                }
                return false;

            case "ENTRENADORES":

                if (coachPanel == null) {

                    coachPanel =
                            new CoachPanel(coachService);

                    mainContentPanel.add(
                            coachPanel,
                            cardName);

                    return true;
                }
                return false;

            case "SALUD":

                if (healthPanel == null) {

                    healthPanel =
                            new HealthPanel(
                                    athleteService,
                                    healthService);

                    mainContentPanel.add(
                            healthPanel,
                            cardName);

                    return true;
                }
                return false;

            case "PAGOS":

                if (paymentPanel == null) {

                    paymentPanel =
                            new PaymentPanel(
                                    athleteService,
                                    saleService);

                    mainContentPanel.add(
                            paymentPanel,
                            cardName);

                    return true;
                }
                return false;

            case "REPORTES":

                if (reportsPanel == null) {

                    reportsPanel =
                            new ReportsPanel(
                                    athleteService,
                                    saleService);

                    mainContentPanel.add(
                            reportsPanel,
                            cardName);

                    return true;
                }
                return false;

            default:
                throw new IllegalArgumentException(
                        "Unknown module: " + cardName);
        }
    }

    private void refreshPanel(String cardName) {

        switch (cardName) {

            case "INICIO":
                homePanel.refresh();
                break;

            case "DEPORTISTAS":

                athleteService.applyActivationPolicy(
                        new MembershipExpirationPolicy(5));

                athleteListPanel.loadAthleteData();
                break;

            case "CATEGORÍAS":
                categoryPanel.refresh();
                break;

            case "DISCIPLINAS":
                disciplinePanel.refresh();
                break;

            case "ENTRENADORES":
                coachPanel.loadCoaches();
                break;

            case "SALUD":
                healthPanel.refresh();
                break;

            case "PAGOS":
                paymentPanel.refresh();
                break;

            case "REPORTES":
                reportsPanel.refresh();
                break;

            default:
                break;
        }
    }

    private void selectSidebarButton(
            SidebarButton selected) {

        for (SidebarButton button : sidebarButtons) {
            button.setActive(button == selected);
        }
    }
}
