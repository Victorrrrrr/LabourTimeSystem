package com.example.labourtimesystem.bean;

/**
 * @ProjectName : LabourTimeSystem
 * @Author : Victor Scott
 * @Time : 2022/5/21 12:55
 * @Description : 描述
 */
public class LabourDetailData {
    private int code;
    private Data data;
    private String msg;
    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    public void setData(Data data) {
        this.data = data;
    }
    public Data getData() {
        return data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

    public class Data {

        private int adminId;
        private String beginTime;
        private int capacity;
        private String checkinEndTime;
        private String checkinStartTime;
        private String checkoutEndTime;
        private String checkoutStartTime;
        private String date;
        private String department;
        private String description;
        private String endTime;
        private int id;
        private int isTop;
        private String latitude;
        private String longitude;
        private String name;
        private String photo;
        private String position;
        private double score;
        private int signed;
        private String signupEndTime;
        private String signupStartTime;
        private int state;
        private int status;
        private String term;
        private int type;
        public void setAdminId(int adminId) {
            this.adminId = adminId;
        }
        public int getAdminId() {
            return adminId;
        }

        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }
        public String getBeginTime() {
            return beginTime;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }
        public int getCapacity() {
            return capacity;
        }

        public void setCheckinEndTime(String checkinEndTime) {
            this.checkinEndTime = checkinEndTime;
        }
        public String getCheckinEndTime() {
            return checkinEndTime;
        }

        public void setCheckinStartTime(String checkinStartTime) {
            this.checkinStartTime = checkinStartTime;
        }
        public String getCheckinStartTime() {
            return checkinStartTime;
        }

        public void setCheckoutEndTime(String checkoutEndTime) {
            this.checkoutEndTime = checkoutEndTime;
        }
        public String getCheckoutEndTime() {
            return checkoutEndTime;
        }

        public void setCheckoutStartTime(String checkoutStartTime) {
            this.checkoutStartTime = checkoutStartTime;
        }
        public String getCheckoutStartTime() {
            return checkoutStartTime;
        }

        public void setDate(String date) {
            this.date = date;
        }
        public String getDate() {
            return date;
        }

        public void setDepartment(String department) {
            this.department = department;
        }
        public String getDepartment() {
            return department;
        }

        public void setDescription(String description) {
            this.description = description;
        }
        public String getDescription() {
            return description;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
        public String getEndTime() {
            return endTime;
        }

        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

        public void setIsTop(int isTop) {
            this.isTop = isTop;
        }
        public int getIsTop() {
            return isTop;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }
        public String getLatitude() {
            return latitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
        public String getLongitude() {
            return longitude;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }
        public String getPhoto() {
            return photo;
        }

        public void setPosition(String position) {
            this.position = position;
        }
        public String getPosition() {
            return position;
        }

        public void setScore(double score) {
            this.score = score;
        }
        public double getScore() {
            return score;
        }

        public void setSigned(int signed) {
            this.signed = signed;
        }
        public int getSigned() {
            return signed;
        }

        public void setSignupEndTime(String signupEndTime) {
            this.signupEndTime = signupEndTime;
        }
        public String getSignupEndTime() {
            return signupEndTime;
        }

        public void setSignupStartTime(String signupStartTime) {
            this.signupStartTime = signupStartTime;
        }
        public String getSignupStartTime() {
            return signupStartTime;
        }

        public void setState(int state) {
            this.state = state;
        }
        public int getState() {
            return state;
        }

        public void setStatus(int status) {
            this.status = status;
        }
        public int getStatus() {
            return status;
        }

        public void setTerm(String term) {
            this.term = term;
        }
        public String getTerm() {
            return term;
        }

        public void setType(int type) {
            this.type = type;
        }
        public int getType() {
            return type;
        }

    }
}
