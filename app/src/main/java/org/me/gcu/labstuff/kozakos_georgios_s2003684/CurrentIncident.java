// Name: Georgios Kozakos   Matric Number: S2003684

package org.me.gcu.labstuff.kozakos_georgios_s2003684;

import androidx.annotation.NonNull;

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

    public void setCoords(String coords) {
        this.coords = coords;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String[] getCoordsArray() {
        String coords = this.coords;
        return coords.split(" ");
    }

    @NonNull
    @Override
    public String toString() {
        return title + "\r\n" + pubDate;
    }
}
