package com.shop.controller.portal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.common.Const;
import com.shop.common.LoginCheck;
import com.shop.common.Message;
import com.shop.pojo.User;
import com.shop.service.IUserService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private IUserService iUserService;
	@Autowired 
	private LoginCheck loginCheck;
	
//	@RequestMapping("/cookieTest")
//	@ResponseBody
//	public void cookie(HttpSession session, HttpServletResponse response){
//		Cookie cookie = new Cookie("time", "20170820");
//		cookie.setMaxAge(5 * 60 * 60);
//		cookie.setPath("/");
//		response.addCookie(cookie);
//	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	public Message register(User user){
		return iUserService.register(user);
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public Message login(String username, String password, HttpSession session){
		Message message = iUserService.login(username, password);
		if(message.isSuccess() == false)
			return message;
		session.setAttribute(Const.CURRENT_USER, message.getData());
		// 设置过期时间: 2天
		session.setMaxInactiveInterval(60 * 60 * 24 * 2);
		return Message.successMsg("登录成功");
	}
	
	@RequestMapping(value = "/logout")
	@ResponseBody
	public Message logout(HttpSession session){
		session.removeAttribute(Const.CURRENT_USER);
		return Message.successMsg("登出成功");
	}
	
	// 找回密码(1)，验证用户名是否存在，若存在则返回问题
	@RequestMapping(value = "/getUsername", method = RequestMethod.POST)
	@ResponseBody
	public Message getUsername(String username){
		return iUserService.getUsername(username);
	}
	
	// 找回密码(2)，验证答案是否都正确
	// 假设前端可以保存用户名
	@RequestMapping(value = "/getAnswer", method = RequestMethod.POST)
	@ResponseBody
	public Message getAnswer(String username, String answer){
		return iUserService.getAnswer(username, answer);
	}
	
	// 找回密码(3)，设置新密码
	@RequestMapping(value = "/setNewPwd", method = RequestMethod.POST)
	@ResponseBody
	public Message setNewPwd(String username, String password, String uuid){
		return iUserService.setNewPwd(username, password, uuid);
	}
	
	@RequestMapping(value = "/editInfo", method = RequestMethod.POST)
	@ResponseBody
	public Message editInfo(User user, HttpSession session){
		if(!loginCheck.check(session, Const.NORMAL_USER))
			return Message.errorMsg("未登录或无权限");
		User sessionUser = (User) session.getAttribute(Const.CURRENT_USER);
		user.setId(sessionUser.getId());
		// 防止把用户名和密码修改了
		user.setUsername(StringUtils.EMPTY);
		user.setPassword(StringUtils.EMPTY);
		return iUserService.editInfo(user);
	}
	
	@RequestMapping(value = "/getInfo", method = RequestMethod.POST)
	@ResponseBody
	public Message getInfo(HttpSession session){
		if(!loginCheck.check(session, Const.NORMAL_USER))
			return Message.errorMsg("未登录或无权限");
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		return Message.successData(user);
	}
	
	@RequestMapping(value = "/getAllInfo", method = RequestMethod.POST)
	@ResponseBody
	public Message getAllInfo(HttpSession session){
		if(!loginCheck.check(session, Const.NORMAL_USER))
			return Message.errorMsg("未登录或无权限");
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		return iUserService.getAllInfo(user);
	}
}
