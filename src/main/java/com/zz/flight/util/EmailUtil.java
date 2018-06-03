package com.zz.flight.util;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {
    public static boolean sendEmail(String from,String to,String content,String subject){

        String smtp = "smtp.gmail.com";// 设置邮件服务器
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host",smtp);
        props.put("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.port", "587");
        props.setProperty("mail.debug","true");
        props.put("mail.smtp.auth", "true");

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("flihtjoe@gmail.com","zhengzhuo");
            }
        };
        Session session = Session.getDefaultInstance(props,authenticator);

        try{

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));
            //message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
            message.setSubject(subject);
            message.setContent(content,"text/html;charset=UTF-8");
            Transport.send(message);
        }catch (Exception e) {
            return false;
        }
        return true;
    }
}
