package com.lv.demo.req.register;

import com.lv.demo.annotation.BirthdayValid;
import com.lv.demo.annotation.EmailValid;
import com.lv.demo.annotation.UserNameValid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * @author lv
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRgReq {

    @UserNameValid
    private String userName;

    @EmailValid
    private String email;

    @Length(min = 1, max = 20, message = "用户昵称长度在1-20字符之间")
    private String nickName;

    @Max(value = 2, message = "性别不合法")
    @Min(value = 0, message = "性别不合法")
    private Integer gender;

    @BirthdayValid
    private String birthday;

    private String password;

}
