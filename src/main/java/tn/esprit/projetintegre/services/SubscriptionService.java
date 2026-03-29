package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.projetintegre.entities.Subscription;
import tn.esprit.projetintegre.entities.User;
import tn.esprit.projetintegre.enums.SubscriptionStatus;
import tn.esprit.projetintegre.exception.ResourceNotFoundException;
import tn.esprit.projetintegre.repositories.SubscriptionRepository;
import tn.esprit.projetintegre.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final InvoiceService invoiceService;

    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public Page<Subscription> getAllSubscriptions(Pageable pageable) {
        return subscriptionRepository.findAll(pageable);
    }

    public Subscription getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));
    }

    public Page<Subscription> getSubscriptionsByUserId(Long userId, Pageable pageable) {
        return subscriptionRepository.findByUserId(userId, pageable);
    }

    public List<Subscription> getActiveSubscriptionsByUserId(Long userId) {
        return subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE);
    }

    public List<Subscription> getSubscriptionsByStatus(SubscriptionStatus status) {
        return subscriptionRepository.findByStatus(status);
    }

    public Subscription updateSubscription(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    public Subscription updateSubscription(Long id, Subscription subscriptionDetails) {
        Subscription subscription = getSubscriptionById(id);
        subscription.setPlanName(subscriptionDetails.getPlanName());
        subscription.setPlanType(subscriptionDetails.getPlanType());
        subscription.setPrice(subscriptionDetails.getPrice());
        subscription.setAutoRenew(subscriptionDetails.getAutoRenew());

        return subscriptionRepository.save(subscription);
    }

    public Subscription createSubscription(Subscription subscription, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        subscription.setUser(user);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDateTime.now());
        if (subscription.getEndDate() == null) {
            subscription.setEndDate(LocalDateTime.now().plusMonths(1));
        }
        return subscriptionRepository.save(subscription);
    }

    /**
     * Charge wallet, create subscription and invoice (integrated checkout flow).
     */
    public Subscription subscribeWithWalletPayment(User user, String planName,
                                                   BigDecimal price, int durationDays) {
        walletService.withdraw(user.getId(), price, "Abonnement " + planName);

        Subscription sub = subscriptionRepository.save(Subscription.builder()
                .user(user)
                .planName(planName)
                .price(price)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(durationDays))
                .renewalDate(LocalDateTime.now().plusDays(durationDays))
                .autoRenew(true)
                .build());

        invoiceService.generateFromSubscription(sub);
        return sub;
    }

    public Subscription activateSubscription(Long id) {
        Subscription subscription = getSubscriptionById(id);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        return subscriptionRepository.save(subscription);
    }

    public Subscription cancelSubscription(Long id) {
        Subscription subscription = getSubscriptionById(id);
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setAutoRenew(false);
        return subscriptionRepository.save(subscription);
    }

    public Subscription suspendSubscription(Long id) {
        Subscription subscription = getSubscriptionById(id);
        subscription.setStatus(SubscriptionStatus.SUSPENDED);
        return subscriptionRepository.save(subscription);
    }

    public Subscription renewSubscription(Long id, int months) {
        Subscription subscription = getSubscriptionById(id);
        subscription.setEndDate(subscription.getEndDate().plusMonths(months));
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        return subscriptionRepository.save(subscription);
    }

    public void processSubscriptionRenewals() {
        List<Subscription> toRenew = subscriptionRepository
                .findByAutoRenewTrueAndRenewalDateBefore(LocalDateTime.now());

        toRenew.forEach(sub -> {
            try {
                walletService.withdraw(sub.getUser().getId(), sub.getPrice(),
                        "Renouvellement " + sub.getPlanName());
                sub.setStartDate(LocalDateTime.now());
                sub.setEndDate(LocalDateTime.now().plusDays(30));
                sub.setRenewalDate(LocalDateTime.now().plusDays(30));
                subscriptionRepository.save(sub);
                invoiceService.generateFromSubscription(sub);
            } catch (Exception e) {
                sub.setStatus(SubscriptionStatus.EXPIRED);
                subscriptionRepository.save(sub);
            }
        });
    }

    public void deleteSubscription(Long id) {
        Subscription subscription = getSubscriptionById(id);
        subscriptionRepository.delete(subscription);
    }
}
