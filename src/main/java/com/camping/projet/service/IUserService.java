package com.camping.projet.service;

import com.camping.projet.dto.request.UserRequest;
import com.camping.projet.dto.response.UserResponse;
import com.camping.projet.enums.Role;
import java.math.BigDecimal;
import java.util.List;

public interface IUserService {
    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);

    UserResponse updateUser(Long id, UserRequest userRequest);

    void deleteUser(Long id);

    List<UserResponse> getAllUsers();

    List<UserResponse> getUsersByRole(Role role);

    List<UserResponse> getUsersByCamping(Long campingId);

    void updateProfilePhoto(Long id, String photoUrl);

    void incrementReservations(Long id);

    void incrementCommandes(Long id);

    void addDepense(Long id, BigDecimal montant);
}
