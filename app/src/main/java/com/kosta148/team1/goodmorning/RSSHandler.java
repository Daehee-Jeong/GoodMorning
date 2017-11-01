package com.kosta148.team1.goodmorning;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

class RSSHandler extends DefaultHandler {
    final int STATE_UNKNOWN = 0;
    final int STATE_TITLE = 1;
    final int STATE_LINK = 2;
    final int STATE_DESCRIPTION = 3;
    final int STATE_IMAGE = 4;
    private int state = STATE_UNKNOWN;
    private boolean isInItem = false;
    private ArrayList<RSSData> dataList;
    private RSSData data = null;

    public RSSHandler(ArrayList<RSSData> dataList) {
        this.dataList = dataList;
    }
//        @Override
//        public void startDocument() throws SAXException {
//            strTitle = "--- Start Document ---\n";
//        }
//
//        @Override
//        public void endDocument() throws SAXException {
//            strTitle += "--- End Document ---";
//            streamTitle = "Number Of Title: " + String.valueOf(numberOfTitle) + "\n" + strTitle;
//        }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if(localName.equalsIgnoreCase("item")){
            isInItem = true;
            data = new RSSData();
        }

        if(localName.equalsIgnoreCase("title") && isInItem){
            state = STATE_TITLE;
        } else if(localName.equalsIgnoreCase("link") && isInItem){
            state = STATE_LINK;
        } else if(localName.equalsIgnoreCase("description") && isInItem){
            state = STATE_DESCRIPTION;
        } else if(localName.equalsIgnoreCase("image") && isInItem){
            state = STATE_IMAGE;
        } else {
            state = STATE_UNKNOWN;
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
//            endElement를 만났을 때 저장
        if (localName.equalsIgnoreCase("item")) {
            isInItem = false;
            dataList.add(data);
            Log.d("confirm", "endElement() 호출됨,  dataList.size() : " + dataList.size()+"");
//                Log.d("log", data.description);
        }
        state = STATE_UNKNOWN;

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String strCharacters = new String(ch, start, length);
        if (state == STATE_TITLE) {
            data.title = strCharacters;
        } else if (state == STATE_LINK){
            data.link = strCharacters;
        } else if (state == STATE_DESCRIPTION){
            data.description = strCharacters;
        } else if (state == STATE_IMAGE){
            data.img_url = strCharacters;
        }
        Log.d("state", "state : "+ state +", "+ strCharacters);
    }
} // end of class RSSHandler