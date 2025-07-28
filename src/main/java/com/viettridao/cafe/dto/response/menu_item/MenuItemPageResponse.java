package com.viettridao.cafe.dto.response.menu_item;

import java.util.List;

import com.viettridao.cafe.dto.response.PageResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItemPageResponse extends PageResponse {
    private List<MenuItemResponse> menuItems;
}
