package com.held.retrofit.response;

/**
 * Created by MAHESH on 9/15/2015.
 */
public class User {

        String rid;
        String pin;
        String profilePic;
        String displayName;
        String phone;
        String joinDate;

        public String getJoinDate() {
                return joinDate;
        }

        public String getDisplayName() {
                return displayName;
        }

        public String getProfilePic() {
                return profilePic;
        }

        public String getPhone() {
                return phone;
        }

        public String getPin() {
                return pin;
        }

        public String getRid(){return rid;}

        public void setDisplayName(String displayName) {
                this.displayName = displayName;
        }

        public void setProfilePic(String profilePic) {
                this.profilePic = profilePic;
        }

}
