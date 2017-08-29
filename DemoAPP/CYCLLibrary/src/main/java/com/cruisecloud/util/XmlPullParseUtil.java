package com.cruisecloud.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.cruisecloud.model.MediaFile;
import com.cruisecloud.model.ParseResult;
import com.yanzhenjie.nohttp.tools.IOUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

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
                                result.cmd = parser.getText();
                            } else if (mCurrentTag.equals("Status")) {
                                result.status = parser.getText();
                            } else if (mCurrentTag.equals("String")) {
                                result.strValue = parser.getText();
                            } else if (mCurrentTag.equals("Value")) {
                                result.value = parser.getText();
                            } else if (mCurrentTag.equals("SN")) {
                                result.setSN(parser.getText());
                            } else if (mCurrentTag.equals("MacAddr")) {
                                result.setMacAddr(parser.getText());
                            }else if (mCurrentTag.equals("SSID")){
                                result.setSSID(parser.getText());
                            } else if (mCurrentTag.equals("PASSPHRASE")){
                                result.setPassPhrase(parser.getText());
                            } else if (mCurrentTag.equals("MovieLiveViewLink")){
                                result.setMovieLiveViewLink(parser.getText());
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
            IOUtils.closeQuietly(reader);
        }

        return result;
    }

    /**
     * parse file list with PullParser
     *
     * @param content xml string
     * @return ParseResult include file list
     */
    public static ParseResult parseFiles(@NonNull String content) {
        ParseResult result = null;
        Reader reader = new StringReader(content);
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(reader);

            result = parseFiles(parser);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return result;
    }

    /**
     * parse file list with PullParser
     *
     * @param file xml file
     * @return ParseResult include file list
     */
    public static ParseResult parseFiles(@NonNull File file) {
        ParseResult result = null;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, "utf-8");

            result = parseFiles(parser);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return result;
    }

    private static ParseResult parseFiles(XmlPullParser parser) {
        ParseResult result = null;
        ArrayList<MediaFile> videoList = null;
        ArrayList<MediaFile> photoList = null;
        MediaFile file = null;

        String mCurrentTag = null;
        String mValue = null;
        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        mCurrentTag = parser.getName();
//                        CCLog.i(TAG, "START_TAG:" + mCurrentTag);
                        if (mCurrentTag.equals("LIST")) {
                            result = new ParseResult();
                            videoList = new ArrayList<>();
                            photoList = new ArrayList<>();
                        } else if (mCurrentTag.equals("ALLFile")) {
                        } else if (mCurrentTag.equals("File")) {
                            file = new MediaFile();
                        }
                    }
                    break;
                    case XmlPullParser.END_TAG: {
                        mCurrentTag = parser.getName();
//                        CCLog.i(TAG, "END_TAG:" + mCurrentTag);
                        if (mCurrentTag.equals("File")) {
                            if(file.flag == 0) {
                                videoList.add(file);
                            } else if (file.flag == 1) {
                                photoList.add(file);
                            } else if (file.flag == -1) {
                                videoList.add(file);
                                photoList.add(file);
                            }

                            file = null;
                        } else if (mCurrentTag.equals("LIST")) {
                            result.setVideoList(videoList);
                            result.setPhotoList(photoList);
                        }
                        mCurrentTag = null;
                    }
                    break;
                    case XmlPullParser.TEXT: {
                        if (mCurrentTag != null) {
                            CCLog.i(TAG, "mCurrentTag:" + mCurrentTag + ", TEXT:" + parser.getText());

                            if (mCurrentTag.equals("NAME")) {
                                file.name = parser.getText();
                                if(file.name.contains("JPG") || file.name.contains("JPEG")){
                                    file.flag = 1;
                                } else {
                                    if (file.name.length() > 21){
                                        file.name = file.name.substring(0,16) + file.name.substring(20, file.name.length());
                                    }
                                    file.flag = 0;
                                }
                            } else if (mCurrentTag.equals("FPATH")) {
                                file.fPath = parser.getText();
                            } else if (mCurrentTag.equals("SIZE")) {
                                mValue = parser.getText();
                                if (!TextUtils.isEmpty(mValue.trim())) {
                                    file.size = Integer.valueOf(mValue) / 1024 / 1024;
                                }
                            } else if (mCurrentTag.equals("TIMECODE")) {
                                mValue = parser.getText();
                                if (!TextUtils.isEmpty(mValue.trim())) {
                                    file.timeCode = Integer.valueOf(mValue);
                                }
                            } else if (mCurrentTag.equals("TIME")) {
                                String[] times = parser.getText().split(" ");
                                if(times.length > 1) {
                                    file.date = times[0];
                                    file.time = times[1];
                                }
                            } else if (mCurrentTag.equals("ATTR")) {
                                file.attr = parser.getText();
                            } else if (mCurrentTag.equals("FLAG")) {
                                // flag file end
                                if (parser.getText().contains("1"))
                                    file.flag = -1;
                            } else if (mCurrentTag.equals("Resolution")) {
                                file.resolution = parser.getText();
                            }
                        }
                    }
                    break;
                    default: {
                    }
                    break;
                }
                eventType = parser.next();

            } // end document

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
    public static List<ParseResult> parseCurSetting(@NonNull String content) {

        List<ParseResult> list = new ArrayList<>();
        ParseResult parseResult= null;

        String mCurrentTag = null;
        Reader reader = null;
        try {
            reader = new StringReader(content);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(reader);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        mCurrentTag = parser.getName();
//                        CCLog.i(TAG, "START_TAG:" + mCurrentTag);
                        if (mCurrentTag.equals("Function")) {
//                      } else if (mCurrentTag.equals("ALLFile")) {
                        } else if (mCurrentTag.equals("Cmd")) {
                            parseResult = new ParseResult();
                        }
                    }
                    break;


                    case XmlPullParser.TEXT: {
                        if (mCurrentTag != null) {
                            CCLog.i(TAG, "mCurrentTag:" + mCurrentTag + ", TEXT:" + parser.getText());

                            if (mCurrentTag.equals("Cmd")) {
                                parseResult.setCmd(parser.getText());
                            } else if (mCurrentTag.equals("Status")) {
                                parseResult.setStatus(parser.getText());
                            }
                        }
                    }
                    break;

                    case XmlPullParser.END_TAG: {
                        mCurrentTag = parser.getName();
//                        CCLog.i(TAG, "END_TAG:" + mCurrentTag);
                        if (mCurrentTag.equals("Status")) {
                            list.add(parseResult);
                            parseResult = null;
                        } else if (mCurrentTag.equals("Function")) {

                        }
                        mCurrentTag = null;
                    }
                    break;
                    default: {
                    }
                    break;
                }
                eventType = parser.next();

            } // end document

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return list;
    }



    public static List<ParseResult> parseVideoRecSize(@NonNull String content) {

        List<ParseResult> list = new ArrayList<>();
        ParseResult parseResult= null;

        String mCurrentTag = null;
        Reader reader = null;
        try {
            reader = new StringReader(content);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(reader);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        mCurrentTag = parser.getName();
//                        CCLog.i(TAG, "START_TAG:" + mCurrentTag);
                        if (mCurrentTag.equals("LIST")) {
//                      } else if (mCurrentTag.equals("ALLFile")) {
                        } else if (mCurrentTag.equals("Item")) {
                            parseResult = new ParseResult();
                        }
                    }
                    break;


                    case XmlPullParser.TEXT: {
                        if (mCurrentTag != null) {
                            CCLog.i(TAG, "mCurrentTag:" + mCurrentTag + ", TEXT:" + parser.getText());

                            if (mCurrentTag.equals("Name")) {
                                parseResult.setRecName(parser.getText());
                            } else if (mCurrentTag.equals("Index")) {
                                parseResult.setIndex(parser.getText());
                            }
                        }
                    }
                    break;

                    case XmlPullParser.END_TAG: {
                        mCurrentTag = parser.getName();
//                        CCLog.i(TAG, "END_TAG:" + mCurrentTag);
                        if (mCurrentTag.equals("Item")) {
                            list.add(parseResult);
                            parseResult = null;
                        } else if (mCurrentTag.equals("LIST")) {

                        }
                        mCurrentTag = null;
                    }
                    break;
                    default: {
                    }
                    break;
                }
                eventType = parser.next();

            } // end document

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return list;
    }

    public static ArrayList<String> parseCyclic(String result){
        ArrayList<String> list = new ArrayList<>();

        Reader reader = null;
        try {
            reader = new StringReader(result);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(reader);

            String mCurrentTag = null;

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        mCurrentTag = parser.getName();
                        break;
                    case XmlPullParser.END_TAG:
                        mCurrentTag = null;
                        break;
                    case XmlPullParser.TEXT:
                        if (mCurrentTag != null) {

                            Log.i(TAG, "mCurrentTag:" + mCurrentTag + ", TEXT:" + parser.getText());

                            if (mCurrentTag.equals("Id")) {
                               list.add(parser.getText());
                            }
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();

            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return list;
    }


}
