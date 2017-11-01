package com.kosta148.team1.goodmorning;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.tsengvn.typekit.Typekit;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {
    private static final int GET_REGION = 0;
    private static final int REG_MEMBER = 1;
    private static final int LOGIN_MEMBER = 2;
    Handler handler = new Handler();
    Toolbar toolbar;
    TextView toolbarTitle;
    Toast t;
    WeatherFragment weatherFragment;
    NewsFragment newsFragment = new NewsFragment();
    FragmentManager fm;
    Animation fabAnimShrink;
    Animation fabAnimInflate;
    Animation fragmentShrink;
    Animation fragmentInflate;
    Animation animFade;
    FloatingActionButton fab;

    Context context;

    ArrayList<String> regionArr;
    LocalListAdapter regionAdapter;
    static SQLiteDatabase db;
    static MySQLiteOpenHelper helper;
    String dbName = "local_data.db";
    int dbVersion = 1;
    String tag = "SQLite";
    static String[][] local;

    String urlXml = "http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=";
    String dongCode;
    String dongName;

    ListView drawerItemList;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;

    LinearLayout addRegion;

    boolean isWeatherFragment = true;
    static String userName = "";
    static String userId = "GUEST";

    Typeface typeFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        typeFace = Typeface.createFromAsset(getAssets(), "NanumBarunGothic.ttf");

        Typekit.getInstance()
                .addCustom1(Typekit.createFromAsset(this, "NanumBarunGothic.ttf"))
                .addCustom2(Typekit.createFromAsset(this, "NanumBarunGothicBold.ttf"))
                .addNormal(Typekit.createFromAsset(this, "NanumBarunGothic.ttf"))
                .addBold(Typekit.createFromAsset(this, "NanumBarunGothicBold.ttf"));

        setContentView(R.layout.activity_main);


        LayoutInflater li = getLayoutInflater();
        LinearLayout linear = (LinearLayout) li.inflate(R.layout.layout_header, null); // 레이아웃에 맞게!

        context = getApplicationContext();

        fabAnimShrink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shrink);
        fabAnimInflate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.inflate);
        fragmentShrink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shrink_fragment);
        fragmentInflate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.inflate_fragment);
        animFade = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        WeatherListFragment fr = (WeatherListFragment) MyPagerAdapter.fragments[0];

        weatherFragment = (WeatherFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_weather);

        fm = getSupportFragmentManager();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("지역을 추가해주세요");


        // 폰트 지정 !!!
        toolbarTitle.setTypeface(typeFace);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_news);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                animateFloatingActionButton();
                toggleFragment();
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog(1);
                return false;
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.ic_setting));
//        toolbar.setNavigationIcon(R.mipmap.ic_launcher);

        addRegion = (LinearLayout) findViewById(R.id.tv_add_region);
        addRegion.setOnClickListener(myClickListener);

        regionArr = new ArrayList<String>();
        regionAdapter = new LocalListAdapter(this, R.layout.simple_list_item_2, regionArr);

        drawerItemList = (ListView) findViewById(R.id.drawer_item_list);
        drawerItemList.setAdapter(regionAdapter);
        drawerItemList.setOnItemClickListener(localItemClickListener);

        helper = new MySQLiteOpenHelper(MainActivity.this, dbName, null, dbVersion);
        try {
//         // 데이터베이스 객체를 얻어오는 다른 간단한 방법
//         db = openOrCreateDatabase(dbName,  // 데이터베이스파일 이름
//                          Context.MODE_PRIVATE, // 파일 모드
//                          null);    // 커서 팩토리
//
//         String sql = "create table mytable(id integer primary key autoincrement, name text);";
//        db.execSQL(sql);

            db = helper.getWritableDatabase(); // 읽고 쓸수 있는 DB
            //db = helper.getReadableDatabase(); // 읽기 전용 DB select문
        } catch (SQLiteException e) {
            e.printStackTrace();
            Log.e(tag, "데이터베이스를 얻어올 수 없음");
            finish(); // 액티비티 종료
        }
        refreshList(1);

        if (local.length > 0) {
//            weatherFragment.regionView.setText(local[0][0]);
            dongCode = local[0][1];
            dongName = local[0][0];

            weatherFragment.startMyTask(dongCode);


//            MyTask t = new MyTask();
//            t.execute();
        } else {
//            Toast.makeText(getApplicationContext(), "등록된 지역이 없습니다\r\n  지역을 추가하세요", Toast.LENGTH_SHORT).show();
        }

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

        drawer.findViewById(R.id.nav_login_member).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, LOGIN_MEMBER);
            }
        });
        // 단말기 Android ID (단! 2.2이전 버전은 디바이스 고유 번호를 획득한다고 보장할 수 없다)
        String idByANDROID_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if ("".equals(userName)) {
            userId = "GUEST(" + idByANDROID_ID + ")";
        }
    } // end of onCreate에

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder dialogEaster = new AlertDialog.Builder(this);
        LayoutInflater lif = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = lif.inflate(R.layout.dialog_easter, null);
        dialogEaster.setTitle(" "); // title

        dialogEaster.setView(view);
        dialogEaster.setPositiveButton("확인", null);
        return dialogEaster.create();
    }

    public void animateFloatingActionButton() {
        fabAnimShrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isWeatherFragment) {
                    fab.setImageResource(R.drawable.ic_news);
                } else {
//                    fab.setImageResource(R.drawable.partly_cloud);
                    fab.setImageResource(R.drawable.main_day_partly_cloud);
                }
                fab.startAnimation(fabAnimInflate);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        fab.startAnimation(fabAnimShrink);
    }

    public void toggleFragment() {
//        showToast("isWeatherFragment : " + isWeatherFragment);
        FragmentTransaction tran = fm.beginTransaction();
        tran.setCustomAnimations(R.anim.inflate_fragment_400, R.anim.shrink_fragment, R.anim.inflate_fragment_400, R.anim.shrink_fragment);
        if (isWeatherFragment) {
            tran.add(R.id.container, newsFragment);
            tran.addToBackStack(null);
            toolbarTitle.setText("주요 뉴스 확인");
            toolbarTitle.startAnimation(animFade);
            isWeatherFragment = false;
        } else {
            fm.popBackStack();
            toolbarTitle.setText(dongName);
            toolbarTitle.startAnimation(animFade);
            isWeatherFragment = true;
        }
        tran.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), ConfigActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    View.OnClickListener myClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tv_add_region) {
                Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                startActivityForResult(intent, GET_REGION);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GET_REGION: // requestCode가 B_ACTIVITY인 케이스
                if (resultCode == RESULT_OK) { //B_ACTIVITY에서 넘겨진 resultCode가 OK일때만 실행
                    if (data != null) {
                        refreshList(1);
                        Toast.makeText(getApplicationContext(), "추가되었습니다.", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case REG_MEMBER:
            case LOGIN_MEMBER:// 회원가입 // 로그인
                if (resultCode == RESULT_OK) {
                    // 회원가입시 자동로그인과 로그인후 로그인
                    TextView tv_nav_name = (TextView) drawer.findViewById(R.id.nav_header).findViewById(R.id.tv_nav_name);
                    TextView tv_nav_id = (TextView) drawer.findViewById(R.id.nav_header).findViewById(R.id.tv_nav_id);
                    userName = data.getStringExtra("userName").toString();
                    userId = data.getStringExtra("userId").toString();
                    tv_nav_name.setText(userName);
                    tv_nav_id.setText(userId);
                    ImageView ivLogInOut = (ImageView) drawer.findViewById(R.id.nav_header).findViewById(R.id.nav_login_member);
                    ivLogInOut.setImageResource(R.drawable.logout_icon_nomal);
                }
                break;
        }
    }

    public void refreshList(int num) {
        if (num == 1) { //드로어 안에있는 리스트 높이 재조정
            regionAdapter.clear();
            Cursor c = db.query("mytable", null, null, null, null, null, null);
            int count = c.getCount();
            local = new String[count][2];
            int i = 0;
            Log.d(tag, "레코드 갯수:" + count);
            while (c.moveToNext()) {
                int _id = c.getInt(0);
                String name = c.getString(1);
                String code = c.getString(2);
                local[i][0] = name;
                local[i][1] = code;
                i++;
                Log.d(tag, "_id:" + _id + ",name:" + name + ",code:" + code);
                regionArr.add(name);
                regionAdapter.notifyDataSetChanged();
            }
            //ListView 높이 재설정
            int h = regionArr.size() * getPx(51);
            ViewGroup.LayoutParams lp = drawerItemList.getLayoutParams();
            lp.height = h;
            drawerItemList.setLayoutParams(lp);

            for (int a = 0; a < local.length; a++) {
                for (int b = 0; b < 2; b++) {
                    Log.e("local[" + a + "]" + "[" + b + "]", local[a][b]); //DB변경사항이 배열에도 잘 반영되었는지 로그확인
                }
            }
        }
//        else if (num == 2) { //날씨 아이템 리스트 높이 재조정
//            int h = dataList.size()*getPx(90);
//            ViewGroup.LayoutParams lp = weatherList.getLayoutParams();
//            lp.height = h;
//            weatherList.setLayoutParams(lp);
//        }

    }

    public void showToast(String text) {
        if (t != null) t.cancel();
        t = Toast.makeText(getApplicationContext(),
                text,
                Toast.LENGTH_SHORT);
        t.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (t != null) t.cancel();
    }

    AdapterView.OnItemClickListener localItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("포지션", "position : " + position);
            Cursor c = db.query("mytable", null, null, null, null, null, null);
            c.move(position + 1);
            Log.d(tag, "name:" + c.getString(1) + " / " + "code:" + c.getString(2));

            WeatherTalkFragment wtf = (WeatherTalkFragment) MyPagerAdapter.fragments[1];
            if (dongCode != null) wtf.unregisterChildListener(dongCode);

            dongCode = local[position][1];
            weatherFragment.dongCode = local[position][1];
            dongCode = local[position][1];
            dongName = local[position][0];
//            weatherFragment.regionView.setText(local[position][0]);
            weatherFragment.startMyTask(dongCode);


            wtf.refreshChildListener(position);

            drawer.closeDrawers();
        }
    };

    class LocalListAdapter extends ArrayAdapter<String> {
        private LayoutInflater inflater;
        private View adapterView;
        private LocalViewHolder viewHolder;

        public LocalListAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            inflater = LayoutInflater.from(context);
        }

        public View getView(final int position, View convertView, android.view.ViewGroup parent) {
            if (convertView == null) {
                adapterView = inflater.inflate(R.layout.simple_list_item_2, null);
                viewHolder = new LocalViewHolder();
                viewHolder.textView = (TextView) adapterView.findViewById(R.id.textView);
                viewHolder.imageView = (ImageView) adapterView.findViewById(R.id.delete_btn);
                adapterView.setTag(viewHolder);
            } else {
                Log.e("Position", "else=>" + position);
                adapterView = convertView;
                viewHolder = (LocalViewHolder) adapterView.getTag();
            }
            viewHolder.textView.setText(local[position][0]);
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setTitle("삭제");
                    alert.setMessage("정말로 삭제 하시겠습니까?");
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e("삭제 처리", "position : " + position + " / name : " + local[position][0] + " / " + "code : " + local[position][1]);
                            Cursor c = db.query("mytable", null, null, null, null, null, null);
                            c.move(position + 1);
                            db.execSQL("delete from mytable where id=" + c.getInt(0) + ";");
                            refreshList(1);
                            Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
            });
            return adapterView;
        }
    } // end of inner class LocalListAdapter

    public int getPx(int dimensionDp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dimensionDp * density + 0.5f);
    }

    class LocalViewHolder {
        TextView textView;
        ImageView imageView;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    Runnable backKeyRun = new Runnable() {
        int count = 0;

        @Override
        public void run() {
            if (count < 1) {
                showToast("뒤로가기를 한번 더 누르시면 종료됩니다");
                count++;
            } else {
                finish();
            }

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    count = 0;
                }
            }, 2000);
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (isWeatherFragment) {
                handler.post(backKeyRun);
            } else {
                animateFloatingActionButton();
                toggleFragment();
            }
        }
    }
} // end of class
