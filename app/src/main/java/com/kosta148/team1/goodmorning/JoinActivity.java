package com.kosta148.team1.goodmorning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class JoinActivity extends AppCompatActivity {

    boolean idCheck = false;
    boolean pwCheck = true;
    boolean pwConfirmCheck = false;
    String id = "";
    String password = "";
    String name = "";

    private ArrayList<User> userList ;
    private FirebaseDatabase firebaseDatabase; // Firebase 데이터베이스에 접근하기 위한 진입점
    private DatabaseReference databaseReference; // 데이터베이스의 위치에 액세스하고 데이터를 읽거나 쓰기 위한 객체

    protected InputFilter filterEng = new InputFilter() {  // 숫자포함 영문만 입력받을 수 있는 메서드
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[a-zA-Z0-9_]+$");
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
        setContentView(R.layout.activity_join);

        // 액션바
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("회원가입");
        actionBar.setDisplayHomeAsUpEnabled(true);

        final EditText etId = (EditText) findViewById(R.id.etId);
        final EditText etName = (EditText) findViewById(R.id.etName);
        final EditText etPw = (EditText) findViewById(R.id.etPw);
        final EditText etPwConfirm = (EditText) findViewById(R.id.etPwConfirm);
        etPwConfirm.setEnabled(false);
        final TextView tvConfirm = (TextView) findViewById(R.id.tvConfirm);
        Button bJoin = (Button) findViewById(R.id.btJoin);
        etId.setPrivateImeOptions("defaultInputmode=english;");

//          ////////////////////////////////////////아이디 입력값 검사

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

                    for (int i = 0; i < userList.size(); i++) {
                        if ((etId.getText().toString()).equals(userList.get(i).userId)){
                            etId.setTextColor(Color.RED);
                            idCheck = false;
                            Toast.makeText(getApplicationContext(),"중복된 아이디입니다.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                } else {
                    etId.setTextColor(Color.RED);
                    idCheck = false;
                }

                if (str.endsWith(" ")) {
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

//         /////////////////////////////////////////비밀번호 유효성 검사

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
                    etPwConfirm.setText("");

                    etPwConfirm.setEnabled(true);


                } else {
                    etPw.setTextColor(Color.RED);
                    pwCheck = false;
                }


                if (str.endsWith(" ")) {
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

        //      비밀번호 확인하기
        etPwConfirm.addTextChangedListener(new TextWatcher() {
            String str;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                str = s.toString();

                if (str.endsWith(" ")) {
                    str = str.trim();
                    etPwConfirm.setText(str);
                    Editable etext = etPwConfirm.getText();
                    Selection.setSelection(etext, etext.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (str.equals(password)) {
                    pwConfirmCheck = true;
                    tvConfirm.setText("비밀번호가 일치합니다.");
                } else {
                    tvConfirm.setText("비밀번호가 일치하지 않습니다.");
                    pwConfirmCheck = false;
                }
            }
        });


//      이름 입력창 유효성 검사
        etName.setFilters(new InputFilter[]{filterKor});

//      회원가입 버튼 누를시 발생하는 이벤트
        bJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etName.getText().toString();

                if ("".equals(name)||name.contains(" ")) {
                    Toast.makeText(getApplicationContext(), "이름", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (idCheck && pwConfirmCheck) {

                    final User user = new User(id, password, name);
                    //UserList.add(user);


                    databaseReference.child("user").push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("userId", user.getUserId());
                            intent.putExtra("userPw", user.getUserPw());
                            JoinActivity.this.setResult(0, intent);
                            JoinActivity.this.finish();
                        }
                    });


                } else if (idCheck && !pwConfirmCheck) {
                    Toast.makeText(getApplicationContext(), "비밀번호 다시", Toast.LENGTH_SHORT).show();
                    etPw.setFocusable(true);
                } else if (!idCheck && pwConfirmCheck) {
                    Toast.makeText(getApplicationContext(), "아이디 다시", Toast.LENGTH_SHORT).show();
                    etId.setFocusable(true);
                } else {
                    Toast.makeText(getApplicationContext(), "둘다 다시", Toast.LENGTH_SHORT).show();
                    etId.setFocusable(true);
                }
            }

        });
        userList = new ArrayList<User>();


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


        databaseReference.child("user").addChildEventListener(childEventListener);

    }//end of onCreate

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            // 아이템이 추가되었을 때 호출
            User user = dataSnapshot.getValue(User.class);
            userList.add(user);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            // 아이템에 변화가 있을 때 호출
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
            // 서버와의 연결 실패 또는 규칙제약에 어긋난 아이템이 삭제처리 되었을 때
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
