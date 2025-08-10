package com.kayky.core.pagination;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int currentPage;
    private long totalElements;
    private int totalPages;
}
