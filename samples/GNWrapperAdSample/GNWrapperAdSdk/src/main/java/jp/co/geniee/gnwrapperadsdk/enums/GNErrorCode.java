package jp.co.geniee.gnwrapperadsdk.enums;

public enum GNErrorCode {
    JSON_PARSE_ERROR(2000, "An error occurred in json parsing");

    private final int code;

    private final String message;

    GNErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
