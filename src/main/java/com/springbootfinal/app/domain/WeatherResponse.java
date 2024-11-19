package com.springbootfinal.app.domain;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WeatherResponse {
    private Response response;

    // Getter, Setter

    public static class Response {
        private Header header;
        private Body body;

        // Getter, Setter

        public static class Header {
            private String resultCode;
            private String resultMsg;

            // Getter, Setter
        }

        public static class Body {
            private String dataType;
            private Items items;

            // Getter, Setter

            public static class Items {
                private List<Item> item;

                // Getter, Setter

                public static class Item {
                    private String baseDate;
                    private String baseTime;
                    private String category;
                    private int nx;
                    private int ny;
                    private String obsrValue;

                    // Getter, Setter
                }
            }
        }
    }
}

