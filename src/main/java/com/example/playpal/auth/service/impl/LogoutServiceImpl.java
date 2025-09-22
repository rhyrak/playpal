package com.example.playpal.auth.service.impl;

import com.example.playpal.auth.repository.UserRepository;
import com.example.playpal.auth.service.LogoutService;
import com.example.playpal.common.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {
    private final UserRepository userRepository;
    private final AuthUtils authUtils;

    @Override
    public void logout() {
        var userId = authUtils.getAuthenticatedUserId();
        var user = userRepository.findById(userId).orElseThrow();

        user.setLastLogout(new Date());
        userRepository.save(user);
    }
}
