package eu.alfred.socialgroupsapp.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Group implements Serializable {

    private String creationDate;
    private String lastUpdated;
    private String description;
    private String name;
    private String userID;
    private String[] memberIds;

    public Group(String description, String name, String userID) {
        TimeZone tz = TimeZone.getDefault();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(tz);
        Date now = new Date();
        this.creationDate = sdf.format(now);
        this.lastUpdated = sdf.format(now);
        this.description = description;
        this.name = name;
        this.userID = userID;
        this.memberIds = new String[]{userID} ;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreationDate() { return creationDate; }
    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String[] getMemberIds() { return memberIds; }

    public void setMemberIds(String[] memberIds) { this.memberIds = memberIds; }

    public String getLastUpdated() { return lastUpdated; }

    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

}
