package com.springbootfinal.app.domain;

import lombok.Data;

import java.util.List;

/*@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor*/
@Data
public class LongWeatherDto {

	private Response response;

    public void setMessage(String 기본값_반환) {
    }

    @Data
	public static class Response {
		private Header header;
		private Body body;
	}

	@Data
	public static class Header {
		private String resultCode;
		private String resultMsg;
	}

	@Data
	public static class Body {
		private String dataType;
		private Items items;
		private Integer pageNo;
		private Integer numOfRows;
		private Integer totalCount;
	}

	@Data
	public static class Items {
		private List<Item> item;
	}

	@Data
	public static class Item {
		private String regId;
		private Integer rnSt4Am;
		private Integer rnSt4Pm;
		private Integer rnSt5Am;
		private Integer rnSt5Pm;
		private Integer rnSt6Am;
		private Integer rnSt6Pm;
		private Integer rnSt7Am;
		private Integer rnSt7Pm;
		private Integer rnSt8;
		private Integer rnSt9;
		private Integer rnSt10;
		private String wf4Am;
		private String wf4Pm;
		private String wf5Am;
		private String wf5Pm;
		private String wf6Am;
		private String wf6Pm;
		private String wf7Am;
		private String wf7Pm;
		private String wf8;
		private String wf9;
		private String wf10;
	}
}