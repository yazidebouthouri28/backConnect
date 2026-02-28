package tn.esprit.projetintegre.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetintegre.entities.Certification;
import tn.esprit.projetintegre.entities.CertificationItem;
import tn.esprit.projetintegre.repositories.CertificationItemRepository;
import tn.esprit.projetintegre.repositories.CertificationRepository;


import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificationItemService  {

    private final CertificationItemRepository certificationItemRepository;
    private final CertificationRepository certificationRepository;

    public List<CertificationItem> getItemsByCertification(Long certificationId) {
        return certificationItemRepository.findByCertification_CertificationId(certificationId);
    }

    public CertificationItem addItem(Long certificationId, CertificationItem item) {
        Certification cert = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new RuntimeException("Certification not found"));
        item.setCertification(cert);
        CertificationItem saved = certificationItemRepository.save(item);
        // Recalculate total score
        recalculateScore(cert);
        return saved;
    }

    public CertificationItem updateItem(Long itemId, CertificationItem updated) {
        CertificationItem existing = certificationItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        existing.setCriteriaName(updated.getCriteriaName());
        existing.setScore(updated.getScore());
        existing.setComment(updated.getComment());
        CertificationItem saved = certificationItemRepository.save(existing);
        recalculateScore(existing.getCertification());
        return saved;
    }

    public void deleteItem(Long itemId) {
        CertificationItem item = certificationItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        Certification cert = item.getCertification();
        certificationItemRepository.deleteById(itemId);
        recalculateScore(cert);
    }

    private void recalculateScore(Certification cert) {
        List<CertificationItem> items = certificationItemRepository
                .findByCertification_CertificationId(cert.getCertificationId());
        // Each item is 0-10, scale total to 0-100
        int total = items.stream().mapToInt(CertificationItem::getScore).sum();
        int maxPossible = items.size() * 10;
        int score = maxPossible > 0 ? (total * 100) / maxPossible : 0;
        cert.setScore(score);
        certificationRepository.save(cert);
    }
}