package com.itis.javalab.dao.interfaces;

import com.itis.javalab.models.Message;

import java.util.List;

public interface MessageDao extends CrudDao<Message> {
    List<Message> findAllById(Long id, int limit, boolean foreign_key);
    List<Message> findMessagesOnPage(Long limit, Long offset);
}
