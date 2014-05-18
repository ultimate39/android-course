package com.ultimate39.android.androidcourse.core.area;

import java.io.InputStream;
import java.util.ArrayList;

public interface AreaDictionaryParser {
    public ArrayList<Area> parseAreas(InputStream in);
}
