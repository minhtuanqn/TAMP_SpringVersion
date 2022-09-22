package com.tamp_backend.convertor;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static com.tamp_backend.utils.ValidatorUtils.checkExistFieldOfClass;

/**
 * Class for support pagination
 * @param <M> Type of model
 * @param <E> Type of entity
 */
public class PaginationConvertor<M, E> {

    /**
     * Create pageable for sort
     * @param paginationRequestModel
     * @param defaultSortBy
     * @return
     */
    public Pageable convertToPageable(PaginationRequestModel paginationRequestModel, String defaultSortBy, Class<E> classType) {
        // Define sort by field for paging
        String sortBy = defaultSortBy;
        if(paginationRequestModel.getSortBy() != null) {
            if(!checkExistFieldOfClass(classType, paginationRequestModel.getSortBy())) {
                sortBy = defaultSortBy;
                paginationRequestModel.setSortBy(sortBy);
            } else {
                sortBy = paginationRequestModel.getSortBy();
            }
        }

        //Build Pageable
        Pageable pageable;
        if (paginationRequestModel.getSortType() != null && paginationRequestModel.getSortType().equals("dsc")) {
            pageable = PageRequest.of(paginationRequestModel.getCurPage(), paginationRequestModel.getPageSize(), Sort.by(sortBy).descending());
            paginationRequestModel.setSortType("dsc");
        } else {
            pageable = PageRequest.of(paginationRequestModel.getCurPage(), paginationRequestModel.getPageSize(), Sort.by(sortBy).ascending());
            paginationRequestModel.setSortType("asc");
        }
        return pageable;
    }

    /**
     * Build pagination
     * @param pagination
     * @param page
     * @param resource
     * @return
     */
    public ResourceModel<M> buildPagination(PaginationRequestModel pagination, Page<E> page, ResourceModel<M> resource) {
        resource.setTotalPage(page.getTotalPages());
        resource.setTotalResult((int) page.getTotalElements());
        resource.setPageIndex(pagination.getCurPage() + 1);
        resource.setLimit(pagination.getPageSize());
        return resource;
    }
}
