package com.aspectsense.fuel.server.admin;

import com.aspectsense.fuel.server.data.Parameter;
import com.aspectsense.fuel.server.datastore.ParameterFactory;
import com.aspectsense.fuel.server.datastore.SyncMessageFactory;

import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

import static com.aspectsense.fuel.server.json.Util.SIMPLE_DATE_FORMAT;

/**
 * @author Nearchos Paspallis
 *         03/02/2017
 *         22:02
 */
public class WeeklyReportServlet extends HttpServlet {

    public static final Logger log = Logger.getLogger(WeeklyReportServlet.class.getCanonicalName());

    public static final long MILLISECONDS_IN_A_DAY = 24L * 60 * 60 * 1000L;
    public static final long MILLISECONDS_IN_A_WEEK = 7L * MILLISECONDS_IN_A_DAY;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        final PrintWriter printWriter = response.getWriter();

        // check if daily reports are turned on
        final Parameter parameterWeeklyReports = ParameterFactory.getParameterByName("WEEKLY_REPORT");
        if(parameterWeeklyReports != null && !parameterWeeklyReports.getValueAsBoolean()) {
            printWriter.println("Weekly reports are not set (add parameter 'WEEKLY_REPORT' and set it to 'true'");
            return;
        }

        // point to most recent Monday 00:00 (i.e. the beginning of this week)
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Nicosia"));
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        final long to = calendar.getTimeInMillis();
        final String toS = SIMPLE_DATE_FORMAT.format(new Date(to));
        final long from = to - MILLISECONDS_IN_A_WEEK;
        final String fromS = SIMPLE_DATE_FORMAT.format(new Date(from));

        // parameter-ized deletion flag (controlled in '/admin/parameters' with parameter 'WEEKLY_REPORT_DELETE'
        final Parameter parameter = ParameterFactory.getParameterByName("WEEKLY_REPORT_DELETE");
        final boolean delete = parameter != null && parameter.getValueAsBoolean();

        final long start = System.currentTimeMillis();
        final Map<Long, String> syncMessages = SyncMessageFactory.querySyncMessage(from, to, delete);
        final int numOfSyncMessages = syncMessages.size();
        final double durationInSeconds = (System.currentTimeMillis() - start) / 1000d;

        sendEmail(fromS, toS, syncMessages, delete, durationInSeconds);

        printWriter.println("Sent " + numOfSyncMessages + " sync messages!");
    }

    public static final String REPORT_RECEIVER_EMAIL = "nearchos@aspectsense.com";
    public static final String REPORT_RECEIVER_NAME  = "Nearchos Paspallis";

    private void sendEmail(final String fromDate, final String toDate, Map<Long, String> attachments, final boolean deleted, final double durationInSeconds)
    {
        final String messageText =
                "Weekly report by Cyprus Fuel Guide\n" +
                        "From: " + fromDate + " (exclusive)\n" +
                        "To: " + toDate + " (inclusive)\n" +
                        "# of SyncMessages: " + attachments.size() + "\n" +
                        "Duration: " + durationInSeconds + " seconds\n" +
                        "Deleted: " + deleted + "\n\n";

        final Session session = Session.getDefaultInstance(new Properties(), null);
        try
        {
            final Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("nearchos@gmail.com", "Cyprus Fuel Guide", "utf-8"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(REPORT_RECEIVER_EMAIL, REPORT_RECEIVER_NAME));
            message.setSubject("Cyprus Fuel Guide - Weekly report ending " + toDate + " (" + attachments.size() + " SyncMessages)");
            final Multipart multipart = new MimeMultipart();

            // the message part ...
            final BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(messageText, "text/plain");
            multipart.addBodyPart(messageBodyPart);

            // ... and the attachment parts
            for(final Map.Entry<Long,String> attachment : attachments.entrySet()) {
                final String toDateS = SIMPLE_DATE_FORMAT.format(new Date(attachment.getKey()));
                final MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                attachmentBodyPart.setFileName(toDateS + "-cfg-report-" + attachment.getKey() + ".json");
                attachmentBodyPart.setContent(attachment.getValue(), "application/json");
                attachmentBodyPart.setContentLanguage(new String[] {"en", "el"});
                multipart.addBodyPart(attachmentBodyPart);
            }

            message.setContent(multipart);
            Transport.send(message);
        }
        catch (AddressException ae)
        {
            log.severe(ae.getMessage());
        }
        catch (MessagingException me)
        {
            log.severe(me.getMessage());
        }
        catch (UnsupportedEncodingException uee)
        {
            log.severe(uee.getMessage());
        }
    }
}