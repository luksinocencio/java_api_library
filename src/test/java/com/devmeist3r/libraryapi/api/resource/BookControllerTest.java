package com.devmeist3r.libraryapi.api.resource;

import com.devmeist3r.libraryapi.api.dto.BookDTO;
import com.devmeist3r.libraryapi.api.model.entity.Book;
import com.devmeist3r.libraryapi.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

  static String BOOK_API = "/api/books";

  @Autowired
  MockMvc mvc;

  @MockBean
  BookService service;

  @Test
  @DisplayName("Deve criar um livro com sucesso.")
  public void createBookTest() throws Exception {

    BookDTO dto = BookDTO
      .builder()
      .author("Artur")
      .title("As aventuras")
      .isbn("001")
      .build();

    Book savedBook = Book
      .builder()
      .id(101L)
      .author("Artur")
      .title("As aventuras")
      .isbn("001")
      .build();

    BDDMockito.given(service.save(Mockito.any(Book.class)))
      .willReturn(savedBook);

    String json = new ObjectMapper().writeValueAsString(dto);

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
      .post(BOOK_API)
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)
      .content(json);

    mvc
      .perform(request)
      .andExpect(status().isCreated())
      .andExpect(jsonPath("id").value(101))
      .andExpect(jsonPath("title").value(dto.getTitle()))
      .andExpect(jsonPath("author").value(dto.getAuthor()))
      .andExpect(jsonPath("isbn").value(dto.getIsbn()));
  }

  @Test
  @DisplayName("Deve lançar erro de validação quando não hover dados suficientes para criação do livro.")
  public void createInvalidBookTest() throws Exception {
    String json = new ObjectMapper().writeValueAsString(new BookDTO());

    MockHttpServletRequestBuilder request = MockMvcRequestBuilders
      .post(BOOK_API)
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)
      .content(json);

    mvc.perform(request)
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("errors", Matchers.hasSize(3)));
  }

}

