package com.kayky.core.pagination;

import org.springframework.data.domain.Page;

public class PageMapper {

    private PageMapper(){
    }

    public static <T> PageResponse<T> toPageResponse(Page<T> page){
        return PageResponse.<T>builder()
                .content(page.getContent())
                .currentPage(page.getNumber())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
