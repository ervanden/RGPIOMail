package sendmail;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

class GMailSender {

    public GMailSender() {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
            readGmailAuthorizationKeyFile("C:\\Users\\erikv\\Documents\\RGPIO\\GmailAuthorizationKeys.txt");
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    static GmailAuthorizationKeyFileEntry GmailAuthorizationKeys;

    /**
     * Application name.
     */
    private final String APPLICATION_NAME
            = "Gmail API Java Quickstart";

    /**
     * Directory to store user credentials for this application.
     */
    private final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/gmail-java-quickstart");

    /**
     * Global instance of the {@link FileDataStoreFactory}.
     */
    private FileDataStoreFactory DATA_STORE_FACTORY;

    /**
     * Global instance of the JSON factory.
     */
    private final JsonFactory JSON_FACTORY
            = JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the HTTP transport.
     */
    private HttpTransport HTTP_TRANSPORT;

    /**
     * Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials at
     * ~/.credentials/gmail-java-quickstart
     */
    private final List<String> SCOPES
            = Arrays.asList(GmailScopes.GMAIL_SEND);

    {

    }

    public static void readGmailAuthorizationKeyFile(String fileName) {
        ArrayList<Object> l;
        l = JSON2Object.readJSONFile(fileName, GmailAuthorizationKeyFileEntry.class);
        if (l.size() != 1) {
            System.out.println("Expected 1 JSON object in " + fileName + ", found : " + l.size());
            System.exit(1);
        } else {
            Object o = l.get(0);
            GmailAuthorizationKeys = (GmailAuthorizationKeyFileEntry) o;
        }
        System.out.println("clientId " + GmailAuthorizationKeys.clientId);
        System.out.println("clientSecret " + GmailAuthorizationKeys.clientSecret);
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public Credential authorize() throws IOException {

        System.out.println(" data store dir " + DATA_STORE_DIR);

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow
                = new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY,
 //                       GmailAuthorizationKeys.clientId,
 //                       GmailAuthorizationKeys.clientSecret,
                                                "853358937100-etg296s8tvulf64pdin6l7gq4ih7o0os.apps.googleusercontent.com",
                                                "Zp8QvGgFHyR2iU8clI99Cw5G",
                        SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Gmail client service.
     *
     * @return an authorized Gmail client service
     * @throws IOException
     */
    public Gmail getGmailService() throws IOException {
        Credential credential = authorize();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public MimeMessage createEmail(String to,
            String from,
            String subject,
            String bodyText)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    public Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    public Message sendMessage(
            String userId,
            MimeMessage emailContent)
            throws MessagingException, IOException {
        
        Gmail service =getGmailService();
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        return message;
    }
}
