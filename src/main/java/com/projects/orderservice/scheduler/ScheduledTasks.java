package com.projects.orderservice.scheduler;

import com.projects.orderservice.service.interfaces.CommandeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
public class ScheduledTasks {

    private final CommandeService commandeService;

    @Autowired
    public ScheduledTasks(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    /**
     * Tâche planifiée pour traiter les commandes non payées depuis plus de 24h
     * Exécutée toutes les heures
     */
    @Scheduled(cron = "0 0 * * * ?") // Toutes les heures
    public void processUnpaidOrders() {
        log.info("Exécution de la tâche planifiée pour annuler les commandes non payées depuis plus de 24h");
        int count = commandeService.processUnpaidOrders();
        log.info("{} commandes ont été annulées", count);
    }
}