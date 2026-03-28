package tn.esprit.projetintegre.servicenadine;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetintegre.nadineentities.Wallet;
import tn.esprit.projetintegre.repositorynadine.WalletRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    // CREATE
    public Wallet create(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    // READ (tous)
    public List<Wallet> readAll() {
        return walletRepository.findAll();
    }

    // READ (par id)
    public Wallet readById(Long id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wallet non trouvé"));
    }

    // UPDATE
    public Wallet update(Long id, Wallet wallet) {
        Wallet existingWallet = readById(id);
        existingWallet.setBalance(wallet.getBalance());
        existingWallet.setTotalDeposited(wallet.getTotalDeposited());
        existingWallet.setTotalWithdrawn(wallet.getTotalWithdrawn());
        existingWallet.setCurrency(wallet.getCurrency());
        existingWallet.setIsActive(wallet.getIsActive());
        return walletRepository.save(existingWallet);
    }

    // DELETE
    public void delete(Long id) {
        walletRepository.deleteById(id);
    }
}