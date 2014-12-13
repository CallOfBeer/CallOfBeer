package com.dev.callofbeer;

import java.sql.Timestamp;

/**
 * Created by admin on 12/12/2014.
 */
public class EventBeer {
    private String nomEvent;
    private String adresseEvent;
    private Long timer;
    private int sizeEvent;
    double lat,longi;

    public EventBeer(String nomEvent, String adresseEvent, int sizeEvent, double lat, double longi){
        this.nomEvent = nomEvent;
        this.adresseEvent = adresseEvent;
        this.sizeEvent = sizeEvent;
        this.lat = lat;
        this.longi = longi;
    }
    public EventBeer(String nomEvent, Long timer, double lat, double longi){
        this.nomEvent = nomEvent;
        this.timer = timer;
        this.lat = lat;
        this.longi = longi;
    }

    public String getNomEvent()
    {
        return nomEvent;
    }

    public String getAdresseEvent()
    {
        return adresseEvent;
    }

    public  int getSizeEvent()
    {
        return sizeEvent;
    }

    public double getLat()
    {
        return  lat;
    }

    public  double getLongi()
    {
        return longi;
    }

    public Long getTimer(){return timer;}
}