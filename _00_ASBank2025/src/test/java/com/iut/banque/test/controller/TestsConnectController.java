package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.controller.Connect;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Utilisateur;
import com.iut.banque.constants.LoginConstants;

public class TestsConnectController {

    private Connect connect;
    private BanqueFacade facadeMock;

    @Before
    public void setUp() {
        facadeMock = mock(BanqueFacade.class);
        // Utiliser le constructeur de test pour injecter la facade
        connect = new Connect(facadeMock);
    }

    @Test
    public void testLoginUser() {
        connect.setUserCde("u1");
        connect.setUserPwd("pwd");
        when(facadeMock.tryLogin("u1","pwd")).thenReturn(LoginConstants.USER_IS_CONNECTED);
        String res = connect.login();
        assertEquals("SUCCESS", res);
    }

    @Test
    public void testLoginManager() {
        connect.setUserCde("m1");
        connect.setUserPwd("pwd");
        when(facadeMock.tryLogin("m1","pwd")).thenReturn(LoginConstants.MANAGER_IS_CONNECTED);
        String res = connect.login();
        assertEquals("SUCCESSMANAGER", res);
    }

    @Test
    public void testLoginFailed() {
        connect.setUserCde("x");
        connect.setUserPwd("pwd");
        when(facadeMock.tryLogin("x","pwd")).thenReturn(LoginConstants.LOGIN_FAILED);
        String res = connect.login();
        assertEquals("ERROR", res);
    }

    @Test
    public void testGetAccounts() {
        Client c = new Client();
        c.setAccounts(new HashMap<>());
        when(facadeMock.getConnectedUser()).thenReturn(c);
        assertNotNull(connect.getAccounts());
    }
}
