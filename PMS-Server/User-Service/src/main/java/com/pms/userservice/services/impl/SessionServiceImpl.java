package com.pms.userservice.services.impl;

import com.pms.userservice.entities.Session;
import com.pms.userservice.entities.User;
import com.pms.userservice.exceptions.ResourceNotFound;
import com.pms.userservice.repositories.SessionRepository;
import com.pms.userservice.services.SessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private static final Integer SESSION_LIMIT = 1;

    @Override
    @Transactional
    public void generateNewSession(User user, String refreshToken) {
        // Get all active sessions for the user
        List<Session> userSessions = sessionRepository.findByUser(user);

        // If session limit is reached, delete the least recently used session
        if (userSessions.size() >= SESSION_LIMIT) {
            userSessions.sort(Comparator.comparing(Session::getLastUsedBy));
            Session lastRecentSession = userSessions.getFirst();
            sessionRepository.delete(lastRecentSession);
        }

        // Create and save a new session
        Session session = Session.builder()
                .user(user)
                .refreshToken(refreshToken)
                .build();

        sessionRepository.save(session);
    }

    @Override
    public boolean validateSession(String refreshToken) {
        // Check if session exists for the given refresh token
        Optional<Session> optionalSession = sessionRepository.findByRefreshToken(refreshToken);
        return optionalSession.isPresent();
    }

    @Override
    public void deleteSession(String refreshToken) {
        // Delete the session associated with the refresh token
        Session session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFound("No active session found!"));
        sessionRepository.delete(session);
    }
}
