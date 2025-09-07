package com.kayky.commons;

import com.kayky.core.pagination.PageResponse;
import com.kayky.domain.operation.OperationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

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

    public static <T> Page<T> toPage(List<T> list) {
        return new PageImpl<>(list, PageRequest.of(0, 10), list.size());
    }
}
