package com.example.acer.vitevents.WordClass;

/**
 * This custom Word Class for Conference events
 * This class gets id,title,venue,Date and time in millisecs,Organizer name, Contact number
 * names of the club/chapter,Link to register,Description of the event,Name of the guest,Poster availability
 * It also has functions that returns those values
 */

public class ConfWord {
    private String id;
    private String Title;
    private String Venue;
    private String Millisec;
    private String Org;
    private String Contact;
    private String Club;
    private String Link;
    private String Des;
    private String Guest;
    private String Poster;

    public ConfWord(String mid, String mt, String mv, String mms, String morg, String mcc, String ml, String mdes, String mg, String mc, String pc) {
        id = mid;
        Title = mt;
        Venue = mv;
        Millisec = mms;
        Org = morg;
        Club = mcc;
        Link = ml;
        Des = mdes;
        Guest = mg;
        Contact = mc;
        Poster = pc;
    }

    public String getPoster() {
        return Poster;
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

    public String getGuest() {
        return Guest;
    }
}
