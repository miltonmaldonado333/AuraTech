package com.creatinaboys.ui;

import com.creatinaboys.service.IBillingService;
import com.creatinaboys.service.IDisciplineService;
import com.creatinaboys.service.MembershipExpirationPolicy;
import com.creatinaboys.service.AthleteService;
import com.creatinaboys.service.ISaleService;
import com.creatinaboys.ui.components.BrandHeader;
import com.creatinaboys.ui.components.SidebarButton;
import com.creatinaboys.ui.panels.AthleteListPanel;
import com.creatinaboys.ui.panels.AthleteRegisterPanel;
import com.creatinaboys.ui.panels.CategoryPanel;
import com.creatinaboys.ui.panels.DisciplinePanel;
import com.creatinaboys.ui.theme.ThemeConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana Principal del Sistema Creatina-Boys con Navegación Lateral y CardLayout (Mockups 1-9).
 * 
 * @author Milton Andrés Maldonado Salcedo
 */
public class PrincipalFrames extends JFrame {

    private final AthleteService athleteService;
    private final ISaleService saleService;
    private final IBillingService billingService;
    private final IDisciplineService disciplineService;

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private List<SidebarButton> sidebarButtons;

    private AthleteListPanel athleteListPanel;
    private CategoryPanel categoryPanel;
    private DisciplinePanel disciplinePanel;

    public PrincipalFrames(AthleteService athleteService, ISaleService saleService,
                           IBillingService billingService, IDisciplineService disciplineService) {
        this.athleteService = athleteService;
        this.saleService = saleService;
        this.billingService = billingService;
        this.disciplineService = disciplineService;
        this.sidebarButtons = new ArrayList<>();

        initFrame();
        initSidebar();
        initMainContent();
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
        JPanel menuPanel = new JPanel(new GridLayout(9, 1, 0, 5));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] menuItems = {
                "Inicio", "Deportistas", "Registrar", 
                "Categorías", "Disciplinas", "Entrenadores", 
                "Pagos", "Reportes", "Configuración"
        };

        for (int i = 0; i < menuItems.length; i++) {
            String name = menuItems[i];
            SidebarButton btn = new SidebarButton(name);
            sidebarButtons.add(btn);

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
                if ("CATEGORÍAS".equals(cardName) && categoryPanel != null) {
                    categoryPanel.refresh();
                } else if ("DISCIPLINAS".equals(cardName) && disciplinePanel != null) {
                    disciplinePanel.refresh();
                }
                cardLayout.show(mainContentPanel, cardName);
            });

            menuPanel.add(btn);
        }

        // Seleccionar primer botón
        selectSidebarButton(sidebarButtons.get(0));

        sidebar.add(menuPanel, BorderLayout.CENTER);

        // Botón Cerrar Sesión (con margen)
        SidebarButton btnLogout = new SidebarButton("Cerrar Sesión");
        btnLogout.addActionListener(e -> System.exit(0));

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

        // Paneles Placeholders para las demás secciones
        mainContentPanel.add(createPlaceholderPanel("Bienvenido al Sistema Creatina-Boys"), "INICIO");
        mainContentPanel.add(athleteListPanel, "DEPORTISTAS");
        mainContentPanel.add(registerPanel, "REGISTRAR");
        mainContentPanel.add(categoryPanel, "CATEGORÍAS");
        mainContentPanel.add(disciplinePanel, "DISCIPLINAS");
        mainContentPanel.add(createPlaceholderPanel("Módulo de Entrenadores por Categoría"), "ENTRENADORES");
        mainContentPanel.add(createPlaceholderPanel("Módulo de Control de Pagos y Cartera"), "PAGOS");
        mainContentPanel.add(createPlaceholderPanel("Módulo de Reportes Financieros"), "REPORTES");
        mainContentPanel.add(createPlaceholderPanel("Configuración y Perfil del Club"), "CONFIGURACIÓN");

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private void selectSidebarButton(SidebarButton selected) {
        for (SidebarButton btn : sidebarButtons) {
            btn.setActive(btn == selected);
        }
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(ThemeConstants.MAIN_BG);
        JLabel lbl = new JLabel(title);
        lbl.setFont(ThemeConstants.FONT_TITLE);
        lbl.setForeground(ThemeConstants.TEXT_MUTED);
        p.add(lbl);
        return p;
    }
}