package tn.esprit.projetintegre.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetintegre.entities.CertificationItem;
import tn.esprit.projetintegre.services.CertificationItemService;


import java.util.List;

@RestController
@RequestMapping("/api/certification-items")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CertificationItemController {

    private final CertificationItemService certificationItemService;

    @GetMapping("/certification/{certificationId}")
    public List<CertificationItem> getByC(@PathVariable Long certificationId) {
        return certificationItemService.getItemsByCertification(certificationId);
    }

    @PostMapping("/certification/{certificationId}")
    public ResponseEntity<CertificationItem> add(@PathVariable Long certificationId,
                                                 @RequestBody CertificationItem item) {
        return ResponseEntity.ok(certificationItemService.addItem(certificationId, item));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<CertificationItem> update(@PathVariable Long itemId,
                                                    @RequestBody CertificationItem item) {
        return ResponseEntity.ok(certificationItemService.updateItem(itemId, item));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> delete(@PathVariable Long itemId) {
        certificationItemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}