package com.itis.javalab.services.implementations;

import com.itis.javalab.context.annotations.Autowired;
import com.itis.javalab.dao.interfaces.MessageDao;
import com.itis.javalab.dao.interfaces.UserDao;
import com.itis.javalab.dto.entity.MessageDTO;
import com.itis.javalab.dto.system.PaginationDto;
import com.itis.javalab.dto.system.ServiceDto;
import com.itis.javalab.models.Message;
import com.itis.javalab.services.interfaces.MessageDtoTranslator;

import java.sql.Timestamp;
import java.util.*;

public class MessageDtoTranslatorImpl implements MessageDtoTranslator {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private UserDao userDao;

    @Override
    public ServiceDto getMessagesViaPagination(PaginationDto dto) {
        List<Message> messages = getListMessages(dto.getNumber(), dto.getSize(), messageDao); //getNumber == pageNumber
        Set<Long> ids = new HashSet<>();
        messages.stream().forEach(message -> ids.add(message.getOwnerId()));
        Map<Long, String> names = getUsersNames(ids);
        List<MessageDTO> listMessageDTO = new ArrayList<>();
        getListOfDTO(names, messages, listMessageDTO);
        return prepareDto(listMessageDTO);
    }

    private ServiceDto prepareDto(List<MessageDTO> listMessageDTO) {
        Map<String, Object> params = new HashMap<>();
        params.put("typ", "200M");
        params.put("data", listMessageDTO);
        return ServiceDto.builder().service(3).chatId(0).resultParams(params).build();
    }

    private void getListOfDTO(Map<Long, String> names, List<Message> messages, List<MessageDTO> listMessageDTO) {
        messages.stream().forEach(
                message -> {
                    listMessageDTO.add(new MessageDTO(message.getId(), message.getText(), names.get(message.getOwnerId()),
                            Timestamp.valueOf(message.getDateTime()).getTime()));
                }
        );
    }

    private Map<Long, String> getUsersNames(Set<Long> ids) {
        return userDao.findNamesByIds(ids);
    }

    private List<Message> getListMessages(Long page, Long size, MessageDao messageDao) {
        return messageDao.findMessagesOnPage(size, (page - 1) * size);
    }
}
