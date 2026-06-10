package com.latam.datagenerator.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UTIL-MAIL (BONUS): Clase encargada de leer la configuración de correo y enviar reportes CSV
 * adjuntos utilizando JavaMail API.
 */
public class MailSender {

    private static final Logger log = LoggerFactory.getLogger(MailSender.class);
    private Properties mailProperties;
    private boolean mailEnabled = false;

    public MailSender() {
        loadMailConfiguration();
    }

    /**
     * Carga las propiedades del archivo config.properties asociadas al correo electrónico.
     */
    private void loadMailConfiguration() {
        mailProperties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                mailProperties.load(input);
                String enabledStr = mailProperties.getProperty("mail.enabled", "false");
                mailEnabled = Boolean.parseBoolean(enabledStr);
                log.info("Configuración de correo cargada. mail.enabled={}", mailEnabled);
            } else {
                log.warn("No se pudo cargar config.properties para el módulo de correo.");
            }
        } catch (IOException e) {
            log.error("Error al leer el archivo de propiedades en el módulo de correo.", e);
        }
    }

    /**
     * Envía un correo con el archivo CSV adjunto.
     * 
     * @param csvFilePath Ruta local del archivo CSV generado.
     * @param recipientEmail Destinatario del correo.
     */
    public void sendCsvReport(String csvFilePath, String recipientEmail) {
        if (!mailEnabled) {
            log.info("Mail desactivado. Saltando envío de correo (mail.enabled=false).");
            return;
        }

        log.info("Preparando el envío del reporte de datos al correo: {}", recipientEmail);

        // Parámetros SMTP
        String host = mailProperties.getProperty("mail.smtp.host", "localhost");
        String port = mailProperties.getProperty("mail.smtp.port", "25");
        String authStr = mailProperties.getProperty("mail.smtp.auth", "false");
        String starttlsStr = mailProperties.getProperty("mail.smtp.starttls.enable", "false");
        final String username = mailProperties.getProperty("mail.username", "");
        final String password = mailProperties.getProperty("mail.password", "");

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", authStr);
        props.put("mail.smtp.starttls.enable", starttlsStr);

        Session session;
        if (Boolean.parseBoolean(authStr)) {
            session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        } else {
            session = Session.getInstance(props);
        }

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username.isEmpty() ? "datagenerator@latam.com" : username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Reporte de Usuarios Ficticios Generados - Latam Airlines");

            // Cuerpo del mensaje
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText("Hola,\n\nAdjunto al presente correo encontrarás el reporte en formato CSV "
                    + "con los datos generados para las pruebas automatizadas.\n\nSaludos,\nData Generator System");

            // Adjunto del CSV
            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(csvFilePath);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName("datos_latam.csv");

            // Agrupar partes
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            log.info("Enviando correo vía SMTP a {}...", host);
            Transport.send(message);
            log.info("¡Correo enviado con éxito a {}!", recipientEmail);

        } catch (MessagingException e) {
            log.error("Error al estructurar o enviar el correo electrónico.", e);
            throw new RuntimeException("Fallo en el envío de correo electrónico.", e);
        }
    }
}
