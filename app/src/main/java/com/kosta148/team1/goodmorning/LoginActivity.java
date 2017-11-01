package com.kosta148.team1.goodmorning;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {


    boolean idCheck = false;
    boolean pwCheck = false;
    String id = "";
    String password = "";
    String name = "";
    private EditText etId;
    private EditText etPw;
    int index;

    private ArrayList<User> userDataList; //user의 저장되는 id와 passworld를 저장하기 위한 ArrayList<User 타입>
    private FirebaseDatabase firebaseDatabase; // Firebase 데이터베이스에 접근하기 위한 진입점
    private DatabaseReference databaseReference; // 데이터베이스의 위치에 액세스하고 데이터를 읽거나 쓰기 위한 객체

    protected InputFilter filterEng = new InputFilter() {  // 숫자포함 영문만 입력받을 수 있는 메서드
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    protected InputFilter filterKor = new InputFilter() {  // 한글만 입력받을 수 있는 메서드
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[가-힣]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 액션바
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("로그인");
        actionBar.setDisplayHomeAsUpEnabled(true);

        etId = (EditText) findViewById(R.id.etId);
        etPw = (EditText) findViewById(R.id.etPw);

        TextView tvJoin = (TextView) findViewById(R.id.tvJoin);
        SpannableString content = new SpannableString("회원가입");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvJoin.setText(content);
        tvJoin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivityForResult(intent, 0);
            }
        });


        Button bLogin = (Button) findViewById(R.id.bLogin);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginCheck();
            }
        });

        //아이디 입력값 검사
        etId.setFilters(new InputFilter[]{filterEng}); // 숫자포함 영어만 입력할 수 있게 setFilters.

        etId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (Pattern.matches("^[a-z0-9_]{5,15}$", str)) { //사용할 수 있을 때
                    etId.setTextColor(Color.BLACK);
                    idCheck = true;
                    id = str;
                } else {
                    etId.setTextColor(Color.RED);
                    idCheck = false;
                }
                if (str.endsWith(" ")) {
                    Toast.makeText(getApplicationContext(), "공백입력 노노", Toast.LENGTH_SHORT).show();
                    str = str.trim();
                    etId.setText(str);
                    Editable etext = etId.getText();
                    Selection.setSelection(etext, etext.length());

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //비밀번호 유효성 검사
        etPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();

                if (Pattern.matches("^[a-zA-Z0-9!@.#$%^&*?_~]{8,16}$", str)) { //사용할 수 있을 때
                    etPw.setTextColor(Color.BLACK);

                    pwCheck = true;
                    password = str;

                } else {
                    etPw.setTextColor(Color.RED);
                    pwCheck = false;
                }


                if (str.endsWith(" ")) {
                    Toast.makeText(getApplicationContext(), "공백입력 노노", Toast.LENGTH_SHORT).show();
                    str = str.trim();
                    etPw.setText(str);
                    Editable etext = etPw.getText();
                    Selection.setSelection(etext, etext.length());

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        userDataList = new ArrayList<User>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("user").addChildEventListener(childEventListener);


    }//end of onCreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode ==0){
            etId.setText(data.getStringExtra("userId").toString());
            etPw.setText(data.getStringExtra("userPw").toString());
        }
    }


    protected void loginCheck() {
        for (int i = 0; i < userDataList.size(); i++) {
            if ((etId.getText().toString()).equals(userDataList.get(i).userId)){
                index = i;
                break;
            }
        }
        //리스트내의 id 비교후 passworld까지 비교했음
        if ((etPw.getText().toString()).equals(userDataList.get(index).userPw)){
            Toast.makeText(getApplicationContext(), userDataList.get(index).userName+"님 로그인을 환영합니다.", Toast.LENGTH_SHORT).show();

            //초기화면을 돌아감
            Intent intent = new Intent();
            intent.putExtra("userName", userDataList.get(index).getUserName());
            intent.putExtra("userId", userDataList.get(index).getUserId());
            LoginActivity.this.setResult(RESULT_OK, intent);
            LoginActivity.this.finish();
        }else {
            Toast.makeText(getApplicationContext(),"등록되지 않은 정보입니다.",Toast.LENGTH_SHORT).show();
        }
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            // 아이템이 추가되었을 때 호출
            User userData = dataSnapshot.getValue(User.class);
            userDataList.add(userData);

        }
        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            // 아이템의 변화가 있을 때 호출
        }
        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            // 아이템이 삭제되었을 때 호출
        }
        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            // 순서가 있는 리스트에서 순서가 변경되었을 때 호출
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            // 서버와의 연결 실패 또는 규칙제약에 어긋난 아이템이 삭제처리 되었을 때 호출
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}//end of class