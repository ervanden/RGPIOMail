package rgpiomail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RGPIOMail {

    public static void main(String[] args) throws IOException {

        GMailSender gmailSender = new GMailSender();

        String arg_to = "evandenmeersch@sipef.com";
        String arg_subject = "mail from PI";
        TimeStamp now = new TimeStamp();
        String arg_content = "Temperature in Medan on " + now.asLongString() + " : " + "29°";
        String arg_mailfile = null;

        for (int arg = 0; arg <= args.length - 1; arg++) {
            String[] s = args[arg].split("=");
            if (s[0].equals("to")) {
                arg_to = s[1];
            } else if (s[0].equals("subject")) {
                arg_subject = s[1];
            } else if (s[0].equals("content")) {
                arg_content = s[1];
            } else if (s[0].equals("mailfile")) {
                arg_mailfile = s[1];
            } else {
                System.out.println("unknown argument " + s[0]);
            }
        }

        if (arg_mailfile != null) {
            // open the file and read to,subject and content from it
            try {
                System.out.println("Opening " + arg_mailfile);
                File file = new File(arg_mailfile);
                InputStream is = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader inputStream = new BufferedReader(isr);

                arg_to = inputStream.readLine();
                arg_subject = inputStream.readLine();
                arg_content = inputStream.readLine();

                inputStream.close();
            } catch (FileNotFoundException fnf) {
                System.out.println("file not found");
            } catch (IOException io) {
                System.out.println("io exception");
            }

        }

        System.out.println("to: " + arg_to);
        System.out.println("subject: " + arg_subject);
        System.out.println("content: " + arg_content);

        try {
            gmailSender.sendMessage(
                    "me",
                    gmailSender.createEmail(
                            arg_to,
                            "me@gmail.com", // ignored anyway
                            arg_subject,
                            arg_content
                    ));
        } catch (Exception e) {
            e.printStackTrace();
        };

    }

}
