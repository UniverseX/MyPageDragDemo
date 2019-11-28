package com.autoai.pagedragframe.test.layoutmanager;

import java.util.ArrayList;
import java.util.List;

public class UserModel {
    private static final UserModel sInstance = new UserModel();

    private UserModel() {
    }

    public static UserModel get() {
        return sInstance;
    }

    private static String[] imgUrls = {
            "http://pic126.nipic.com/file/20170405/282640_103209563035_2.jpg",
            "https://2e.zol-img.com.cn/product/64/410/ceneo4LyDg8c.jpg",
            "http://pic126.nipic.com/file/20170405/282640_103209563035_2.jpg",
            "http://pic126.nipic.com/file/20170405/282640_103209563035_2.jpg",
            "http://pic126.nipic.com/file/20170405/282640_103209563035_2.jpg",
            "https://2e.zol-img.com.cn/product/64/410/ceneo4LyDg8c.jpg",
            "http://pic126.nipic.com/file/20170405/282640_103209563035_2.jpg",
            "http://pic126.nipic.com/file/20170405/282640_103209563035_2.jpg",
            "http://pic126.nipic.com/file/20170405/282640_103209563035_2.jpg",
            "https://2e.zol-img.com.cn/product/64/410/ceneo4LyDg8c.jpg",
            "http://pic126.nipic.com/file/20170405/282640_103209563035_2.jpg",
            "http://pic126.nipic.com/file/20170405/282640_103209563035_2.jpg"
    };
    private static String[] names = {
            "Jack",
            "Sunny",
            "Jorge",
            "Peter",
            "Jack",
            "Sunny",
            "Jorge",
            "Peter",
            "Jack",
            "Sunny",
            "Jorge",
            "Peter"
    };

    private List<UserData> users = new ArrayList<>();

    public void initUsers() {
        users.clear();
        for (int i = 0; i < imgUrls.length; i++) {
            users.add(new UserData(imgUrls[i], names[i]));
        }
    }

    public List<UserData> getUsers() {
        return users;
    }
}
