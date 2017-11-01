package com.kosta148.team1.goodmorning;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 정다린 on 2017-04-27.
 */

public class RSSData {
    String title = "";
    String description = "";
    String link = "";
    String img_url = "";


    public RSSData() { }
    public RSSData(String title, String description, String link, String img_url) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.img_url = img_url;
    }
}
