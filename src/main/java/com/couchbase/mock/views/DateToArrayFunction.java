package com.couchbase.mock.views;

import org.json.simple.JSONArray;
import org.mozilla.javascript.*;

import java.util.Calendar;

public class DateToArrayFunction extends BaseFunction {

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        Calendar calendar = javax.xml.bind.DatatypeConverter.parseDateTime(args[0].toString());

        JSONArray arr = new JSONArray();
        arr.add(calendar.get(Calendar.YEAR));
        arr.add(calendar.get(Calendar.MONTH) + 1);
        arr.add(calendar.get(Calendar.DAY_OF_MONTH));
        arr.add(calendar.get(Calendar.HOUR_OF_DAY));
        arr.add(calendar.get(Calendar.MINUTE));
        arr.add(calendar.get(Calendar.SECOND));

        return NativeJSON.parse(cx, scope, arr.toJSONString(), new Callable() {
            @Override
            public Object call(Context context, Scriptable scriptable, Scriptable scriptable1, Object[] objects) {
                return objects[1];
            }
        });
    }
}
