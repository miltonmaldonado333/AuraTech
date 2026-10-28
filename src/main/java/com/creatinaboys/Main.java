package com.creatinaboys;

import com.creatinaboys.repository.FileAthleteRepository;
import com.creatinaboys.repository.IAthleteRepository;
import com.creatinaboys.service.AthleteService;
import com.creatinaboys.service.BillingService;
import com.creatinaboys.service.IBillingService;
import com.creatinaboys.service.ISaleService;
import com.creatinaboys.service.SaleService;
import com.creatinaboys.ui.PrincipalFrames;

import javax.swing.*;

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
            IBillingService billingService = new BillingService();

            AthleteService athleteService = new AthleteService(repository, billingService);
            ISaleService saleService = new SaleService(repository, billingService);

            // Iniciar interfaz gráfica
            PrincipalFrames frame = new PrincipalFrames(athleteService, saleService);
            frame.setVisible(true);
        });
    }
}