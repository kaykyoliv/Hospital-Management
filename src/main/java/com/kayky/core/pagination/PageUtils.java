package com.kayky.core.pagination;

import org.springframework.data.domain.Page;

import java.util.function.Function;

public class PageUtils {

    private PageUtils(){
    }

    public static <T, R> PageResponse<R> mapPage(Page<T> page, Function<T, R> mapper){
        Page<R> mappedPage = page.map(mapper);
        return PageMapper.toPageResponse(mappedPage);
    }

}
