package com.suncaption.schoolfood;

class SchoolAddItem {
    String kraOrgNm;
    String schoolType;
    String schoolCode;
    String zipAdres;

    public SchoolAddItem(String kraOrgNm, String schoolType, String schoolCode, String zipAdres) {
        this.kraOrgNm = kraOrgNm;
        this.schoolType = schoolType;
        this.schoolCode = schoolCode;
        this.zipAdres = zipAdres;
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
}
