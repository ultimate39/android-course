package com.ultimate39.android.androidcourse.core.area;

import android.util.Log;
import android.util.Xml;
import com.ultimate39.android.androidcourse.ui.vacancy.ActivityVacancies;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class XmlAreaDictionaryParser implements AreaDictionaryParser {
    private final String TAG_ID = "id";
    private final String TAG_NAME = "name";
    private boolean mId = false;
    private boolean mName = false;

    @Override
    public ArrayList<Area> parseAreas(InputStream stream) {
        XmlPullParser parser = Xml.newPullParser();
        ArrayList<Area> areas = new ArrayList<Area>();
        Area area = null;
        try {
            parser.setInput(stream, null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String localName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (localName.equals(TAG_ID)) {
                            area = new Area();
                            mId = true;
                        } else if (localName.equals(TAG_NAME)) {
                            mName = true;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (mId) {
                            area.setId(parser.getText());
                            mId = false;
                        } else if (mName) {
                            area.setName(parser.getText());
                            mName = false;
                            areas.add(area);
                        }
                        break;
                }
                try {
                    eventType = parser.next();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return areas;
    }

    private void printAreas(ArrayList<Area> areas) {
        for (Area area : areas) {
            Log.d(ActivityVacancies.LOG_TAG, "-----------------");
            Log.d(ActivityVacancies.LOG_TAG, "ID:" + area.getId());
            Log.d(ActivityVacancies.LOG_TAG, "Name:" + area.getName());
        }
    }
}
