package com.magnet.magnet.domain.post.content.app;

import com.magnet.magnet.domain.post.content.dto.request.RequestCreatePost;
import com.magnet.magnet.domain.post.content.dto.request.RequestUpdatePost;
import com.magnet.magnet.domain.post.content.dto.response.ResponsePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

    ResponsePost createPost(RequestCreatePost dto, String email);

    ResponsePost updatePost(RequestUpdatePost dto, String email);

    ResponsePost deletePost(Long clubId, Long postId, String email);

    ResponsePost getPost(Long postId);

    Page<ResponsePost> getPostList(Long clubId, Pageable pageable);

    Page<ResponsePost> getPostListByCategoryTitle(Long clubId, String categoryTitle, Pageable pageable);

}