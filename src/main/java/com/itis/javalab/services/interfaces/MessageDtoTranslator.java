package com.itis.javalab.services.interfaces;

import com.itis.javalab.context.annotations.Component;
import com.itis.javalab.dto.system.PaginationDto;
import com.itis.javalab.dto.system.ServiceDto;

@Component
public interface MessageDtoTranslator {
    public ServiceDto getMessagesViaPagination(PaginationDto dto);
}
