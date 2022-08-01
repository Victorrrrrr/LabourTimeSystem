package com.example.labourtimesystem.bean;

/**
 * @ProjectName : LabourTimeSystem
 * @Author : Victor Scott
 * @Time : 2022/5/25 15:32
 * @Description : 描述
 */
public class JsonEditPwd {

    private Data data;
    public void setData(Data data) {
        this.data = data;
    }
    public Data getData() {
        return data;
    }

    public class Data {

        private String oldPassword;
        private String newPassword;
        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }
        public String getOldPassword() {
            return oldPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
        public String getNewPassword() {
            return newPassword;
        }
    }
}
