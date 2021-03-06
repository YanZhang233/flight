package com.zz.flight.common;

public class Const {
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";


    public static final String USER_NAME = "username";

    public static final String PHONE = "phone";

    public static final String ADMIN_TOKEN = "zhengzhuo&zhangyan2018";

    public interface Role {
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1; //管理员
        int ROLE_V0LUNTEER = 2;//接机人
    }

    public interface EmailChecked{
        int EMAIL_VALID = 0;
        int EMAIL_INVALID = 1;
    }

    public interface RequestStatus{
        int REQUEST_CANCELED = -1;
        int REQUEST_NOT_TAKEN = 0;
        int REQUEST_TAKEN = 1;
        int REQUEST_COMPLETED = 2;
    }

    public interface VolunteerStatus{
        int VOLUNTEER_CHECKED = 0;
        int VOLUNTEER_UNCHECKED = 1;
    }

    public interface Status{
        int USER_VALID = 0;
        int USER_INVALID = 1;
    }
}