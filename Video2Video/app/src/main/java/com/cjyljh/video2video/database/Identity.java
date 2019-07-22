package com.cjyljh.video2video.database;

public enum Identity {
    /**/
    ORDINARY_USER(0, "会员"), VIP(1, "超级会员"), ADMINISTRATOR(2, "管理员");

    public final int intValue;
    public final String strValue;

    Identity(int intValue, String strValue){
        this.intValue = intValue;
        this.strValue = strValue;
    }

    public static Identity from(int intValue) {
        for(Identity identity : Identity.values()) {
            if(identity.intValue == intValue) {
                return identity;
            }
        }
        return ORDINARY_USER;
    }
}
