package com.gjob.pub.controller;

import com.gjob.pub.model.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @RestController注解=@Controller+@RequestBody
 */
@RestController("/user")
public class UserController {

    @RequestMapping(value="/getUserList",method = RequestMethod.GET)
    public List<User> getUserIist(){
        List<User> userList = new ArrayList<User>();
        for(int i = 0; i < 100; i++){
            User user = new User();
            user.setAddress("中国杭州市"+i);
            user.setAge("18");
            user.setSex("男");
            user.setUserName("南航金克斯"+i);
            userList.add(user);
        }
        return userList;
    }
}
