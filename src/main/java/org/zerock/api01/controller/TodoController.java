package org.zerock.api01.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.zerock.api01.dto.PageRequestDTO;
import org.zerock.api01.dto.PageResponseDTO;
import org.zerock.api01.dto.TodoDTO;
import org.zerock.api01.service.TodoService;

import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
public class TodoController {

  private final TodoService todoService;


  @Operation(summary = "Register")
  @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, Long> register(@RequestBody TodoDTO todoDTO) {
    log.info(todoDTO);

    Long tno = todoService.register(todoDTO);

    return Map.of("tno", tno);
  }

  @Operation(summary = "ReadById")
  @GetMapping("/{tno}")
  public TodoDTO read(@PathVariable("tno") Long tno) {
    log.info("read tno: " + tno);

    return todoService.read(tno);
  }

  @Operation(summary = "List")
  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  public PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO) {

    return todoService.list(pageRequestDTO);
  }

  @Operation(summary = "Delete")
  @DeleteMapping(value = "/{tno}")
  public Map<String, String> delete(@PathVariable Long tno) {

    todoService.remove(tno);
    return Map.of("result", "success");
  }

  @Operation(summary = "Modify")
  @PutMapping(value = "/{tno}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, String> modify(@PathVariable("tno") Long tno, @RequestBody TodoDTO todoDTO) {

    //잘못된 tno가 발생하지 못하도록
    todoDTO.setTno(tno);

    todoService.modify(todoDTO);

    return Map.of("result", "success");
  }
}
