package com.suncaption.schoolfood;

import android.os.Parcel;
import android.os.Parcelable;

class SchoolListItem implements Parcelable {



    String kraOrgNm;
    String schoolType;
    String schoolCode;
    String zipAdres;
    String schoolGrade;
    String schoolClass;

    public String getSchoolGrade() {
        return schoolGrade;
    }

    public void setSchoolGrade(String schoolGrade) {
        this.schoolGrade = schoolGrade;
    }

    public String getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(String schoolClass) {
        this.schoolClass = schoolClass;
    }

    public SchoolListItem(String kraOrgNm, String schoolType, String schoolCode
            , String schoolGrade , String schoolClass) {
        this.kraOrgNm = kraOrgNm;
        this.schoolType = schoolType;
        this.schoolCode = schoolCode;
        this.zipAdres = zipAdres;
        this.schoolGrade = schoolGrade;
        this.schoolClass = schoolClass;
    }

    public String getZipAdres() {
        return zipAdres;
    }

    public void setZipAdres(String zipAdres) {
        this.zipAdres = zipAdres;
    }

    public String getKraOrgNm() {
        return kraOrgNm;
    }

    public void setKraOrgNm(String kraOrgNm) {
        this.kraOrgNm = kraOrgNm;
    }

    public String getSchoolType() {
        return schoolType;
    }

    public void setSchoolType(String schoolType) {
        this.schoolType = schoolType;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.kraOrgNm);
        dest.writeString(this.schoolType);
        dest.writeString(this.schoolCode);
        dest.writeString(this.zipAdres);
        dest.writeString(this.schoolGrade);
        dest.writeString(this.schoolClass);
    }

    protected SchoolListItem(Parcel in) {
        this.kraOrgNm = in.readString();
        this.schoolType = in.readString();
        this.schoolCode = in.readString();
        this.zipAdres = in.readString();
        this.schoolGrade = in.readString();
        this.schoolClass = in.readString();
    }

    public static final Creator<SchoolListItem> CREATOR = new Creator<SchoolListItem>() {
        @Override
        public SchoolListItem createFromParcel(Parcel source) {
            return new SchoolListItem(source);
        }

        @Override
        public SchoolListItem[] newArray(int size) {
            return new SchoolListItem[size];
        }
    };
}
