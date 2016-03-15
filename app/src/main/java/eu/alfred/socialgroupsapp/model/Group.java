package eu.alfred.socialgroupsapp.model;

import java.io.Serializable;

public class Group implements Serializable {

    private String subject;
    private String description;
    private int numberOfMembers;

    public String getSubject() { return subject; }

    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public int getNumberOfMembers() { return numberOfMembers; }

    public void setNumberOfMembers(int numberOfMembers) { this.numberOfMembers = numberOfMembers; }
}
