package com.xiaoma.im.enums;

public enum ConstellationEnum {
    CONSTELLATION_ONE(1, "白羊座"),
    CONSTELLATION_TWO(2, "金牛座"),
    CONSTELLATION_THREE(3, "双子座"),
    CONSTELLATION_FOUR(4, "巨蟹座"),
    CONSTELLATION_FIVE(5, "狮子座"),
    CONSTELLATION_SIX(6, "处女座"),
    CONSTELLATION_SEVEN(7, "天秤座"),
    CONSTELLATION_EIGHT(8, "天蝎座"),
    CONSTELLATION_NINE(9, "射手座"),
    CONSTELLATION_TEN(10, "摩羯座"),
    CONSTELLATION_ELEVEN(11, "水瓶座"),
    CONSTELLATION_TWELVE(12, "双鱼座")
    ;

    private Integer code;
    private String message;

    ConstellationEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static String getMessage(int code) {
        ConstellationEnum[] values = ConstellationEnum.values();
        for (ConstellationEnum value : values) {
            if (value.getCode().equals(code)) {
                return value.getMessage();
            }
        }
        return null;
    }

    public static int getMessage(String message) {
        ConstellationEnum[] values = ConstellationEnum.values();
        for (ConstellationEnum value : values) {
            if (value.getMessage().equals(message)) {
                return value.getCode();
            }
        }
        return 0;
    }

}
