package nextstep.subway.applicaion.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.domain.Line;

public class LineResponse {
	private Long id;
	private String name;
	private String color;
	private List<SectionResponse> sections;
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;

	public LineResponse(Long id, String name, String color, List<SectionResponse> sections,
		LocalDateTime createdDate, LocalDateTime modifiedDate) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.sections = sections;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getModifiedDate() {
		return modifiedDate;
	}

	public List<SectionResponse> getSections() {
		return sections;
	}

	public static LineResponse of(Line line) {
		List<SectionResponse> sectionResponses = line.getSections().stream()
			.map(SectionResponse::of)
			.collect(Collectors.toList());

		return new LineResponse(line.getId(), line.getName(), line.getColor(), sectionResponses, line.getCreatedDate(),
			line.getModifiedDate());
	}
}
