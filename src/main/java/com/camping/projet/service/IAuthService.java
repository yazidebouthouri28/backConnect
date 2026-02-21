package com.camping.projet.service;

import com.camping.projet.dto.request.LoginRequest;
import com.camping.projet.dto.request.UserRequest;
import com.camping.projet.dto.response.JwtResponse;
import com.camping.projet.dto.response.UserResponse;

public interface IAuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);

    UserResponse registerUser(UserRequest signUpRequest);

    void verifyEmail(String token);

    void initiatePasswordReset(String email);

    void resetPassword(String token, String newPassword);
}
