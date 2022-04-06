package org.me.gcu.labstuff.kozakos_georgios_s2003684;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlannedRoadwork {
    String title;
    String description;
    String link;
    String coords;
    String pubDate;

    public PlannedRoadwork() {
        this.title = "";
        this.description = "";
        this.link = "";
        this.coords = "";
        this.pubDate = "";
    }

    public PlannedRoadwork(String title, String description, String link, String coords, String pubDate) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.coords = coords;
        this.pubDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCoords() {
        return coords;
    }

    public void setCoords(String coords) {
        this.coords = coords;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getStartDate(){
        Pattern pattern = Pattern.compile(", (.*?) - ");
        Matcher matcher = pattern.matcher(this.description);
        if (matcher.find())
        {
            return matcher.group(1);
        }
        else return "N/A";
    }

    public String getStartTime(){
        Pattern pattern = Pattern.compile(" - (.*?)<br />");
        Matcher matcher = pattern.matcher(this.description);
        if (matcher.find())
        {
            return matcher.group(1);
        }
        else return "N/A";
    }

    public String getEndDate(){
        Pattern pattern = Pattern.compile("End Date: (.*?), (.*?) -");
        Matcher matcher = pattern.matcher(this.description);
        if (matcher.find())
        {
            return matcher.group(2);
        }
        else return "N/A";
    }

    public String getEndTime(){
        Pattern pattern = Pattern.compile("End Date: (.*?), (.*?) - (.*?)<br />");
        Matcher matcher = pattern.matcher(this.description);
        if (matcher.find())
        {
            return matcher.group(3);
        }
        else return "N/A";
    }

    public String[] getCoordsArray(){
        String coords = this.coords;
        String[] bothCoords = coords.split(" ");
        return bothCoords;
    }

    public long getDays() {
        SimpleDateFormat sdf=new SimpleDateFormat("dd MMMM yyyy");
        String date1 = this.getStartDate();
        String date2 = this.getEndDate();
        Date date1date = null;
        try {
            date1date = sdf.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2date = null;
        try {
            date2date = sdf.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long daysBetween = date2date.getTime() - date1date.getTime();
        return TimeUnit.DAYS.convert(daysBetween, TimeUnit.MILLISECONDS);
    }

    public List<Date> getDates() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        String date1 = this.getStartDate();
        String date2 = this.getEndDate();
        Date startDate = null;
        try {
            startDate = sdf.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endDate = null;
        try {
            endDate = sdf.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Date> dates = new ArrayList<Date>();
        long interval = 24 * 1000 * 60 * 60;
        long endTime = endDate.getTime();
        long curTime = startDate.getTime();
        while (curTime <= endTime) {
            dates.add(new Date(curTime));
            curTime += interval;
        }
        return dates;
    }

    @Override
    public String toString() {
        return title + "\r\n" + pubDate;
    }
}
