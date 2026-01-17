package com.iut.banque.test.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.controller.DetailCompte;
import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.opensymphony.xwork2.ActionContext;

import java.util.HashMap;
import java.util.Map;

public class TestsDetailCompte {
    private DetailCompte action;
    private BanqueFacade facade;

    @Before
    public void setUp() {
        facade = mock(BanqueFacade.class);
        action = new DetailCompte(facade);
    }

}
