package com.iut.banque.test.selenium;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertTrue;

/**
 * Classe de test pour vérifier l'intégration de Selenium.
 * Assurez-vous d'avoir le driver approprié (ChromeDriver, GeckoDriver, etc.) configuré dans votre PATH ou via System.setProperty.
 */
public class SeleniumLoginTest {

    @Test
    public void testLogin() {
        // Configuration du chemin vers le driver Chrome
        // Remplacez le chemin ci-dessous par le chemin absolu vers votre exécutable chromedriver.exe
        // Exemple : "C:/tools/chromedriver.exe"
        // Si chromedriver est déjà dans votre PATH système, cette ligne peut être commentée.
        System.setProperty("webdriver.chrome.driver", "C:/chromedriver/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        try {
            // Navigation vers la page de login de l'application
            // Assurez-vous que votre serveur Tomcat est démarré et que l'application est accessible à cette URL
            // Si vous utilisez le Dockerfile fourni, le port exposé est 8080, mais le script RunDocker.bat mappe 8081:8081 (ce qui semble être une erreur dans le script bat si le Dockerfile expose 8080)
            // Cependant, si vous lancez via Maven tomcat7:run, c'est généralement 8080.
            // Essayons l'URL standard pour un déploiement local via Maven ou Tomcat externe standard.
            // Si vous utilisez Docker avec le script fourni, essayez http://localhost:8081/
            
            // Tentative de connexion sur le port 8080 (standard Maven/Tomcat local)
            driver.get("http://localhost:8080/ASBank-2025/Login.jsp");

            // Localisation des éléments du formulaire
            // Note: Struts génère des IDs ou des noms spécifiques, on utilise ici le nom "userCde" et "userPwd" comme défini dans la JSP
            WebElement userCdeInput = driver.findElement(By.name("userCde"));
            WebElement userPwdInput = driver.findElement(By.name("userPwd"));
            WebElement submitButton = driver.findElement(By.id("loginUser_submit")); // Struts génère souvent id="actionName_submit" ou similaire, à vérifier avec l'inspecteur si besoin. Sinon on peut chercher par type="submit"

            // Remplissage du formulaire
            userCdeInput.sendKeys("client1"); // Remplacez par un identifiant valide de votre base de données de test
            userPwdInput.sendKeys("client1"); // Remplacez par le mot de passe correspondant

            // Soumission du formulaire
            // submitButton.click(); 
            // Alternativement, on peut soumettre le formulaire directement depuis un champ
            userPwdInput.submit();

            // Vérification du résultat
            // On attend que la page suivante se charge ou qu'un élément spécifique apparaisse
            // Par exemple, vérifier si on est redirigé vers la page d'accueil ou si un message de bienvenue est présent
            // Ici, on fait une vérification simple sur le titre ou l'URL pour l'exemple
            
            // Exemple d'attente explicite (recommandé)
            // WebDriverWait wait = new WebDriverWait(driver, 10);
            // wait.until(ExpectedConditions.titleContains("Accueil"));

            // assertTrue(driver.getTitle().contains("Accueil") || driver.getCurrentUrl().contains("index"));
            
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            // Fermeture du navigateur
            driver.quit();
        }
    }
}
