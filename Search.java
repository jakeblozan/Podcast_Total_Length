import java.io.*;
import java.util.*;
import java.net.*;
import java.text.DecimalFormat;

/**
 * Scans podcast RSS feeds for episode durations and sums them.
 *
 * @author (Jake Blozan)
 * @version (11/22/19)
 */
public class Search
{
    private static DecimalFormat df = new DecimalFormat("0.00");

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);

        System.out.println("Welcome! Please enter the url for the RSS feed you would like to crawl:");
        boolean valid = false;
        URL url = null;
        while (valid == false) {
            String input = scan.nextLine();
            if (isValid(input)) {
                url = new URL(input);
                valid = true;
            }
            else {
                System.out.println("Sorry, that isn't a valid URL. Try again: ");
            }
        }

        URLConnection con = url.openConnection();
        InputStream is = con.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        boolean title_got = false;
        String title = null;
        String sum = "00:00:00";
        String line = null;
        while ((line = br.readLine()) != null) {
            if (title_got == false) {
                int name_idx = line.indexOf("title");
                if (name_idx != -1) {
                    title = line.substring(name_idx + 6, line.indexOf("<",name_idx));
                    title_got = true;
                }
            }

            int dur_idx = line.indexOf("itunes:duration");
            if (dur_idx != -1 && line.chars().filter(ch -> ch == ':').count() >= 3) {
                String duration = line.substring(line.indexOf(":", dur_idx + 8) - 2, line.indexOf("<",dur_idx));
                sum = addTime(sum, duration);
                //System.out.println(sum);
            }
        }
        double sum_hours = Integer.parseInt(sum.substring(0,sum.indexOf(":"))) + Integer.parseInt(sum.substring(sum.indexOf(":") + 1,sum.lastIndexOf(":")))/60.0 + Integer.parseInt(sum.substring(sum.lastIndexOf(":") + 1,sum.length()))/3600.0;
        int sum_minutes = Integer.parseInt(sum.substring(0,sum.indexOf(":")))*60 + Integer.parseInt(sum.substring(sum.indexOf(":") + 1,sum.lastIndexOf(":"))) + Integer.parseInt(sum.substring(sum.lastIndexOf(":") + 1,sum.length()))/60;
        int sum_seconds = Integer.parseInt(sum.substring(0,sum.indexOf(":")))*3600 + Integer.parseInt(sum.substring(sum.indexOf(":") + 1,sum.lastIndexOf(":")))*60 + Integer.parseInt(sum.substring(sum.lastIndexOf(":") + 1,sum.length()));
        System.out.println("The total length of " + title + " is:");
        System.out.println(sum + " or");
        System.out.println(df.format(sum_hours) + " hours or");
        System.out.println(Integer.toString(sum_minutes) + " minutes or");
        System.out.println(Integer.toString(sum_seconds) + " seconds");
    }

    public static String addTime(String time1, String time2) {
        String sum = null;
        if (time2.length() == 8) {
            int hour1 = Integer.parseInt(time1.substring(0,time1.indexOf(":")));
            int hour2 = Integer.parseInt(time2.substring(0,time2.indexOf(":")));
            int minute1 = Integer.parseInt(time1.substring(time1.indexOf(":") + 1,time1.lastIndexOf(":")));
            int minute2 = Integer.parseInt(time2.substring(time2.indexOf(":") + 1,time2.lastIndexOf(":")));
            int second1 = Integer.parseInt(time1.substring(time1.lastIndexOf(":") + 1,time1.length()));
            int second2 = Integer.parseInt(time2.substring(time2.lastIndexOf(":") + 1,time2.length()));
            int totalHours = hour1 + hour2;
            int totalMinutes = minute1 + minute2;
            int totalSeconds = second1 + second2;
            if (totalSeconds >= 60) {
                totalMinutes ++;
                totalSeconds = totalSeconds % 60;
            }
            if (totalMinutes >= 60) {
                totalHours ++;
                totalMinutes = totalMinutes % 60;
            }
            sum = String.format("%02d",totalHours) + ":" + String.format("%02d",totalMinutes) + ":" + String.format("%02d",totalSeconds);
        }
        else if (time2.length() == 5) {
            int hour1 = Integer.parseInt(time1.substring(0,time1.indexOf(":")));
            int minute1 = Integer.parseInt(time1.substring(time1.indexOf(":") + 1,time1.lastIndexOf(":")));
            int minute2 = Integer.parseInt(time2.substring(0,time2.indexOf(":")));
            int second1 = Integer.parseInt(time1.substring(time1.lastIndexOf(":") + 1,time1.length()));
            int second2 = Integer.parseInt(time2.substring(time2.indexOf(":") + 1,time2.length()));
            int totalHours = hour1;
            int totalMinutes = minute1 + minute2;
            int totalSeconds = second1 + second2;
            if (totalSeconds >= 60) {
                totalMinutes ++;
                totalSeconds = totalSeconds % 60;
            }
            if (totalMinutes >= 60) {
                totalHours ++;
                totalMinutes = totalMinutes % 60;
            }
            sum = String.format("%02d",totalHours) + ":" + String.format("%02d",totalMinutes) + ":" + String.format("%02d",totalSeconds);
        }

        return sum;
    }

    public static boolean isValid(String url) 
    { 
        // Try creating a valid URL
        try { 
            new URL(url).toURI(); 
            return true; 
        }
        // If there was an Exception 
        // while creating URL object 
        catch (Exception e) { 
            return false; 
        } 
    }
}
