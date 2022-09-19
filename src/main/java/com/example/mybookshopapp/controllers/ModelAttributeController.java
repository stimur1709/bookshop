package com.example.mybookshopapp.controllers;

import com.example.mybookshopapp.dto.SearchWordDto;
import com.example.mybookshopapp.dto.UserDto;
import com.example.mybookshopapp.model.book.Book;
import com.example.mybookshopapp.model.book.links.BookCodeType;
import com.example.mybookshopapp.service.BookShopService;
import com.example.mybookshopapp.service.UserProfileService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ModelAttributeController {

    private final UserProfileService userProfileService;
    private final BookShopService bookShopService;

    @Autowired
    public ModelAttributeController(UserProfileService userProfileService, BookShopService bookShopService) {
        this.userProfileService = userProfileService;
        this.bookShopService = bookShopService;
    }

    @ModelAttribute("getUser")
    public UserDto getUserDTO() {
        return userProfileService.getCurrentUser();
    }

    @ModelAttribute("isAuthenticatedUser")
    public boolean isAuthenticatedUser() {
        return userProfileService.isAuthenticatedUser();
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto() {
        return new SearchWordDto();
    }

    @ModelAttribute("searchResult")
    public List<Book> searchResult() {
        return new ArrayList<>();
    }

    @ModelAttribute(name = "bookCart")
    public List<Book> bookCart() {
        return new ArrayList<>();
    }

    @ModelAttribute("cartSize")
    public int cartSize(@CookieValue(name = "cartContent", required = false) String cartContent) {
        return bookShopService.getBooksUser(cartContent, BookCodeType.CART).size();
    }

    @ModelAttribute("keptSize")
    public int keptSize(@CookieValue(name = "keptContent", required = false) String keptContent) {
        return bookShopService.getBooksUser(keptContent, BookCodeType.KEPT).size();
    }
}
