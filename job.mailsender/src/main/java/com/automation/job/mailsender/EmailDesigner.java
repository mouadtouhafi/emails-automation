package com.automation.job.mailsender;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class EmailDesigner {
	public void design() throws Exception {

		ClassLoader classLoader = EmailGenerator.class.getClassLoader();
        String fileContent = new String(Files.readAllBytes(Paths.get(classLoader.getResource("job_links.txt").toURI())));
        
        String[] splitedContent = fileContent.split("\\\n");
        for(String link : splitedContent) {
        	link = link.strip();
        	NavigateToLink navigateToLink = new NavigateToLink();  	
        	String post = navigateToLink.navigate_to_link(link);
        	
        	EmailGenerator emailGenerator = new EmailGenerator();
        	List<Map<String, String>> generatedEmails = emailGenerator.email_generator(post);
        	System.out.println("Number of emails to send is : " + generatedEmails.size());
        	
        	for(Map<String, String> email : generatedEmails) {
        		Thread.sleep(500);
        		String receiver_email = email.get("receiver_email");
        		String subject = email.get("subject");
        		String message = email.get("message");
        		
        		/* Printing values for debugging purposes */
        		System.out.println("receiver_email  :  " + receiver_email);
        		System.out.println("subject  :  " + subject);
        		System.out.println("message  :  " + message.replace("\n", ""));
        		
        		if(receiver_email != "null") {
        			EmailSender emailSender = new EmailSender();
        			emailSender.mail_sender(receiver_email, subject, message);
        		}else {
        			System.out.println("email not found");
        		}
        	}
        }
	}
}
