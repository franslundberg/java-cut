package cut;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Exception handler that prints exception to System.err and to 
 * the file exception.txt, then exits.
 * Install it with 
 * <code>
 *   Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
 * </code>
 * 
 * @author Frans Lundberg
 */
public class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {
    
    private static final SimpleDateFormat DATE_FORMAT;

    static {
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    
    public void uncaughtException(Thread thread, Throwable exception) {
        //
        // This method must absolutely not throw exceptions.
        //

        Date date = new Date(System.currentTimeMillis());
        String timeString = DATE_FORMAT.format(date) + " UTC";

        StringBuilder b = new StringBuilder();
        Throwable cause = exception.getCause();

        b.append("\n").append("==== UNCAUGHT EXCEPTION ====").append("\n").
                append("time: ").
                append(timeString).append("\n").
                append("thread: ").
                append(thread.getId()).
                append(", ").
                append(thread.getName()).append("\n").
                append("exception type: ").
                append(exception.getClass().getName()).append("\n").
                append("exception message: ").
                append(exception.getMessage()).append("\n");

        if (cause != null) {
            b.append("cause type: ").
                    append(cause.getClass().getName()).append("\n").
                    append("cause message: ").
                    append(cause.getClass().getName()).append("\n");
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        pw.close();

        b.append("trace: ").append(sw.toString());

        String message = b.toString();

        System.err.print(message);

        byte[] messageBytes;
        try {
            messageBytes = message.getBytes("UTF-8");
        } catch (Exception ignored) {
            messageBytes = message.getBytes();
        }

        String filename = "exception.txt";
        try {
            Files.write(Paths.get(filename), messageBytes,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Could not write error text to " + filename + ", " + e.getMessage());
        }

        System.exit(-1);
    }
}
