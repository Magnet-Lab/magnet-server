package com.magnet.magnet.domain.post.category.app;

import com.magnet.magnet.domain.post.category.dto.request.RequestCreateCategory;
import com.magnet.magnet.domain.post.category.dto.request.RequestUpdateCategory;
import com.magnet.magnet.domain.post.category.dto.response.ResponseCategory;

import java.util.List;

public interface CategoryService {

    void createCategory(RequestCreateCategory dto, String email);

    void updateCategory(RequestUpdateCategory dto, String email);

    void deleteCategory(Long categoryId, String email);

    List<ResponseCategory> getCategoryList(Long clubId);

}