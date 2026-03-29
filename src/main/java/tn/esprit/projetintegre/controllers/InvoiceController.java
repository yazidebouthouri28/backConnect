package tn.esprit.projetintegre.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetintegre.entities.Invoice;
import tn.esprit.projetintegre.services.InvoiceService;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Invoice>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(invoiceService.getByUser(userId));
    }

//    @GetMapping("/order/{orderId}")
//    public ResponseEntity<Invoice> getByOrder(@PathVariable Long orderId) {
//        return ResponseEntity.ok(invoiceService.getByOrder(orderId));
//    }

    @PutMapping("/{id}/paid")
    public ResponseEntity<Invoice> markAsPaid(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.markAsPaid(id));
    }
}