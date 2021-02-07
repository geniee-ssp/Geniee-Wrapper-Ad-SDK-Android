package jp.co.geniee.gnwrapperadsdk.parsers;

import android.util.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import jp.co.geniee.gnwrapperadsdk.enums.GNErrorCode;
import jp.co.geniee.gnwrapperadsdk.utils.StringUtils;
import jp.co.geniee.gnwrapperadsdk.loggers.Log;
import jp.co.geniee.gnwrapperadsdk.params.GNHBParams;
import jp.co.geniee.gnwrapperadsdk.params.GNUPRParams;
import jp.co.geniee.gnwrapperadsdk.params.GNWrapperParams;
import jp.co.geniee.gnwrapperadsdk.listeners.GNJsonParserListener;

public class GNJsonParser {
    private static final String TAG = "GNJsonParser";

    private static final String UNIT_ID = "unit_id";
    private static final String USE_HB = "use_hb";
    private static final String USE_UPR = "use_upr";
    private static final String IS_REFRESH = "is_refresh";
    private static final String REFRESH_INTERVAL = "refresh_interval";
    private static final String UPR_SETTINGS = "upr_settings";
    private static final String TIMEOUT = "timeout";
    private static final String HB_LIST = "hb_list";
    private static final String HB_NAME = "hb_name";
    private static final String HB_VALUES = "hb_values";
    private static final String UPR_KEY = "upr_key";
    private static final String UPR_VALUE = "upr_value";

    private final GNJsonParserListener gnJsonParserListener;
    private GNWrapperParams gnWrapperParams;

    public GNJsonParser(GNJsonParserListener gnJsonParserListener) {
        this.gnJsonParserListener = gnJsonParserListener;
    }


    public void getJsonParamWithString(String gnGBRemoteConfigValue) {
        gnWrapperParams = new GNWrapperParams();
        InputStream inputStream = new ByteArrayInputStream(gnGBRemoteConfigValue.getBytes());
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        if(parseGNHBJson(jsonReader)) {
            Log.d(TAG, "Json parse complete");
            gnJsonParserListener.onJsonParseComplete(gnWrapperParams);
        } else {
            gnJsonParserListener.onJsonParseError(GNErrorCode.JSON_PARSE_ERROR);
        }
    }

    private boolean parseGNHBJson(JsonReader jsonReader) {
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case UNIT_ID:
                        gnWrapperParams.setAdUnit(jsonReader.nextString());
                        Log.d(TAG, UNIT_ID + ": " + gnWrapperParams.getAdUnit());
                        break;
                    case TIMEOUT:
                        gnWrapperParams.setTimeout(jsonReader.nextDouble());
                        Log.d(TAG, TIMEOUT + ": " + gnWrapperParams.getTimeout());
                        break;
                    case IS_REFRESH:
                        gnWrapperParams.setIsRefresh(jsonReader.nextBoolean());
                        Log.d(TAG, IS_REFRESH + ": " + gnWrapperParams.isRefresh());
                        break;
                    case REFRESH_INTERVAL:
                        gnWrapperParams.setRefreshInterval(jsonReader.nextDouble());
                        Log.d(TAG, REFRESH_INTERVAL + ": " + gnWrapperParams.getRefreshInterval());
                        break;
                    case USE_UPR:
                        gnWrapperParams.setUseUPR(jsonReader.nextBoolean());
                        Log.d(TAG, USE_UPR + ": " + gnWrapperParams.getUseUPR());
                        break;
                    case UPR_SETTINGS:
                        parseUPRSettings(jsonReader);
                        break;
                    case USE_HB:
                        gnWrapperParams.setUseHB(jsonReader.nextBoolean());
                        Log.d(TAG, USE_HB + ": " + gnWrapperParams.getUseHB());
                        break;
                    case HB_LIST:
                        parseHBList(jsonReader);
                        break;
                    default:
                        Log.w(TAG, "Parameter does not exist. line: " + jsonReader.toString());
                        break;
                }
            }
            jsonReader.endObject();
            if (StringUtils.isNullOrEmpty(gnWrapperParams.getAdUnit())) {
                Log.w(TAG, UNIT_ID + " is Nothing");
                return false;
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, e);
            return false;
        }
    }

    private void parseUPRSettings(JsonReader jsonReader) {
        try {
            GNUPRParams gnUPRParams = new GNUPRParams();
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                switch (jsonReader.nextName()) {
                    case UPR_KEY:
                        gnUPRParams.setUPRKey(jsonReader.nextString());
                        Log.d(TAG, UPR_KEY + ": " + gnUPRParams.getUPRKey());
                        break;
                    case UPR_VALUE:
                        gnUPRParams.setUPRValue(jsonReader.nextString());
                        Log.d(TAG, UPR_VALUE + ": " + gnUPRParams.getUPRValue());
                        break;
                    default:
                        Log.w(TAG, "Parameter does not exist. line: " + jsonReader.toString());
                        break;
                }
            }
            if (StringUtils.isNullOrEmpty(gnUPRParams.getUPRKey())) {
                Log.w(TAG, UPR_KEY + " is Nothing");
            } else if (StringUtils.isNullOrEmpty(gnUPRParams.getUPRValue())) {
                Log.w(TAG, UPR_VALUE + " is Nothing");
            } else {
                gnWrapperParams.setGnUPRParams(gnUPRParams);
            }
            jsonReader.endObject();
        } catch (IOException e) {
            Log.e(TAG, e);
        }
    }

    private void parseHBList(JsonReader jsonReader) {
        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                GNHBParams gnHBParams = new GNHBParams();
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    switch (jsonReader.nextName()) {
                        case HB_NAME:
                            gnHBParams.setHbName(jsonReader.nextString());
                            Log.d(TAG, HB_NAME + ": " + gnHBParams.getHbName());
                            break;
                        case HB_VALUES:
                            gnHBParams.setHbRequestParams(parseHBRequestValues(jsonReader));
                            Log.d(TAG, HB_VALUES + ": " + gnHBParams.getHbRequestParams());
                            break;
                        default:
                            Log.w(TAG, "Parameter does not exist. line: " + jsonReader.toString());
                            break;
                    }
                }
                if (!StringUtils.isNullOrEmpty(gnHBParams.getHbName())) {
                    gnWrapperParams.addHbRequestParam(gnHBParams.getHbName(), gnHBParams);
                } else {
                    Log.w(TAG, HB_NAME + " is Nothing");
                }
                jsonReader.endObject();
            }
            jsonReader.endArray();
        } catch (IOException e) {
            Log.e(TAG, e);
        }
    }

    private Map<String, String> parseHBRequestValues(JsonReader jsonReader) {
        Map<String, String> hbRequestValueMap = new HashMap<String, String>();
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                hbRequestValueMap.put(jsonReader.nextName(), jsonReader.nextString());
            }
            jsonReader.endObject();
        } catch (IOException e) {
            Log.e(TAG, e);
        }
        return hbRequestValueMap;
    }
}
