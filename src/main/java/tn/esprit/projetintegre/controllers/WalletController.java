package tn.esprit.projetintegre.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.projetintegre.nadineentities.Wallet;
import tn.esprit.projetintegre.servicenadine.WalletService;

import java.util.List;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    // CREATE - Ajouter un wallet
    @PostMapping("/add")
    public ResponseEntity<Wallet> add(@RequestBody Wallet wallet) {
        return ResponseEntity.ok(walletService.create(wallet));
    }

    // READ - Afficher tous les wallets
    @GetMapping("/all")
    public ResponseEntity<List<Wallet>> getAll() {
        return ResponseEntity.ok(walletService.readAll());
    }

    // READ - Afficher un wallet par son id
    @GetMapping("/get")
    public ResponseEntity<Wallet> getById(@RequestParam Long id) {
        return ResponseEntity.ok(walletService.readById(id));
    }

    // UPDATE - Modifier un wallet
    @PutMapping("/update")
    public ResponseEntity<Wallet> update(@RequestParam Long id, @RequestBody Wallet wallet) {
        return ResponseEntity.ok(walletService.update(id, wallet));
    }

    // DELETE - Supprimer un wallet
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam Long id) {
        walletService.delete(id);
        return ResponseEntity.ok().build();
    }
}