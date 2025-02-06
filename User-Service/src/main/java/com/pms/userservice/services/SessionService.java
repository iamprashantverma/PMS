package com.pms.userservice.services;

import com.pms.userservice.entities.User;

public interface SessionService {
    /* Generate the new Session on each login*/
    void generateNewSession(User user, String refreshToken);

    /* validate the Session for each subsequent request*/
    boolean validateSession(String refreshToken) ;

    /* delete the session on logout request */
    void deleteSession(String refreshToken);

}
