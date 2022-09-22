package com.tamp_backend.resolver;

import com.tamp_backend.constant.PaginationConst;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.resolver.annotation.RequestPagingParam;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

public class RequestPaginationResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(RequestPagingParam.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String page = request.getParameter(PaginationConst.PAGE_INDEX);
        String perPage = request.getParameter(PaginationConst.LIMIT);
        String sortType = request.getParameter(PaginationConst.SORT_TYPE);
        String sortBy = request.getParameter(PaginationConst.SORT_BY);
        int pageIndex;
        int limit;
        if(page != null) {
            pageIndex = Integer.parseInt(page);
            pageIndex = pageIndex > 0 ? --pageIndex : PaginationConst.DEFAULT_PAGE_INDEX;
        } else {
            pageIndex = PaginationConst.DEFAULT_PAGE_INDEX;
        }
        if(perPage != null) {
            limit = Integer.parseInt(perPage) > 0 ? Integer.parseInt(perPage) : PaginationConst.DEFAULT_LIMIT;
        } else {
            limit = PaginationConst.DEFAULT_LIMIT;
        }

        return new PaginationRequestModel(pageIndex, limit, sortBy, sortType);
    }
}
