package it.hackhub.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public class PaymentRepository {

    @Transactional
    public void savePaymentRecord(String teamId, double amount, String hackathonId) {
        System.out.println("[DB SPRING] Simulazione salvataggio pagamento: " + amount + "€ al team " + teamId);
    }
}