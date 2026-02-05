package com.lv.demo.req.login;

import com.lv.demo.annotation.UserNameValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author lv
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AccountLoginReq {

    @UserNameValid
    private String userName;

    private String password;

}
