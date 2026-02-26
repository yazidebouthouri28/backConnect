package tn.esprit.backconnect.enums;

/**
 * Statut des transactions financières.
 */
public enum TransactionStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED,
    REFUNDED,
    ON_HOLD
}
