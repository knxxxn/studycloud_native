package com.example.catalog_service.domain;

import org.springframework.stereotype.Service;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    public Iterable<Book> viewBookList(){

        return bookRepository.findAll();
    }

    public Book viewBookDetails(String isbn){//존재하지 않은 책을 보려고 할 때 예외발생
        return bookRepository.findByIsbn(isbn).orElseThrow(()-> new BookNotFoundException(isbn));

    }
    public Book addBookToCatalog(Book book){//동일한 책을 여러 번 추가하려고 할 때 예외 발생
        if(bookRepository.existByIsbn(book.isbn())){
            throw new BookAlreadyExistsException(book.isbn());
        }
        return bookRepository.save(book);
    }
    public void removeBookFromCatalog(String isbn){
        bookRepository.deleteByIsbn(isbn);
    }
    public Book editBookDetails(String isbn, Book book){ //책 수정시 isbn을 제외한 모든 필드 수정
        return bookRepository.findByIsbn(isbn).map(existingBook -> {
            var bookToUpdate = new Book(
                    existingBook.isbn(),
                    book.title(),
                    book.author(),
                    book.price());
            return  bookRepository.save(bookToUpdate);
        }).orElseGet(() -> addBookToCatalog(book)); //카탈로그에 없는 책이라면 새로운 책으로 등록
    }
}
