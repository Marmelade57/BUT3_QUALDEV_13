package com.iut.banque.test.constants;

import com.iut.banque.constants.LoginConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour la classe LoginConstants.
 * Vérifie que toutes les constantes sont correctement définies.
 */
class TestsLoginConstants {

    @Test
    void testUserIsConnected() {
        assertEquals(1, LoginConstants.USER_IS_CONNECTED, 
            "USER_IS_CONNECTED doit valoir 1");
    }

    @Test
    void testManagerIsConnected() {
        assertEquals(2, LoginConstants.MANAGER_IS_CONNECTED, 
            "MANAGER_IS_CONNECTED doit valoir 2");
    }

    @Test
    void testLoginFailed() {
        assertEquals(-1, LoginConstants.LOGIN_FAILED, 
            "LOGIN_FAILED doit valoir -1");
    }

    @Test
    void testError() {
        assertEquals(-2, LoginConstants.ERROR, 
            "ERROR doit valoir -2");
    }

    @Test
    void testConstantsAreDifferent() {
        assertNotEquals(LoginConstants.USER_IS_CONNECTED, 
            LoginConstants.MANAGER_IS_CONNECTED, 
            "Les constantes doivent avoir des valeurs différentes");
        assertNotEquals(LoginConstants.LOGIN_FAILED, 
            LoginConstants.ERROR, 
            "Les constantes d'erreur doivent avoir des valeurs différentes");
    }
}

