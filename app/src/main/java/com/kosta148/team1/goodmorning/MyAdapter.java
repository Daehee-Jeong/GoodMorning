package com.kosta148.team1.goodmorning;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {

    Handler handler = new Handler();
    Context context;
    int list_item_chat;
    ArrayList<ChatData> chatDataList;
    LayoutInflater lif;

    int DP_10;

    public MyAdapter(Context context, int list_item_chat, ArrayList<ChatData> chatDataList) {
        this.context = context;
        this.list_item_chat = list_item_chat;
        this.chatDataList = chatDataList;
        this.lif = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DP_10 = getPx(10);
    }

    @Override
    public int getCount() {
        return chatDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatDataList.get(position).getId();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = lif.inflate(list_item_chat, null);
            holder = new ViewHolder();
            holder.linear = (LinearLayout)convertView.findViewById(R.id.linear_chat_item);
            holder.tvId = (TextView) convertView.findViewById(R.id.tvId);
            holder.tvText = (TextView) convertView.findViewById(R.id.tvText);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ChatData chatData = chatDataList.get(position);
        holder.tvId.setText(chatData.getId());
        holder.tvText.setText(chatData.getText());
        holder.tvDate.setText(chatData.getDate());

        // 레이아웃 관련
//        holder.tvText.setPadding(DP_10, DP_10, DP_10, DP_10); // 패딩 xml 속성이 먹지않아서 코드로..
        if (chatData.getId().equals(MainActivity.userId)) {
            holder.tvText.setBackgroundResource(R.drawable.outbox);
            holder.linear.setGravity(Gravity.RIGHT);
            holder.tvDate.setGravity(Gravity.RIGHT);
        } else {
            holder.tvText.setBackgroundResource(R.drawable.inbox);
            holder.linear.setGravity(Gravity.LEFT);
            holder.tvDate.setGravity(Gravity.LEFT);
        }
//        if (position % 2 == 0) {
//            holder.tvText.setBackgroundResource(R.drawable.inbox);
//            holder.linear.setGravity(Gravity.LEFT);
//            holder.tvDate.setGravity(Gravity.LEFT);
//        } else {
//            holder.tvText.setBackgroundResource(R.drawable.outbox);
//            holder.linear.setGravity(Gravity.RIGHT);
//            holder.tvDate.setGravity(Gravity.RIGHT);
//        }

        //ImgThread t = new ImgThread(chatData.getImgPath(), holder.ivProfile);
        //t.start();
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.add_from_bottom_chat);
        convertView.startAnimation(anim);

        return convertView;
    }

    static class ViewHolder {
        LinearLayout linear;
        TextView tvId;
        TextView tvText;
        TextView tvDate;
    }

    public int getPx(int dimensionDp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dimensionDp * density + 0.5f);
    }
/*
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
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                final Bitmap bm = BitmapFactory.decodeStream(is);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bm);
                    }
                });
            } catch (MalformedURLException e) {
            } catch (IOException e) {
            }
        }
    } // end of Inner Class ImageThread
*/

}
