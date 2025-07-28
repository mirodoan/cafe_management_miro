package com.viettridao.cafe.dto.response.menu_item;

import lombok.Getter;
import lombok.Setter;
import com.viettridao.cafe.dto.response.menu_item_detail.MenuItemDetailResponse;
import java.util.List;

@Getter
@Setter
public class MenuItemResponse {
    private Integer id;
    private String itemName;
    private Double currentPrice;
    private List<MenuItemDetailResponse> menuDetails;
}
