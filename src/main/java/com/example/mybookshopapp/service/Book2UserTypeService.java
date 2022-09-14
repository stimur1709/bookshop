package com.example.mybookshopapp.service;

import com.example.mybookshopapp.dto.BooksStatusRequestDto;
import com.example.mybookshopapp.dto.ResponseResultDto;
import com.example.mybookshopapp.model.book.Book;
import com.example.mybookshopapp.model.book.links.Book2User;
import com.example.mybookshopapp.model.book.links.BookCodeType;
import com.example.mybookshopapp.model.user.User;
import com.example.mybookshopapp.repository.Book2UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Book2UserTypeService {

    private final BookService bookService;
    private final BooksRatingAndPopularityService booksRatingAndPopularityService;
    private final UserProfileService userProfileService;
    private final UserContactService userContactService;
    private final Book2UserService book2UserService;
    private final Book2UserTypeRepository book2UserTypeRepository;

    @Autowired
    public Book2UserTypeService(BookService bookService,
                                BooksRatingAndPopularityService booksRatingAndPopularityService,
                                UserProfileService userProfileService, UserContactService userContactService,
                                Book2UserService book2UserService, Book2UserTypeRepository book2UserTypeRepository) {
        this.bookService = bookService;
        this.booksRatingAndPopularityService = booksRatingAndPopularityService;
        this.userProfileService = userProfileService;
        this.userContactService = userContactService;
        this.book2UserService = book2UserService;
        this.book2UserTypeRepository = book2UserTypeRepository;
    }

    public ResponseResultDto changeBookStatus(BooksStatusRequestDto dto) {
        Book book = bookService.getBookBySlug(dto.getBooksIds());
        User user = userContactService.getUserContact(userProfileService.getCurrentUser().getMail()).getUser();

        switch (dto.getStatus()) {
            case CART: {
                changeTypeBook2User(book, user, BookCodeType.CART);
                booksRatingAndPopularityService.changePopularity(book, 0.7);
                break;
            }
            case KEPT: {
                changeTypeBook2User(book, user, BookCodeType.KEPT);
                booksRatingAndPopularityService.changePopularity(book, 0.4);
                break;
            }
            case UNLINK: {
                booksRatingAndPopularityService.changePopularity(book, getValue(user, book));
                changeTypeBook2User(book, user, BookCodeType.UNLINK);
                break;
            }
            default:
                return new ResponseResultDto(false);
        }


        return new ResponseResultDto(true);
    }

    private void changeTypeBook2User(Book book, User user, BookCodeType status) {
        Optional<Book2User> optionalBook2User = book2UserService.getBook2User(book, user);
        Book2User book2User;
        if (!optionalBook2User.isPresent()) {
            book2User = new Book2User(book2UserTypeRepository.findByCode(status), book, user);
            book2UserService.save(book2User);

        } else {
            book2User = optionalBook2User.get();
            booksRatingAndPopularityService.changePopularity(book, getValue(book2User.getType().getCode()));
            book2User.setType(book2UserTypeRepository.findByCode(status));
            book2UserService.save(book2User);
        }
    }

    private double getValue(BookCodeType status) {
        return status.equals(BookCodeType.CART) ? -0.7 : -0.4;
    }

    private double getValue(User user, Book book) {
        Optional<Book2User> book2User = book2UserService.getBook2User(book, user);
        return book2User.map(link -> getValue(link.getType().getCode())).orElse(0.0);
    }
}