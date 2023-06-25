package com.example.penguin.controller;

import com.example.penguin.Entities.UserAccountEntity;
import com.example.penguin.Service.UserAccService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.util.Base64;

@Controller
public class loginController {

    @Autowired
    UserAccService userAccService;

    @Autowired
    HttpSession session;


    @GetMapping("/login")
    public String login(Model model)
    {
        model.addAttribute("newAcc", new UserAccountEntity());
        return "login";
    }

    @PostMapping("/login/sign_up")
    private String sign_up(RedirectAttributes rd ,
                           @ModelAttribute UserAccountEntity userAccount ,
                           @ModelAttribute(name="username") String username,
                           @ModelAttribute(name = "birthday") Date birthday,
                           @ModelAttribute(name="phone") String phone,
                           @ModelAttribute(name="pass") String pass,
                           @ModelAttribute(name ="email" ) String email ,
                           @ModelAttribute(name="confirmpass") String confirm
                           )
    {

        int flag = 0;
        UserAccountEntity userAccount1 = userAccService.findNameUser(phone);
        if(userAccount1 == null)
        {
                String password = Base64.getEncoder().encodeToString(pass.trim().getBytes());
                userAccount.setPassword(password.trim());
                userAccount.setPhone(phone);
                userAccount.setEmail(email);
                userAccount.setName(username);
                userAccount.setBirthDate(birthday);
                userAccount.setRole(0);
                userAccount.setAvatar("https://res.cloudinary.com/dqy4p8xug/image/upload/v1684272398/userImage_oujnyf.png");
                userAccService.saveUser(userAccount);
                flag=1;

        }else {
            rd.addFlashAttribute("msg", "Số điện thoại này đã được sử dụng , vui lòng sử dụng số điện thoại khác");
            flag=0;
        }

        if(flag== 1)
        {
            return "redirect:/login" ;
        }else
        {
            return "redirect:/sign_up" ;
        }

    }
    @PostMapping("/login/sign_in")
    private String sign_in(Model model ,
                           @ModelAttribute(name = "phone") String phone,
                           @ModelAttribute(name = "pass") String password
                           )
    {

        // tìm kiếm người dùng dựa trên số điện thoại
        UserAccountEntity userAccount = userAccService.findByPhone(phone);


        if(userAccount != null)
        {
            // giải mã mk đã được mã hóa
            String decodePass = new String(Base64.getDecoder().decode(userAccount.getPassword()));
            // . trim xóa khoản trắng ở đầu và cuối chuỗi
            if(decodePass.equals(password.trim()))
            {

                session.setAttribute("account", userAccount);
                if(userAccount.getRole()==1){
                    return "Admin_home";
                }else {
                    return "redirect:/login";
                }
            }else {
                model.addAttribute("error","Mật khẩu hoặc tài khoảng không đúng nhá");
                session.setAttribute("error","Mật khẩu hoặc tài khoảng không đúng nhá");
                return "redirect:/login";
            }
        }else {
            model.addAttribute("error","Mật khẩu hoặc tài khoảng không đúng nhá");
            session.setAttribute("error","Mật khẩu hoặc tài khoảng không đúng nhá");
            return "redirect:/login";
        }






    }


}
