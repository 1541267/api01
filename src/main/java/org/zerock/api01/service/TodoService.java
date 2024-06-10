package org.zerock.api01.service;


import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.zerock.api01.domain.Todo;
import org.zerock.api01.dto.PageRequestDTO;
import org.zerock.api01.dto.PageResponseDTO;
import org.zerock.api01.dto.TodoDTO;

import java.util.Optional;

@Transactional
public interface TodoService {

  Long register(TodoDTO todoDTO);

  TodoDTO read (Long tno);

  PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO);

  void remove(Long tno);

  void modify(TodoDTO todoDTO);
}
