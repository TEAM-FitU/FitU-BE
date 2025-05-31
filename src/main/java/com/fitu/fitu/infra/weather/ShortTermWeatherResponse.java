package com.fitu.fitu.infra.weather;

import lombok.Data;

import java.util.List;

@Data
public class ShortTermWeatherResponse {
    private Response response;

    @Data
    static class Response {
        private Header header;
        private Body body;
    }

    @Data
    static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Data
    static class Body {
        private String dataType;
        private Items items;
        private int pageNo;
        private int numOfRows;
        private int totalCount;
    }

    @Data
    static class Items {
        private List<Item> item;
    }

    @Data
    static class Item {
        private String baseDate;
        private String baseTime;
        private String fcstDate;
        private String fcstTime;
        private String category;
        private String fcstValue;
        private int nx;
        private int ny;
    }
}
