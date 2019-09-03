package com.mastercard.labs.bps.discovery.util;

import io.swagger.model.SearchRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


public abstract class PaginationUtils {

    public static Pageable createPageable(SearchRequest request, Pageable pageable) {

        if (request != null && request.getSearchPage() != null) {

            int page = (request.getSearchPage().getPage() != null && request.getSearchPage().getPage() >= 0) ? request.getSearchPage().getPage() : pageable.getPageNumber();
            int size = (request.getSearchPage().getSize() != null && request.getSearchPage().getSize() >= 0) ? request.getSearchPage().getSize() : pageable.getPageSize();

            Sort sort = pageable.getSort();
            if (request.getSearchPage().getSort() != null) {
                sort = Sort.by(request.getSearchPage().getSort().stream().map(s ->
                        new Sort.Order(Sort.Direction.valueOf(s.getDirection().name()), s.getName())
                ).toArray(Sort.Order[]::new));
            }
            pageable = PageRequest.of(page, size, sort);
        }
        return pageable;
    }
}
