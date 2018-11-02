package com.example.acer.vitevents.WordClass;

/**
 * This custom Word Class for WorkShop events
 * This class gets id,title,venue,Date and time in millisecs,Organizer name, Contact number
 names of the club/chapter,Link to register,Description of the event,fees for the event,Poster availability
 * It also has functions that returns those values
 */

public class WorkshopWord {

    private String id;
    private String Title;
    private String Venue;
    private String Millisec;
    private String Org;
    private String Club;
    private String Link;
    private String Des;
    private String Fees;
    private String Contact;
    private String Poster;

    public WorkshopWord(String mid, String mt, String mv, String mms, String morg, String mcc, String ml, String mdes, String mfees, String mc, String pc) {
        id = mid;
        Title = mt;
        Venue = mv;
        Millisec = mms;
        Org = morg;
        Club = mcc;
        Link = ml;
        Des = mdes;
        Fees = mfees;
        Contact = mc;
        Poster = pc;
    }

    public String getPoster() {
        return Poster;
    }

    public String getFees() {
        return Fees;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return Title;
    }

    public String getVenue() {
        return Venue;
    }

    public String getMillisec() {
        return Millisec;
    }

    public String getOrg() {
        return Org;
    }

    public String getClub() {
        return Club;
    }

    public String getContact() {
        return Contact;
    }

    public String getLink() {
        return Link;
    }

    public String getDes() {
        return Des;
    }
}
