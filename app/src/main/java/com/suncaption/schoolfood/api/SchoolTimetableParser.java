package com.suncaption.schoolfood.api;

import java.util.ArrayList;
import java.util.List;

/**
 * NEIS API
 * 전국 교육청 소속 교육기관의 학사일정, 메뉴를 간단히 불러올 수 있습니다.
 *
 * @author HyunJun Kim
 * @version 3.1
 */
class SchoolTimetableParser {

    /**
     * 웹에서 가져온 데이터를 바탕으로 급식 메뉴를 파싱합니다.
     */
    static List<SchoolTimetable> parse(String rawData) throws SchoolException {

        if (rawData.length() < 1)
            throw new SchoolException("불러온 데이터가 올바르지 않습니다.");

        List<SchoolTimetable> monthlyTimetable = new ArrayList<>();

        /*
         파싱 편의를 위해 모든 공백을 제거합니다.
         급식 메뉴의 이름에는 공백이 들어가지 않으므로, 파싱 결과에는 영향을 주지 않습니다.
         */
        rawData = rawData.replaceAll("\\s+", "");

        /*
         <div> - </div> 쌍을 찾아 그 사이의 데이터를 추출합니다.
         */
        StringBuilder buffer = new StringBuilder();

        boolean inDiv = false;

        try {
            for (int i = 0; i < rawData.length(); i++) {
                if (rawData.charAt(i) == 'v') {
                    if (inDiv) {
                        buffer.delete(buffer.length() - 4, buffer.length());
                        if (buffer.length() > 0)
                            monthlyTimetable.add(new SchoolTimetable(buffer.toString()));
                        buffer.setLength(0);
                    } else {
                        i++;
                    }
                    inDiv = !inDiv;
                } else if (inDiv) {
                    buffer.append(rawData.charAt(i));
                }
            }

            return monthlyTimetable;

        } catch (Exception e) {
            throw new SchoolException("시간표 정보 파싱에 실패했습니다. API를 최신 버전으로 업데이트 해 주세요.");
        }
    }


}
