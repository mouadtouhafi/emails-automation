package com.automation.job.mailsender;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class NavigateToLink {
	
    public String navigate_to_link(String link) throws IOException, URISyntaxException {
    	
    	/*
    	 * Here, you create a new instance of EdgeDriver, which launches Microsoft Edge via Selenium. 
    	 * driver.get(...) navigates the browser to the specified URL link. 
    	 * This setup is essential because all subsequent Selenium operations (finding elements, clicking, extracting text) 
    	 * depend on the page being loaded.
    	 * */
        WebDriver driver = new EdgeDriver();
        driver.get(link);
        
        
        /*
         * This block handles any modal popup that may appear on the LinkedIn page. 
         * We create a WebDriverWait for up to 15 seconds, waiting for an element with the CSS 
         * classes .modal__dismiss.btn-tertiary to become visible. 
         * If it appears, Selenium clicks it to close the popup. 
         * The try-catch ensures that if the element doesn’t appear within 15 seconds, 
         * the program doesn’t crash but prints a message instead. 
         * This prevents modal popups from blocking the rest of the automation.
         * */
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        try {
            WebElement closePopupButton = wait.until(ExpectedConditions
                    .visibilityOfElementLocated(By.cssSelector(".modal__dismiss.btn-tertiary")));
            closePopupButton.click();
            System.out.println("popup found and closed!");
        } catch (Exception e) {
            System.out.println("Element not found within 15 seconds.");
        }

        
        
        /*
         * Here, Selenium waits until the main LinkedIn post text element becomes visible, 
         * using a CSS selector that targets multiple classes (including the escaped !text-sm). 
         * Once located, getText() extracts the visible text of the post into a String variable postContent.
         * */
        WebElement postContentElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
        		By.cssSelector(".attributed-text-segment-list__content.text-color-text.\\!text-sm.whitespace-pre-wrap.break-words")
        	));
        String postContent = postContentElement.getText();
        
        
        
        /*
         * Finally, driver.quit() closes the Edge browser and ends the Selenium session. 
         * This is important for freeing system resources and ensuring that no background browser processes remain running. 
         * Without this step, multiple runs of the program could leave orphaned browser instances open.
         * */
        driver.quit();
        
        
        return postContent;

    }
}
