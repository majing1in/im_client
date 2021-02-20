package com.xiaoma.im.enums;

public enum GenderEnum {
    GENDER_MAN(0,"男"),
    GENDER_WOMAM(1,"女");

    private Integer code;
    private String gender;

    GenderEnum(Integer code, String gender) {
        this.code = code;
        this.gender = gender;
    }

    public Integer getCode() {
        return code;
    }


    public String getGender() {
        return gender;
    }

    public static int getGender(String gender) {
        GenderEnum[] values = GenderEnum.values();
        for (GenderEnum value : values) {
            if (value.getGender().equals(gender)) {
                return value.getCode();
            }
        }
        return 0;
    }

}
