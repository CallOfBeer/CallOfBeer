package com.dev.callofbeer.models;

/**
 * Created by admin on 12/12/2014.
 */


/**
 * Class EventBeer
 *    Model of the events created
 *
 */
public class EventBeer {
    private int id;
    private String nomEvent;
    private String adresseEvent;
    private Long timer;
    private int sizeEvent;
    double latitude, longitude;

    public EventBeer(int id, String nomEvent, String adresseEvent, int sizeEvent, double latitude, double longitude){
        this.id = id;
        this.nomEvent = nomEvent;
        this.adresseEvent = adresseEvent;
        this.sizeEvent = sizeEvent;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public EventBeer(int id, String nomEvent, Long timer, double latitude, double longitude){
        this.id = id;
        this.nomEvent = nomEvent;
        this.timer = timer;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() { return id;}

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

    public double getLatitude()
    {
        return latitude;
    }

    public  double getLongitude()
    {
        return longitude;
    }

    public Long getTimer(){return timer;}
}