package it.hackhub.service.external;

import it.hackhub.model.domain.Team;

/**
 * Servizio esterno per l'erogazione dei premi
 */
public interface PaymentService {
    /**
     * Eroga il premio in denaro al team vincitore
     */
    void processPrizePayment(Team winner, double amount);
}