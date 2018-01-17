package rgpiomail;

import java.io.IOException;

public class RGPIOMail {

    public static void main(String[] args) throws IOException {

        GMailSender gmailSender = new GMailSender();

        String arg_to = "evandenmeersch@sipef.com";
        String arg_subject = "mail from PI";
        TimeStamp now = new TimeStamp();
        String arg_content = "Temperature in Medan on " + now.asLongString() + " : " + "29°";

        for (int arg = 0; arg <= args.length - 1; arg++) {
            String[] s = args[arg].split("=");
            if (s[0].equals("to")) {
                arg_to = s[1];
            } else if (s[0].equals("subject")) {
                arg_subject = s[1];
            } else if (s[0].equals("content")) {
                arg_content = s[1];
            } else {
                System.out.println("unknown argument " + s[0]);
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
                            "me@gmail.com",  // ignored anyway
                            arg_subject,
                            arg_content
                    ));
        } catch (Exception e) {
            e.printStackTrace();
        };

    }

}
