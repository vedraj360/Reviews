package com.vdx.reviews.Models;


public class CourseModel {

    private String CourseName;
    private String Mentor;
    private String url;


    public CourseModel() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getCourseName() {
        return CourseName;
    }

    public void setCourseName(String courseName) {
        CourseName = courseName;
    }

    public String getMentor() {
        return Mentor;
    }

    public void setMentor(String mentor) {
        Mentor = mentor;
    }
}
