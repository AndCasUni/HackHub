package it.hackhub.service.external;

import it.hackhub.model.domain.Team;

/**
 * Servizio esterno per l'erogazione dei premi[cite: 33].
 */
public interface PaymentService {
    /**
     * Eroga il premio in denaro al team vincitore[cite: 34].
     */
    void processPrizePayment(Team winner, double amount);
}