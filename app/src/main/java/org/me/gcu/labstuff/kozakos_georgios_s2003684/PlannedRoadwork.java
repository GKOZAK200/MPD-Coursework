package org.me.gcu.labstuff.kozakos_georgios_s2003684;

import java.util.Scanner;
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

    public void getStartDate(){
        Pattern pattern = Pattern.compile(", (.*?) - ");
        Matcher matcher = pattern.matcher(this.description);
        if (matcher.find())
        {
            System.out.println(matcher.group(1));
        }
    }

    public void getStartTime(){
        Pattern pattern = Pattern.compile(" - (.*?)<br />");
        Matcher matcher = pattern.matcher(this.description);
        if (matcher.find())
        {
            System.out.println(matcher.group(1));
        }
    }

    public void getEndDate(){
        Pattern pattern = Pattern.compile("End Date: (.*?), (.*?) -");
        Matcher matcher = pattern.matcher(this.description);
        if (matcher.find())
        {
            System.out.println(matcher.group(2));
        }
    }

    public void getEndTime(){
        Pattern pattern = Pattern.compile("End Date: (.*?), (.*?) - (.*?)<br />");
        Matcher matcher = pattern.matcher(this.description);
        if (matcher.find())
        {
            System.out.println(matcher.group(3));
        }
    }

    @Override
    public String toString() {
        return title + "\r\n" + pubDate;
    }
}
