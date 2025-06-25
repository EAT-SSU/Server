package ssu.eatssu.domain.admin.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageWrapper<T>(List<T> content, int totalPages, long totalElements, int number, int size,
                             int numberOfElements,
                             boolean isFirst, boolean isLast) {
    public PageWrapper(Page<T> page) {
        this(page.getContent(), page.getTotalPages(), page.getTotalElements(), page.getNumber(), page.getSize(),
             page.getNumberOfElements(), page.isFirst(), page.isLast());
    }
}
