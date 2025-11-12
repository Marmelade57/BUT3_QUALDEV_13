package com.iut.banque.test.controller;

import com.iut.banque.controller.HashMotDePasse;
import com.iut.banque.exceptions.TechnicalException;
import org.junit.Test;

import java.util.Base64;

import static org.junit.Assert.*;

public class TestsHashMotDePasse extends HashMotDePasse {

    @Test
    public void testHashLength() throws TechnicalException {
        HashResult result = hashPassword("Test1234");
        byte[] hashBytes = Base64.getDecoder().decode(result.hashBase64);
        assertEquals("La taille du hash doit être de 16 bits", 16, hashBytes.length);
    }

    @Test
    public void testSaltLength() throws TechnicalException {
        HashResult result = hashPassword("Test1234");
        byte[] saltBytes = Base64.getDecoder().decode(result.saltBase64);
        assertEquals("La taille du sel doit être de 16 bits", 16, saltBytes.length);
    }

    @Test(expected = TechnicalException.class)
    public void testNullPasswordThrowsException() throws TechnicalException {
        hashPassword(null);
    }

    @Test(expected = TechnicalException.class)
    public void testEmptyPasswordThrowsException() throws TechnicalException {
        hashPassword("");
    }

    @Test
    public void testHashUniquenessWithDifferentSalts() throws TechnicalException {
        HashResult result1 = hashPassword("SamePassword");
        HashResult result2 = hashPassword("SamePassword");

        assertNotEquals("Les sels devraient être différent", result1.saltBase64, result2.saltBase64);
        assertNotEquals("Les hashs devraient être différent", result1.hashBase64, result2.hashBase64);
    }
}
