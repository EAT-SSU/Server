package ssu.eatssu.domain.admin.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public record PageWrapper<T>(List<T> content, int totalPages, long totalElements, int number, int size,
							 int numberOfElements,
							 boolean isFirst, boolean isLast) {
	public PageWrapper(Page<T> page) {
		this(page.getContent(), page.getTotalPages(), page.getTotalElements(), page.getNumber(), page.getSize(),
			page.getNumberOfElements(), page.isFirst(), page.isLast());
	}
}
