package com.kosta148.team1.goodmorning;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Daehee on 2017-04-26.
 */

public class NewsFragment extends Fragment {
    private ArrayList<RSSData> dataList;
    private GridView gridView;
    Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.fragment_news, container, false);
        gridView = (GridView) v.findViewById(R.id.gridView01);

        // 읽어오기
        ProcessXmlTask xmlTask = new ProcessXmlTask();
        xmlTask.execute("http://rss.hankyung.com/new/news_main.xml");

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridView gridView = (GridView) parent;
                RSSData rssData = (RSSData) gridView.getItemAtPosition(position);
                String title = rssData.title;
                String link = rssData.link;
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("link", link);
                startActivity(intent);
            }
        });
        return v;
    } // end of onCreateView

    private class ProcessXmlTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            try {
                dataList = new ArrayList<RSSData>();
                URL rssUrl = new URL(urls[0]);
                SAXParserFactory mySAXParserFactory = SAXParserFactory.newInstance();
                SAXParser mySAXParser = mySAXParserFactory.newSAXParser();
                XMLReader myXMLReader = mySAXParser.getXMLReader();
                RSSHandler myRSSHandler = new RSSHandler(dataList);
                myXMLReader.setContentHandler(myRSSHandler);
                InputSource myInputSource = new InputSource(rssUrl.openStream());
                myXMLReader.parse(myInputSource);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("test","Cannot connect RSS!");
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                Log.d("test","Cannot connect RSS!");
            } catch (SAXException e) {
                e.printStackTrace();
                Log.d("test","Cannot connect RSS!");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("test","Cannot connect RSS!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("confirm", "onPostExecute 후 setListView() 호출 전 dataList.size() : " + dataList.size()+"");
            setListview();
            Log.d("confirm", "onPostExecute 후 setListView() 호출 후 dataList.size() : " + dataList.size()+"");
        }
//        for (int i = 0; i  < dataList.size(); i++){
//                Log.d("log", "xyz"+ dataList.get(i).title + dataList.get(i).description);
//            }


    } // end of class ProcessXmlTask
    private void setListview() {
        Log.d("confirm", "setListView() 시작, 어댑터 만들기 전 dataList.size() : " + dataList.size()+"");
        RSSDataAdapter rssAdapter = new RSSDataAdapter(
                getActivity(),
                R.layout.myrow,
                dataList, handler);
        gridView.setAdapter(rssAdapter);
        rssAdapter.notifyDataSetChanged();
        Log.d("confirm", "setListView() 시작, 어댑터 만든 후 dataList.size() : " + dataList.size()+"");

    } // end of setListview

} // end of class

class RSSDataAdapter extends ArrayAdapter<RSSData> {
    private ArrayList<RSSData> RSSDataList;
    private Context context;
    private int lastPosition = -1;
    Animation animFromBottom;
    Animation animFromTop;
    Animation animImg;
    Handler handler;

    public RSSDataAdapter(Context context, int resource, ArrayList<RSSData> RSSDataList, Handler handler) {
        super(context, resource, RSSDataList);
        this.RSSDataList = RSSDataList;
        this.context = context;
        this.handler = handler;
        animImg = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null){
            LayoutInflater lif = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = lif.inflate(R.layout.myrow, null);
        }
        RSSData rss = RSSDataList.get(position);
        if (rss != null){
            TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            TextView tvDescription = (TextView) v.findViewById(R.id.tvDescription);
            final ImageView ivImg = (ImageView) v.findViewById(R.id.ivImg);
            if(tvTitle != null) tvTitle.setText(rss.title);
            if(tvDescription != null) tvDescription.setText(rss.description);
            if(ivImg != null) {
                ivImg.setImageResource(R.mipmap.default_img);
                ImgThread t = new ImgThread(rss.img_url, ivImg);
                t.start();
            }
        }
        Log.d("confirm", "getView() : " + position);
//        v.startAnimation((position > this.lastPosition ? animFromBottom : animFromTop));
        this.lastPosition = position;
        return v;
    } // end of getView


    class ImgThread extends Thread {
        String imgUrl;
        ImageView imageView;

        public ImgThread(String imgUrl, ImageView imageView) {
            this.imgUrl = imgUrl;
            this.imageView = imageView;
        }
        @Override
        public void run() {
            try {
                URL url = new URL(imgUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                final Bitmap bm = BitmapFactory.decodeStream(is);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bm);
                        imageView.startAnimation(animImg); // 여기서 호출시 계속 깜빡이는 문제..
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    } // end of Inner Class ImageThread
} // end of adapter
