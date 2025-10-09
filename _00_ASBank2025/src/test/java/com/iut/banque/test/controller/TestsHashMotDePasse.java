package com.iut.banque.test.controller;

import com.iut.banque.controller.HashMotDePasse;
import com.iut.banque.controller.HashMotDePasse.HashResult;
import org.junit.Test;

import java.util.Base64;

import static org.junit.Assert.*;

public class TestsHashMotDePasse extends HashMotDePasse {

    @Test
    public void testHashLength() {
        HashResult result = hashPassword("Test1234");
        byte[] hashBytes = Base64.getDecoder().decode(result.hashBase64);
        assertEquals("La taille du hash doit être de 16 bits", 16, hashBytes.length);
    }

    @Test
    public void testSaltLength() {
        HashResult result = hashPassword("Test1234");
        byte[] saltBytes = Base64.getDecoder().decode(result.saltBase64);
        assertEquals("La taille du sel doit être de 16 bits", 16, saltBytes.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullPasswordThrowsException() {
        hashPassword(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPasswordThrowsException() {
        hashPassword("");
    }

    @Test
    public void testHashUniquenessWithDifferentSalts() {
        HashResult result1 = hashPassword("SamePassword");
        HashResult result2 = hashPassword("SamePassword");

        assertNotEquals("Les sels devraient être différent", result1.saltBase64, result2.saltBase64);
        assertNotEquals("Les hashs devraient être différent", result1.hashBase64, result2.hashBase64);
    }
}
