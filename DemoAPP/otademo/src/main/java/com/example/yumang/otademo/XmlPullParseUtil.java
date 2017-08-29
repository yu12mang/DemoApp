package com.example.yumang.otademo;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Created by wand on 2017/6/29.
 */

public class XmlPullParseUtil {

    private static final String TAG = "XmlPullParser";

    /**
     * parse normal command with PullParser
     *
     * @param content xml string
     * @return ParseResult
     */
    public static ParseResult parse(@NonNull String content) {

        ParseResult result = new ParseResult();

        Reader reader = null;
        try {
            reader = new StringReader(content);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(reader);

            String mCurrentTag = null;
            String mValue = null;

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        mCurrentTag = parser.getName();
//                        Log.i(TAG, "START_TAG:" + mCurrentTag);
                        break;
                    case XmlPullParser.END_TAG:
                        mCurrentTag = null;
//                        Log.i(TAG, "END_TAG");
                        break;
                    case XmlPullParser.TEXT:
                        if (mCurrentTag != null) {

                            Log.i(TAG, "mCurrentTag:" + mCurrentTag + ", TEXT:" + parser.getText());

                            if (mCurrentTag.equals("Cmd")) {
                                result.setCmd(parser.getText());
                            } else if (mCurrentTag.equals("Status")) {
                                result.setStatus(parser.getText());
                            } else if (mCurrentTag.equals("String")) {
                                result.setString(parser.getText());
                            } else if (mCurrentTag.equals("Value")) {
                                result.setValue(parser.getText());
                            } else if (mCurrentTag.equals("SN")) {
                                result.setSN(parser.getText());
                            } else if (mCurrentTag.equals("MacAddr")) {
                                result.setMacAddr(parser.getText());
                            }else if (mCurrentTag.equals("SSID")){
                                result.setSSID(parser.getText());
                            } else if (mCurrentTag.equals("PASSPHRASE")){
                                result.setPASSPHRASE(parser.getText());
                            }
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();

            } // end document

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//            IOUtils.closeQuietly(reader);
        }

        return result;
    }



}
