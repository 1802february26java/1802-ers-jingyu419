package com.revature.thread;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.apache.log4j.Logger;

import com.revature.util.EmailUtil;

public class EmailThread implements Runnable{

	private static Logger logger = Logger.getLogger(EmailThread.class);
	
	private String emailSubject;
	private String emailBody;
	private String emailAddress;
	
	public EmailThread(String subject, String body, String email){
		this.emailSubject = subject;
		this.emailBody = body;
		this.emailAddress = email;
	}
	
	public void run() {

		/**
		   Outgoing Mail (SMTP) Server
		   requires TLS or SSL: smtp.gmail.com (use authentication)
		   Use Authentication: Yes
		   Port for TLS/STARTTLS: 587
		 */
		final String fromEmail = "ersowner@gmail.com"; //requires valid gmail id
		final String password = "@123456789"; // correct password for gmail id
		//final String toEmail = "projectERSEmployeeA@gmail.com";  // can be any email id 
		
		logger.trace("ERS Email Start");
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
		props.put("mail.smtp.port", "587"); //TLS Port
		props.put("mail.smtp.auth", "true"); //enable authentication
		props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
		
                //create Authenticator object to pass in Session.getInstance argument
		Authenticator auth = new Authenticator() {
			//override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		};
		Session session = Session.getInstance(props, auth);
		
		EmailUtil.sendEmail(session, emailAddress,emailSubject,emailBody);
		
	}
	
	/*
	   public static void main(String[] args){
		   
		   String subject = "Employee Registration";
		   String body = "Employee Registration successfully";
		   String email = "projectERSEmployeeA@gmail.com";
		   
		      EmailThread runnableThread = new EmailThread(subject,body,email);
		        
		        Thread t = new Thread(runnableThread);
		        
		        t.start(); 
		        
		      
	   }
	 */
}
