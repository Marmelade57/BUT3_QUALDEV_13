package com.iut.banque.test.util;

/**
 * Détecte si les tests sont exécutés avec un agent de couverture IntelliJ/Jacoco.
 */
public final class CoverageDetector {
    private static final boolean ACTIVE = computeCoverage();

    private CoverageDetector() {
        // Utilitaire
    }

    private static boolean computeCoverage() {
        // IntelliJ coverage runner renseigne ce flag.
        if (Boolean.getBoolean("idea.is.coverage.enabled")) {
            return true;
        }
        // Certains runners ajoutent cette propriété avec le nom du runner.
        if (System.getProperty("idea.coverage.runner") != null) {
            return true;
        }
        // Fallback Jacoco (utilisé par Maven Surefire/Failsafe & IntelliJ)
        return System.getProperty("jacoco-agent.destfile") != null;
    }

    public static boolean isActive() {
        return ACTIVE;
    }
}

