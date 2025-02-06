package com.pms.userservice.services.impl;

import com.pms.userservice.entities.Session;
import com.pms.userservice.entities.User;
import com.pms.userservice.exceptions.ResourceNotFound;
import com.pms.userservice.repositories.SessionRepository;
import com.pms.userservice.services.SessionService;
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
    private final  static Integer  SESSION_LIMIT = 1;

    @Override
    public void generateNewSession(User user, String refreshToken) {
        /* get the all active session of the user */
        List<Session> userSessions = sessionRepository.findByUser(user);
        /* if session limit full then delete the lase recent session */
        if ( userSessions.size() == SESSION_LIMIT) {
            userSessions.sort(Comparator.comparing(Session::getLastUsedBy));
            Session lastRecentSession = userSessions.getFirst();
            sessionRepository.delete(lastRecentSession);
        }

        /* creating the new Session */
        Session session = Session.builder()
                .user(user)
                .refreshToken(refreshToken)
                .build();
        /* persist the new session into the database */
        sessionRepository.save(session);
    }

    @Override
    public boolean validateSession(String refreshToken) {
        Optional<Session> optionalSession = sessionRepository.findByRefreshToken(refreshToken);
        return  optionalSession.isPresent() ;
    }

    @Override
    public void deleteSession(String refreshToken) {
        Session session = sessionRepository.findByRefreshToken(refreshToken).orElseThrow(()->new ResourceNotFound("No active  session found !"));
        sessionRepository.delete(session);
    }

}
