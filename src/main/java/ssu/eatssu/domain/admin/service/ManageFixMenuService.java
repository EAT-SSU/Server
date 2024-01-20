package ssu.eatssu.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.admin.dto.MenuBoards;

@Service
@RequiredArgsConstructor
public class ManageFixMenuService {

    public MenuBoards getMenuBoards() {

        //TODO menuBoards 를 만들어서 리턴
        return new MenuBoards();
    }
}
