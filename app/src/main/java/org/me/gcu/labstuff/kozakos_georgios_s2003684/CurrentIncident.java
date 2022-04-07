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

public class CurrentIncident {
    String title;
    String description;
    String link;
    String coords;
    String pubDate;

    public CurrentIncident() {
        this.title = "";
        this.description = "";
        this.link = "";
        this.coords = "";
        this.pubDate = "";
    }

    public CurrentIncident(String title, String description, String link, String coords, String pubDate) {
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

    public String[] getCoordsArray(){
        String coords = this.coords;
        String[] bothCoords = coords.split(" ");
        return bothCoords;
    }

    @Override
    public String toString() {
        return title + "\r\n" + pubDate;
    }
}