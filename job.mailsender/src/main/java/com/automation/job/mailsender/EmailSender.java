package com.automation.job.mailsender;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EmailSender {
	public void mail_sender(String receiver, String subject, String core_message) throws IOException {

		/*
		 * Here, the code reads our gmail app password from a local text file rather
		 * than hardcoding it into the source. The Files.readString method retrieves the
		 * file content as a string, and trim() removes any extra whitespace that could
		 * cause authentication errors. This approach is safer and keeps sensitive
		 * information like passwords out of the source code.
		 */
		String pathToken = "C:\\Users\\touhafi\\eclipse-workspace\\gmailToken.txt";
		String senderAppPassword = Files.readString(Paths.get(pathToken)).trim();

		
		/* This is the email address we will use to send emails. */
		final String senderEmail = "mr.mouadthf@gmail.com";

		
		/*
		 * This part sets up the configuration properties required to connect to Gmail’s
		 * SMTP server. - It enables authentication with mail.smtp.auth, meaning the
		 * server will check our email and password. - The mail.smtp.starttls.enable
		 * turns on TLS encryption for secure transmission. - The mail.smtp.host is set
		 * to Gmail’s SMTP server. - The mail.smtp.port is 587, the standard port for
		 * TLS-secured email. These settings are essential for establishing a secure
		 * connection to Gmail.
		 */
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		
		/*
		 * This block creates a Session object that represents a mail session with
		 * Gmail. The Authenticator subclass overrides getPasswordAuthentication() to
		 * provide the sender’s email and app password automatically when requested by
		 * the SMTP server. This ensures that Gmail can verify your identity before
		 * allowing the email to be sent. Without this authentication step, Gmail would
		 * reject the connection.
		 */
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(senderEmail, senderAppPassword);
			}
		});

		
		try {
			
			/*
			 * Here, a new MimeMessage object is created, which allows the email to include both text and attachments. 
			 * The sender’s email is set using setFrom, and the recipient(s) are specified with setRecipients, 
			 * parsing the string from the method argument. 
			 * The subject line is set from the subject parameter. 
			 * This prepares the basic structure of the email, including the sender, recipient, and subject.
			 * */
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(senderEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
			message.setSubject(subject);


			/*
			 * Emails that contain both text and attachments must use a Multipart object. 
			 * In this part, a MimeMultipart object is created to hold multiple components of the email. 
			 * A MimeBodyPart is then used to hold the main text content (core_message). 
			 * Adding this body part to the multipart ensures that the email has a readable message in addition to 
			 * any attached files.
			 * */
			Multipart multipart = new MimeMultipart();
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(core_message);
			multipart.addBodyPart(textPart);

			
			/*
			 * This section attaches files to the email. 
			 * An array of file paths is defined, and for each path, a new MimeBodyPart is created. 
			 * The attachFile method attaches the physical file to that body part. 
			 * Each attachment is then added to the multipart object. This allows the email to carry multiple files, 
			 * in this case, a CV and a motivation letter, alongside the text message.
			 * */
			String[] filePaths = { "C:\\Users\\touhafi\\Downloads\\TOUHAFI-Mouad-CV-2025.pdf",
					"C:\\Users\\touhafi\\Downloads\\lettre_motivation.pdf" };

			for (String filePath : filePaths) {
				MimeBodyPart attachmentPart = new MimeBodyPart();
				attachmentPart.attachFile(new File(filePath));
				multipart.addBodyPart(attachmentPart);
			}

			/*
			 * After creating the multipart object with the text and attachments, 
			 * this line sets the content of the MimeMessage to be the multipart object. 
			 * This tells JavaMail that the email is composed of multiple parts, including the body and attachments, 
			 * so that the email client can properly display the message and attachments.
			 * */
			message.setContent(multipart);

			/*
			 * This line actually sends the email through the Gmail SMTP server. 
			 * Transport.send handles the network connection, authentication, and delivery. 
			 * If the email is sent successfully, a confirmation message is printed to the console.
			 * */
			Transport.send(message);
			System.out.println("Email with attachments sent successfully!");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
