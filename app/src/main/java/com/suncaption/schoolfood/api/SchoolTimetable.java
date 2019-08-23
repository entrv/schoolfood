package com.suncaption.schoolfood.api;

/**
 * NEIS API
 * 전국 교육청 소속 교육기관의 학사일정, 메뉴를 간단히 불러올 수 있습니다.
 *
 * @author HyunJun Kim
 * @version 3.1
 */
public class SchoolTimetable {

    public String timetable;

    /**
     * 일정이 없을 경우
     */
    public SchoolTimetable() {
        timetable = "";
    }

    /**
     * 일정이 있을 경우
     *
     * @param timetable 학사일정 인스턴스
     */
    public SchoolTimetable(String timetable) {
        this.timetable = timetable;
    }

    @Override
    public String toString() {
        return timetable;
    }
}
