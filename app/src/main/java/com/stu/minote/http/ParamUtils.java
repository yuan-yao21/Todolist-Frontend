package com.stu.minote.http;//package com.pda.hospital.http;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ParamUtils {

    public static RequestBody getRequestBody(String value) {

        RequestBody bodyStr = RequestBody.create(MediaType.parse("text/plain"), value);

        return bodyStr;
    }
}
