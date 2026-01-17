package com.iut.banque.test.modele;

import static org.junit.Assert.*;

import org.junit.Test;

import com.iut.banque.controller.HashMotDePasse;
import com.iut.banque.controller.HashMotDePasse.HashResult;
import com.iut.banque.modele.Client;

public class TestsUtilisateur {

    @Test
    public void testSetAndGetHashedPassword() throws Exception {
        Client c = new Client();
        // initial userPwd null
        assertFalse(c.isPasswordHashed());

        HashMotDePasse hasher = new HashMotDePasse();
        HashResult hr = hasher.hashPassword("secret123");
        c.setHashedPassword(hr.saltBase64, hr.hashBase64);

        assertTrue(c.isPasswordHashed());
        assertEquals(hr.saltBase64, c.getPasswordSalt());
        assertEquals(hr.hashBase64, c.getPasswordHash());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNumeroClientNullThrows() throws Exception {
        Client c = new Client();
        c.setNumeroClient(null);
    }

    @Test
    public void testCheckFormatNumeroClient() {
        assertTrue(Client.checkFormatNumeroClient("0123456789"));
        assertFalse(Client.checkFormatNumeroClient("123"));
    }
}

