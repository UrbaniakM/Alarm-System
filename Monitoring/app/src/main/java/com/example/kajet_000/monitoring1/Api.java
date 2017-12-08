package com.example.kajet_000.monitoring1;

/**
 * Created by kajet_000 on 01.12.2017.
 */
public class Api {
    private static final String ROOT_URL = "http://kajetanz.heliohost.org/MonitoringApi/v1/Api.php?apicall=";

    public static final String URL_CREATE_HERO = ROOT_URL + "createhero";
    public static final String URL_READ_HEROES = ROOT_URL + "getheroes";
    public static final String URL_READ_STATUS = ROOT_URL + "getstatus";
    public static final String URL_UPDATE_HERO = ROOT_URL + "updatehero";
    public static final String URL_DELETE_HERO = ROOT_URL + "deletehero&id=";
}
