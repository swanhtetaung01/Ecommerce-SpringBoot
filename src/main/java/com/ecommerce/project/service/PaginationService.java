package com.ecommerce.project.service;

import org.springframework.data.domain.Pageable;

public interface PaginationService {
    Pageable getPageDetails(Integer pageNumber, Integer pageSize,
                            String sortBy, String sortOrder);
}
