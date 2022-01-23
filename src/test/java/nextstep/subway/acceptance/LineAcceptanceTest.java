package nextstep.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.applicaion.dto.LineResponse;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {
	/**
	 * When 지하철 노선 생성을 요청 하면
	 * Then 지하철 노선 생성이 성공한다.
	 */
	@DisplayName("지하철 노선 생성")
	@Test
	void createLine() {
		지하철_노선_생성("신분당선", "bg-red-600");
	}

	/**
	 * Given 지하철 노선 생성을 요청 하고
	 * Given 새로운 지하철 노선 생성을 요청 하고
	 * When 지하철 노선 목록 조회를 요청 하면
	 * Then 두 노선이 포함된 지하철 노선 목록을 응답받는다
	 */
	@DisplayName("지하철 노선 목록 조회")
	@Test
	void getLines() {
		// given
		지하철_노선_생성("신분당선", "bg-red-600");
		지하철_노선_생성("5호선", "bg-purple-600");

		// when
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.when().get("/lines")
			.then().log().all().extract();

		// then
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		List<String> lineNames = response.jsonPath().getList(".", LineResponse.class)
			.stream().map(LineResponse::getName)
			.collect(Collectors.toList());
		Assertions.assertThat(lineNames).containsAll(Arrays.asList("신분당선", "5호선"));
	}

	/**
	 * Given 지하철 노선 생성을 요청 하고
	 * When 생성한 지하철 노선 조회를 요청 하면
	 * Then 생성한 지하철 노선을 응답받는다
	 */
	@DisplayName("지하철 노선 조회")
	@Test
	void getLine() {
		// given
		지하철_노선_생성("신분당선", "bg-red-600");

		// when
		ExtractableResponse<Response> response = 지하철_노선_조회(1L);

		// then
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
		Assertions.assertThat(lineResponse.getName()).isEqualTo("신분당선");
	}

	/**
	 * Given 지하철 노선 생성을 요청 하고
	 * When 지하철 노선의 정보 수정을 요청 하면
	 * Then 지하철 노선의 정보 수정은 성공한다.
	 */
	@DisplayName("지하철 노선 수정")
	@Test
	void updateLine() {
		// given
		지하철_노선_생성("신분당선", "bg-red-600");

		Map<String, String> params = new HashMap<>();
		params.put("name", "구분당선");
		params.put("color", "bg-blue-600");

		// when
		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().put("/lines/{id}", 1L)
			.then().log().all().extract();

		// then
		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

		ExtractableResponse<Response> selectResponse = 지하철_노선_조회(1L);

		LineResponse lineResponse = selectResponse.jsonPath().getObject(".", LineResponse.class);
		Assertions.assertThat(lineResponse.getName()).isEqualTo("구분당선");
		Assertions.assertThat(lineResponse.getColor()).isEqualTo("bg-blue-600");
	}

	/**
	 * Given 지하철 노선 생성을 요청 하고
	 * When 생성한 지하철 노선 삭제를 요청 하면
	 * Then 생성한 지하철 노선 삭제가 성공한다.
	 */
	@DisplayName("지하철 노선 삭제")
	@Test
	void deleteLine() {
		// given
		지하철_노선_생성("신분당선", "bg-red-600");

		// when
		ExtractableResponse<Response> response =RestAssured
			.given().log().all()
			.when().delete("/lines/{id}", 1L)
			.then().log().all().extract();

		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	private void 지하철_노선_생성(String name, String color) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", color);

		ExtractableResponse<Response> response = RestAssured
			.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().post("/lines")
			.then().log().all().extract();

		Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	private ExtractableResponse<Response> 지하철_노선_조회(Long id) {
		return RestAssured
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when().get("/lines/{id}", id)
			.then().log().all().extract();
	}
}
