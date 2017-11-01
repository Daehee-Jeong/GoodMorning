package com.kosta148.team1.goodmorning;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by Daehee on 2017-05-04.
 */

public class AirQuality {
    private String dataString;
    private String dataArr[][];
    private String nameArr[][] = new String[][]{
            {"서울특별시", "서울"},
            {"부산광역시", "부산"},
            {"대구광역시", "대구"},
            {"인천광역시", "인천"},
            {"광주광역시", "광주"},
            {"대전광역시", "대전"},
            {"울산광역시", "울산"},
            {"경기도", "경기남부"},    // 남부, 북부 구분 필요...
            {"강원도", "영동"},        // 영동, 영서 구분 필요...
            {"충청북도", "충북"},
            {"충청남도", "충남"},
            {"전라북도", "전북"},
            {"전라남도", "전남"},
            {"경상북도", "경북"},
            {"경상남도", "경남"},
            {"제주특별자치도", "제주"},
    };

    public String getDataString() {
        return dataString;
    }

    public void setDataString(String dataString) {
        this.dataString = dataString;

        String temp[] = dataString.split(" : |,");
        dataArr = new String[temp.length/2][2];
        setDataArr(temp);

    }

    public String[][] getDataArr() {
        return dataArr;
    }
    public void setDataArr(String temp[]) {
        for (int i = 0; i < temp.length/2; i++) {
            for (int j = 0; j <= 1; j++) {
                dataArr[i][j] = temp[(i * 2) + j];
            }
        } // end of for
        for (int i = 0; i < dataArr.length; i++ ) {
            Log.d("dataArr", Arrays.toString(dataArr[i]));
        }
    }

    public String getAirQuality(String localName) {
        String result = "조회오류";
        String temp = ""; // 변환된 시도 이름
        for (int i = 0; i < nameArr.length; i++) {
            if (localName.equals(nameArr[i][0])) {
                temp = nameArr[i][1];
            }
        }
        if (!"".equals(temp)) {
            for (int i = 0; i < dataArr.length; i++) {
                if (temp.equals(dataArr[i][0])) {
                    result = dataArr[i][1];
                }
            }
        }
        return result;
    }
} // end of class
