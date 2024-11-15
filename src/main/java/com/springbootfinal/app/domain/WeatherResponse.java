package com.springbootfinal.app.domain;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
public class WeatherResponse {

    private Header header;
    private Body body;

    @XmlElement
    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    @XmlElement
    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public static class Header {
        private String resultCode;
        private String resultMsg;

        @XmlElement
        public String getResultCode() {
            return resultCode;
        }

        public void setResultCode(String resultCode) {
            this.resultCode = resultCode;
        }

        @XmlElement
        public String getResultMsg() {
            return resultMsg;
        }

        public void setResultMsg(String resultMsg) {
            this.resultMsg = resultMsg;
        }
    }

    public static class Body {
        private List<Item> items;

        @XmlElement(name = "item")
        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }

        public static class Item {
            private String baseDate;
            private String baseTime;
            private String category;
            private int nx;
            private int ny;
            private String obsrValue;

            @XmlElement
            public String getBaseDate() {
                return baseDate;
            }

            public void setBaseDate(String baseDate) {
                this.baseDate = baseDate;
            }

            @XmlElement
            public String getBaseTime() {
                return baseTime;
            }

            public void setBaseTime(String baseTime) {
                this.baseTime = baseTime;
            }

            @XmlElement
            public String getCategory() {
                return category;
            }

            public void setCategory(String category) {
                this.category = category;
            }

            @XmlElement
            public int getNx() {
                return nx;
            }

            public void setNx(int nx) {
                this.nx = nx;
            }

            @XmlElement
            public int getNy() {
                return ny;
            }

            public void setNy(int ny) {
                this.ny = ny;
            }

            @XmlElement
            public String getObsrValue() {
                return obsrValue;
            }

            public void setObsrValue(String obsrValue) {
                this.obsrValue = obsrValue;
            }
        }
    }
}
