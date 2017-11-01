package com.kosta148.team1.goodmorning;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class WeatherListFragment extends Fragment {

    MainActivity mainActivity;
    ListView weatherList;
    ArrayList<DataGetterSetters> dataList = new ArrayList<DataGetterSetters>();
    DataListAdapter dataListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.fragment_weather_list, container, false);
        mainActivity = (MainActivity) getActivity();

        weatherList = (ListView) v.findViewById(R.id.wearther_item_list);
        //weatherList.addHeaderView(linear);

        dataListAdapter = new DataListAdapter(getActivity(), R.layout.weather_item, dataList);
        weatherList.setAdapter(dataListAdapter);
        dataListAdapter.notifyDataSetChanged();

        return v;
    } // end of onCreateView

    public void setDataList(ArrayList<DataGetterSetters> dataList) {
        this.dataList.clear();
        for(DataGetterSetters data : dataList) {
            this.dataList.add(data);
        }
        dataListAdapter.notifyDataSetChanged();
//        Toast.makeText(getActivity(), "아이템 갯수 : " + this.dataList.size(), Toast.LENGTH_SHORT).show();
    }
}

class DataListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private View adapterView;
    private ArrayList<DataGetterSetters> dataList;
    int lastPosition = -1;
    private Context context;

    public DataListAdapter(Context context, int resource, ArrayList<DataGetterSetters> dataList) {
        inflater = LayoutInflater.from(context);
        this.dataList = dataList;
        this.context = context;
    }
    @Override
    public int getCount() {
        return dataList.size();
    }
    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, android.view.ViewGroup parent) {
        Log.e("겟뷰ㅠㅠ", position+"");
        DataGetterSetters data = dataList.get(position);
        MyViewHolder myViewHolder;

        if (convertView == null) {
            adapterView = inflater.inflate(R.layout.weather_item, null);
            myViewHolder = new MyViewHolder();
            myViewHolder.dayView = (TextView) adapterView.findViewById(R.id.day_view);
            myViewHolder.hourView = (TextView) adapterView.findViewById(R.id.hour_view);
            myViewHolder.tempView = (TextView) adapterView.findViewById(R.id.temp_view);
            myViewHolder.imageView = (ImageView) adapterView.findViewById(R.id.weather_image);
            adapterView.setTag(myViewHolder);
        } else {
            Log.e("Position", "else=>" + position);
            adapterView = convertView;
            myViewHolder = (MyViewHolder) adapterView.getTag();
        }
        if (data != null) {
            myViewHolder.dayView.setText(data.getDay() + "");
            myViewHolder.hourView.setText(data.getHour() - 3 + "시");
            myViewHolder.tempView.setText(data.getTemp() + "℃");


//            int fromTime = data.getHour()-3;
//            if ( 6 <= fromTime  && fromTime <= 15) {
//
//            } else {
//
//            }

            switch (data.getWfKor()) {
                case 0:
                    myViewHolder.imageView.setImageResource(R.drawable.sunny);
                    break;
                case 1:
                    myViewHolder.imageView.setImageResource(R.drawable.partly_cloud);
                    break;
                case 2:
                    myViewHolder.imageView.setImageResource(R.drawable.mostly_cloud);
                    break;
                case 3:
                    myViewHolder.imageView.setImageResource(R.drawable.cloud);
                    break;
                case 4:
                    myViewHolder.imageView.setImageResource(R.drawable.rainy);
                    break;
                case 5:
                    myViewHolder.imageView.setImageResource(R.drawable.snow);
                    break;
                case 6:
                    myViewHolder.imageView.setImageResource(R.drawable.snow);
                    break;
            }
        }
        Animation animation = AnimationUtils.loadAnimation(context, (position > this.lastPosition ? R.anim.add_from_bottom : R.anim.add_from_top));
        adapterView.startAnimation(animation);
        lastPosition = position;
        return adapterView;
    }

    static class MyViewHolder {
        TextView dayView;
        TextView hourView;
        TextView tempView;
        ImageView imageView;
    }
} // end of inner class adapter
