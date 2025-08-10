package com.kayky.commons;

import com.kayky.core.pagination.PageResponse;
import org.springframework.data.domain.Page;

public class PageUtils {

    private PageUtils(){
    }

    public static <T> PageResponse<T> pageResponse(Page<T> page){
        return PageResponse.<T>builder()
                .content(page.getContent())
                .currentPage(page.getNumber())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
