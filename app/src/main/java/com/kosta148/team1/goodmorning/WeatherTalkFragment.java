package com.kosta148.team1.goodmorning;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Daehee on 2017-04-26.
 */

public class WeatherTalkFragment extends Fragment {

    MainActivity mainActivity;

    ListView listView;
    private EditText etInput;
    Button btnSend;
    private ArrayList<ChatData> chatDataList;
    private MyAdapter adapter;

    // 파이어베이스
    private FirebaseDatabase firebaseDatabase; // Firebase 데이터베이스에 접근하기 위한 진입점
    private DatabaseReference databaseReference; // 데이터베이스의 위치에 액세스하고 데이터를 읽거나 쓰기 위한 객체

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.fragment_weather_talk, container, false);
        mainActivity = (MainActivity) getActivity();

        listView = (ListView) v.findViewById(R.id.listView01);
        etInput = (EditText) v.findViewById(R.id.etInput);
        btnSend = (Button) v.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy년MM월dd일 hh:mm");
                String date = sdfDate.format(new Date());
                String text = etInput.getText().toString();
                if ("".equals(text)) {
                    Toast.makeText(mainActivity.getApplicationContext(),"내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                ChatData chatData = new ChatData(MainActivity.userId, text, date);
                databaseReference.child("chat").child(mainActivity.dongCode).push().setValue(chatData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("OnCompleteListener","전송 완료");
                                etInput.setText("");
                            }
                        });
                adapter.notifyDataSetChanged();
                ((InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(etInput.getWindowToken(), 0);
            }
        });

        chatDataList = new ArrayList<ChatData>();
        adapter = new MyAdapter(mainActivity.getApplicationContext(), R.layout.list_item_chat, chatDataList);
        listView.setAdapter(adapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        if (MainActivity.local.length > 0) {
//            refreshChildListener(0);
            btnSend.setEnabled(true);
        } else {
            btnSend.setEnabled(false);
        }
        // 최초 생성될때 저장된 첫번째 지역의 채팅방으로 초기화
        refreshChildListener(0);
        return v;
    } // end of onCreateView

    public void refreshChildListener(int position) {
        if (MainActivity.local.length > 0) {
            Log.e("리프레쉬", MainActivity.local[position][0] + "");
//        databaseReference.child("chat").child(MainActivity.local[0][1]).addChildEventListener(null);
            if (chatDataList != null) chatDataList.clear();
            databaseReference.child("chat").child(MainActivity.local[position][1]).addChildEventListener(childEventListener);
            adapter.notifyDataSetChanged();

//            mainActivity.handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    listView.smoothScrollToPosition(chatDataList.size()-1);
//                    listView.setSelection(chatDataList.size()-1);
//                }
//            }, 2000);
        }
    }

    public void unregisterChildListener(String str) {
        databaseReference.child("chat").child(str).removeEventListener(childEventListener);
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            // 아이템이 추가되었을 때 호출
            ChatData chatData = dataSnapshot.getValue(ChatData.class);
            chatDataList.add(chatData);
            adapter.notifyDataSetChanged();
            listView.setSelection(chatDataList.size()-1);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            // 아이템이 변경되었을 때 호출
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

}
