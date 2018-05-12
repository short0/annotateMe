package com.example.user.mobilemicroscopy;

import java.io.Serializable;

/**
 * Class to describe the image. It implements Serializable interface so it could be passed in an intent
 */
public class Image implements Serializable {

    // Define all member variables
    private int id;
    private String date;
    private String time;
    private String specimenType;
    private String originalFileName;
    private String annotatedFileName;
    private String gpsPosition;
    private String magnification;
    private String originalImageLink;
    private String annotatedImageLink;
    private String studentComment;
    private String teacherComment;
    private String username;

    /**
     * Constructor WITH id
     */
    public Image(int id,
                 String date,
                 String time,
                 String specimenType,
                 String originalFileName,
                 String annotatedFileName,
                 String gpsPosition,
                 String magnification,
                 String originalImageLink,
                 String annotatedImageLink,
                 String studentComment,
                 String teacherComment,
                 String username) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.specimenType = specimenType;
        this.originalFileName = originalFileName;
        this.annotatedFileName = annotatedFileName;
        this.gpsPosition = gpsPosition;
        this.magnification = magnification;
        this.originalImageLink = originalImageLink;
        this.annotatedImageLink = annotatedImageLink;
        this.studentComment = studentComment;
        this.teacherComment = teacherComment;
        this.username = username;
    }

    /**
     * Constructor WITHOUT id
     */
    public Image(String date,
                 String time,
                 String specimenType,
                 String originalFileName,
                 String annotatedFileName,
                 String gpsPosition,
                 String magnification,
                 String originalImageLink,
                 String annotatedImageLink,
                 String studentComment,
                 String teacherComment,
                 String username) {
        this.date = date;
        this.time = time;
        this.specimenType = specimenType;
        this.originalFileName = originalFileName;
        this.annotatedFileName = annotatedFileName;
        this.gpsPosition = gpsPosition;
        this.magnification = magnification;
        this.originalImageLink = originalImageLink;
        this.annotatedImageLink = annotatedImageLink;
        this.studentComment = studentComment;
        this.teacherComment = teacherComment;
        this.username = username;
    }

    /**
     * Empty Constructor
     */
    public Image() {

    }

    // Accessor methods
    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getSpecimenType() {
        return specimenType;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getAnnotatedFileName() {
        return annotatedFileName;
    }

    public String getGpsPosition() {
        return gpsPosition;
    }

    public String getMagnification() {
        return magnification;
    }

    public String getOriginalImageLink() {
        return originalImageLink;
    }

    public String getAnnotatedImageLink() {
        return annotatedImageLink;
    }

    public String getStudentComment() {
        return studentComment;
    }

    public String getTeacherComment() {
        return teacherComment;
    }

    public String getUsername() {
        return username;
    }

    // Mutator methods
    public void setId(int id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSpecimenType(String specimenType) {
        this.specimenType = specimenType;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public void setAnnotatedFileName(String annotatedFileName) {
        this.annotatedFileName = annotatedFileName;
    }

    public void setGpsPosition(String gpsPosition) {
        this.gpsPosition = gpsPosition;
    }

    public void setMagnification(String magnification) {
        this.magnification = magnification;
    }

    public void setOriginalImageLink(String originalImageLink) {
        this.originalImageLink = originalImageLink;
    }

    public void setAnnotatedImageLink(String annotatedImageLink) {
        this.annotatedImageLink = annotatedImageLink;
    }

    public void setStudentComment(String studentComment) {
        this.studentComment = studentComment;
    }

    public void setTeacherComment(String teacherComment) {
        this.teacherComment = teacherComment;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
