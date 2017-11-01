package com.kosta148.team1.goodmorning;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Daehee on 2017-04-26.
 */

public class WeatherFragment extends Fragment {

    TextView curTemp;
    TextView curRainRatio;
    TextView curRainAmount;
    TextView curWind;
    TextView lastUpdateView;
    TextView rehView;
    TextView predictRainView;
    TextView airQualityView;
    LinearLayout airQualityLayout;
    ImageView ivMainWeather;

    MyPagerAdapter myPagerAdapter;
    ViewPager viewPager;
    MainActivity mainActivity;
    PagerSlidingTabStrip tabStrip;
    ArrayList<DataGetterSetters> dataList;
    private DataListAdapter adapter;
    String dongCode = "";

    WeatherListFragment weatherListFragment;
    WeatherTalkFragment weatherTalkFragment;
    private StringBuilder resultBuilder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.fragment_weather, container, false);
        mainActivity = (MainActivity) getActivity();

        ivMainWeather = (ImageView)v.findViewById(R.id.ivMainWeather);
        curTemp = (TextView) v.findViewById(R.id.curTemp);
        curRainRatio = (TextView) v.findViewById(R.id.curRainRatio);
        curRainAmount = (TextView) v.findViewById(R.id.curRainAmount);
        curWind = (TextView) v.findViewById(R.id.curWind);
        lastUpdateView = (TextView) v.findViewById(R.id.lastUpdateView);
        rehView = (TextView)v.findViewById(R.id.curRehView);
        predictRainView = (TextView)v.findViewById(R.id.predictRainView);
        airQualityView = (TextView)v.findViewById(R.id.airQualityView);
        airQualityLayout = (LinearLayout)v.findViewById(R.id.airQualityViewLayout);
        airQualityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Display display = mainActivity.getWindowManager().getDefaultDisplay();
                int w = (int) (display.getHeight() / 0.6);
                int h = w;

                final AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setTitle("미세먼지 상세정보")
                        .setMessage(
                                resultBuilder
                        ).setNegativeButton("닫기", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.height = (int) (lp.width * 1.2);
                dialog.getWindow().setAttributes(lp);
//                dialog.getWindow().setLayout(w, h);
            }
        });

        myPagerAdapter = new MyPagerAdapter(mainActivity.getSupportFragmentManager());
        viewPager = (ViewPager) v.findViewById(R.id.viewPager01);
        viewPager.setAdapter(myPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                Toast.makeText(mainActivity, "현재 포지션 : " + position, Toast.LENGTH_SHORT).show();
                if (position == 1) {
                    mainActivity.fab.hide();
                } else {
                    mainActivity.fab.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabStrip = (PagerSlidingTabStrip) v.findViewById(R.id.tabStrip);
        tabStrip.setViewPager(viewPager);

        if (MainActivity.local != null) {
            if (MainActivity.local.length > 0) {
//                regionView.setText(MainActivity.local[0][0]);
                dongCode = MainActivity.local[0][1];
                MyTask t = new MyTask(dongCode);
                t.execute();

                Log.e("this clause in", "ok");
                weatherTalkFragment = (WeatherTalkFragment) MyPagerAdapter.fragments[1];
//                weatherTalkFragment.refreshChildListener(0);

            } else {
                Toast.makeText(getActivity(), "등록된 지역이 없습니다\r\n  지역을 추가하세요", Toast.LENGTH_SHORT).show();
            }
        }
        return v;
    } // end of onCreateView




    class MyTask extends AsyncTask<String, Integer, Boolean> {
        DataHandler myDataHandler;
        ProgressDialog progressDialog;
        String dongCode;

        MyTask(String dongCode) {
            this.dongCode = dongCode;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(mainActivity);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("로딩중입니다.");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                Log.e("doInBackground() 시작", "doInBackground() 시작");
                //------ 메인 파싱 구간 시작 ------//
                SAXParserFactory saxPF = SAXParserFactory.newInstance();
                SAXParser saxParser = saxPF.newSAXParser();
                XMLReader xmlReader = saxParser.getXMLReader();
//				URL url = new URL("http://rss.hankyung.com/new/news_industry.xml");
                URL url = new URL("http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=" + this.dongCode);
                myDataHandler = new DataHandler();
                xmlReader.setContentHandler(myDataHandler);
                xmlReader.parse(new InputSource(url.openStream()));
                //------ 메인 파싱 구간 종료 ------//

                // load parsing data & View
                dataList = myDataHandler.getData();
                Log.e("doIn완료 전", dataList.toString());
                Thread.sleep(500);
                Log.e("doInBackground() 완료", "doInBackground() 완료");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.e("onPostExecute() 시작", "onPostExecute() 시작");
            adapter = new DataListAdapter(getActivity(), 0, dataList);
            if (dataList == null) {
                Log.e("데이터리스트 null", "데이터리스트 null");
            } else { //dataList가 null이면 adapter연결 X (강제종료 방지)
                lastUpdateView.setText("업데이트 : " + myDataHandler.lastUpdate);
                Log.e("최종 업데이트 : ", myDataHandler.lastUpdate);
                Log.e("위치 : ", myDataHandler.myLocation);
                SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(adapter);
//                animationAdapter.setAbsListView(weatherList);
//                weatherList.setAdapter(animationAdapter);
//                weatherList.setAdapter(adapter);

                int fromTime = dataList.get(0).getHour() - 3;
                Log.e("asdasdadasd", fromTime+"");
                if (6 <= fromTime && fromTime <= 15) { // 낮일때
                    switch (dataList.get(0).getWfKor()) {
                        case 0:
//                        weatherKorView.setText("맑음");
                            ivMainWeather.setImageResource(R.drawable.main_main_day_clear);
                            break;
                        case 1:
//                        weatherKorView.setText("구름 조금");
                            ivMainWeather.setImageResource(R.drawable.main_day_partly_cloud);
                            break;
                        case 2:
//                        weatherKorView.setText("구름 많음");
                            ivMainWeather.setImageResource(R.drawable.main_day_more_cloud);
                            break;
                        case 3:
//                        weatherKorView.setText("흐림");
                            ivMainWeather.setImageResource(R.drawable.cloud);
                            break;
                        case 4:
//                        weatherKorView.setText("비");
                            ivMainWeather.setImageResource(R.drawable.main_day_rain);
                            break;
                        case 5:
//                        weatherKorView.setText("눈/비");
                            ivMainWeather.setImageResource(R.drawable.main_day_snow_rain);
                            break;
                        case 6:
//                        weatherKorView.setText("눈");
                            ivMainWeather.setImageResource(R.drawable.main_day_snow);
                            break;
                    }
                } else { // 밤일때
                    switch (dataList.get(0).getWfKor()) {
                        case 0:
//                        weatherKorView.setText("맑음");
                            ivMainWeather.setImageResource(R.drawable.main_night_clear);
                            break;
                        case 1:
//                        weatherKorView.setText("구름 조금");
                            ivMainWeather.setImageResource(R.drawable.main_night_partly_cloud);
                            break;
                        case 2:
//                        weatherKorView.setText("구름 많음");
                            ivMainWeather.setImageResource(R.drawable.main_night_more_cloud);
                            break;
                        case 3:
//                        weatherKorView.setText("흐림");
                            ivMainWeather.setImageResource(R.drawable.cloud);
                            break;
                        case 4:
//                        weatherKorView.setText("비");
                            ivMainWeather.setImageResource(R.drawable.main_night_rain);
                            break;
                        case 5:
//                        weatherKorView.setText("눈/비");
                            ivMainWeather.setImageResource(R.drawable.main_night_snow_rain);
                            break;
                        case 6:
//                        weatherKorView.setText("눈");
                            ivMainWeather.setImageResource(R.drawable.main_night_snow);
                            break;
                    }
                }


//                tempView.setText(dataList.get(0).getTemp() + "℃");
                for (int i = 0; i < dataList.size(); i++) {
                    if (dataList.get(i).getTmx() == -999.0) {
                        i++;
                    } else {
                        if (i != 0) {
//                            Toast.makeText(mainActivity, "최고최저 온도현황 내일것 불러옴", Toast.LENGTH_SHORT).show();
                        }
//                        curTemp.setText(dataList.get(i).getTmx() + " / " + dataList.get(i).getTmn() + "℃");
                        curTemp.setText(dataList.get(i).getTmx() + "℃");
                        break;
                    }
                }
                curRainRatio.setText(dataList.get(0).getPop() + "%");
                curRainAmount.setText(dataList.get(0).getR12() + "mm");
                curWind.setText(dataList.get(0).getWs() + "m/s");

                rehView.setText(dataList.get(0).getReh() + "%");
                predictRainView.setText(dataList.get(0).getSky());
            }
            Log.e("onPostExecute() 완료", "onPostExecute() 완료");
            weatherListFragment = (WeatherListFragment) MyPagerAdapter.fragments[0];
            weatherListFragment.setDataList(dataList);

            mainActivity.toolbarTitle.setText(mainActivity.dongName);
            progressDialog.dismiss();

            WeatherTalkFragment wtf = (WeatherTalkFragment)MyPagerAdapter.fragments[1];
            wtf.btnSend.setEnabled(true);
//            wtf.refreshChildListener(0);

            super.onPostExecute(result);
        }
    } // end of inner class MyTask

    public void getJsonData() {
        new Thread(){
            @Override
            public void run() {
                String result = "";
                String searchDate = "";

                Date today = new Date();
                Date selDate = new Date();
                Date today_05am = new Date(today.getYear(), today.getMonth(), today.getDate(), 05, 00);

                // 당일의 05시 이전에는 요청데이터 값이 비어있다. 따라서 05시 전에는 하루전날의 예보를 보여주고,
                // 하루전날 예보의 '내일예보'를 참고하도록 도와야 한다..

                if (today.before(today_05am)) {
                    selDate.setTime(today.getTime() + (1000 * 60 * 60 * 24) * (-1));
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                searchDate = sdf.format(selDate);
                Log.e("날짜", searchDate);

                StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMinuDustFrcstDspth?searchDate="+ searchDate +"&_returnType=json");
                try {
                    urlBuilder.append("&" + URLEncoder.encode("ServiceKey", "UTF-8") + "=WiWFmvhwlbZEnbjJ1BtY%2FMZlVVF12Q3yOw6lukrHOEl84d774fMzsACXiIf4mCg4jYv3ouh8mjL0M4NzjgFuJw%3D%3D"); /*Service Key*/
                    URL url = new URL(urlBuilder.toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-type", "application/json");
                    System.out.println("Response code: " + conn.getResponseCode());
                    BufferedReader rd;
                    if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } else {
                        rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    rd.close();
                    conn.disconnect();
//            System.out.println(sb.toString());
//                    showToast(sb.toString());
                    Log.e("미세먼지", sb.toString());
                    String jsonData = new String(sb);
                    resultBuilder = new StringBuilder();
                    JSONObject rootObject = new JSONObject(jsonData);
                    JSONArray list = rootObject.getJSONArray("list");

                    JSONObject firstObject = list.getJSONObject(0);
                    String dataTime = firstObject.getString("dataTime");                // 통보시간
                    String informCause = firstObject.getString("informCause");          // 발생원인
                    String informGrade = firstObject.getString("informGrade");          // 예보등급
                    String informOverall = firstObject.getString("informOverall");      // 예보개황

                    JSONObject secondObject = list.getJSONObject(1);
                    String informCause2 = secondObject.getString("informCause");
                    String informOverall2 = secondObject.getString("informOverall");


                    if (informOverall.length() > 0) informOverall = "○ " + informOverall.substring(8, informOverall.length());
                    if (informCause.length() > 0) informCause = "○ " + informCause.substring(8, informCause.length());
                    if (informOverall2.length() > 0) informOverall2 = "○ " + informOverall2.substring(8, informOverall2.length());
                    if (informCause2.length() > 0) informCause2 = "○ " + informCause2.substring(8, informCause2.length());

                    resultBuilder.append(dataTime);
                    resultBuilder.append("\n\n" + "[오늘]\n" + informOverall);
                    resultBuilder.append("\n" + informCause);
                    resultBuilder.append("\n\n" + "[내일]\n" + informOverall2);
                    resultBuilder.append("\n" + informCause2);

                    AirQuality aq = new AirQuality();
                    aq.setDataString(informGrade);
                    final String quality = aq.getAirQuality((mainActivity.dongName.split(" "))[0]);
                    mainActivity.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            airQualityView.setText(quality);
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                } catch (ProtocolException e) {
                } catch (MalformedURLException e) {
                } catch (IOException e) {
                } catch (JSONException e) {
                }
            }
        }.start();
    } // end of getJsonData

    public int getPx(int dimensionDp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dimensionDp * density + 0.5f);
    }

    public void startMyTask(String dongCode) {
        MyTask myTask = new MyTask(dongCode);
        myTask.execute();
        getJsonData();
    }
} // end of class




