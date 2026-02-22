package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.WarehouseDTO;
import tn.esprit.projetPi.entities.Warehouse;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.exception.DuplicateResourceException;
import tn.esprit.projetPi.repositories.WarehouseRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public List<WarehouseDTO> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<WarehouseDTO> getActiveWarehouses() {
        return warehouseRepository.findByIsActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public WarehouseDTO getWarehouseById(String id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
        return toDTO(warehouse);
    }

    public WarehouseDTO getWarehouseByCode(String code) {
        Warehouse warehouse = warehouseRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with code: " + code));
        return toDTO(warehouse);
    }

    public WarehouseDTO createWarehouse(WarehouseDTO dto) {
        if (warehouseRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("Warehouse already exists with code: " + dto.getCode());
        }
        Warehouse warehouse = toEntity(dto);
        if (warehouse.getIsActive() == null) warehouse.setIsActive(true);
        Warehouse saved = warehouseRepository.save(warehouse);
        return toDTO(saved);
    }

    public WarehouseDTO updateWarehouse(String id, WarehouseDTO dto) {
        Warehouse existing = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
        
        existing.setName(dto.getName());
        existing.setAddress(dto.getAddress());
        existing.setCity(dto.getCity());
        existing.setCountry(dto.getCountry());
        existing.setPhone(dto.getPhone());
        existing.setEmail(dto.getEmail());
        existing.setIsActive(dto.getIsActive());
        
        Warehouse saved = warehouseRepository.save(existing);
        return toDTO(saved);
    }

    public void deleteWarehouse(String id) {
        if (!warehouseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Warehouse not found with id: " + id);
        }
        warehouseRepository.deleteById(id);
    }

    public List<WarehouseDTO> getWarehousesByCity(String city) {
        return warehouseRepository.findByCity(city).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private WarehouseDTO toDTO(Warehouse warehouse) {
        WarehouseDTO dto = new WarehouseDTO();
        dto.setId(warehouse.getId());
        dto.setName(warehouse.getName());
        dto.setCode(warehouse.getCode());
        dto.setAddress(warehouse.getAddress());
        dto.setCity(warehouse.getCity());
        dto.setCountry(warehouse.getCountry());
        dto.setPhone(warehouse.getPhone());
        dto.setEmail(warehouse.getEmail());
        dto.setIsActive(warehouse.getIsActive());
        return dto;
    }

    private Warehouse toEntity(WarehouseDTO dto) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(dto.getName());
        warehouse.setCode(dto.getCode());
        warehouse.setAddress(dto.getAddress());
        warehouse.setCity(dto.getCity());
        warehouse.setCountry(dto.getCountry());
        warehouse.setPhone(dto.getPhone());
        warehouse.setEmail(dto.getEmail());
        warehouse.setIsActive(dto.getIsActive());
        return warehouse;
    }
}
