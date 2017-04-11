package com.ms.tests;

/**
 * Created by aashbhardwaj on 4/11/17.
 */

public class SheetsStrings {

    // Testing environment: 1ooKJktuWc0N9SFUkcI8GlgkoRQLGP6mqOwt2TKmOIDo
    // Production environment: 1YvI3CjS4ZlZQDYi5PaiA7WGGcoCsZfLoSFM0IdvdbDU

    // Team environment: 1G8cw3DzNt19QyWN8-BK6tNwONEXzIU3doZ1oV6LnD_k (<- writes not working to this one)
    // Copy t15 doc: 1jnh9rVu6Xt7ZBvE9XfKXbAbinPtDx3tpS7HInnaJ7QQ
    // TODO use http requests to write to personal sheet instead of writing to it using sheets API

    private String url = "1YvI3CjS4ZlZQDYi5PaiA7WGGcoCsZfLoSFM0IdvdbDU";
    private String teamUrl = "1ooKJktuWc0N9SFUkcI8GlgkoRQLGP6mqOwt2TKmOIDo";

    public String getUrl(){
        return url;
    }
    public String getTeamUrl() {return teamUrl;}
}
