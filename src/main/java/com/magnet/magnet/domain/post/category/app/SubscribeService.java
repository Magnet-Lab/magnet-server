package com.magnet.magnet.domain.post.category.app;

import com.magnet.magnet.domain.post.category.dto.response.ResponseSubscribe;

import java.util.List;

public interface SubscribeService {

    void subscribe(Long categoryId, String email);

    void unSubscribe(Long categoryId, String email);

    List<ResponseSubscribe> getSubscribeList(String email);

}