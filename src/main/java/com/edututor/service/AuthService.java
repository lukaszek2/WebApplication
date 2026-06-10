package com.edututor.service;

import com.edututor.dto.auth.JwtResponse;
import com.edututor.dto.auth.LoginRequest;
import com.edututor.dto.auth.RegisterRequest;
import com.edututor.entity.Student;
import com.edututor.repository.UserRepository;
import com.edututor.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SystemLogService systemLogService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, SystemLogService systemLogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.systemLogService = systemLogService;
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        Student saved = (Student) userRepository.save(student);
        systemLogService.log("REGISTER", "Nowe konto studenta: " + request.getEmail(), saved.getId(), request.getEmail());
    }

    public JwtResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            systemLogService.log("LOGIN_FAILED", "Nieudana próba logowania: " + request.getEmail(), null, request.getEmail());
            throw new IllegalArgumentException("Invalid credentials");
        }
        if (!user.getIsActive()) {
            throw new IllegalArgumentException("Account is suspended");
        }

        systemLogService.log("LOGIN", "Zalogowano jako " + user.getRole().name(), user.getId(), user.getEmail());
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new JwtResponse(token, user.getRole().name());
    }
}
