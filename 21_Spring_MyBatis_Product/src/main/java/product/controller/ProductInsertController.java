package product.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import product.model.ProductBean;
import product.model.ProductDao;

@Controller
public class ProductInsertController {
	private final String command = "/insert.prd";
	private String getPage = "productInsertForm";
	private String gotoPage = "redirect:/list.prd";
	
	@Autowired
	ProductDao pdao;
	
	@Autowired
	ServletContext servletContext;
	//상품 목록 보기에서 추가 클릭
	@RequestMapping(value=command, method = RequestMethod.GET)
	public String doAction(HttpSession session) {
		//로그인 시 로그인 정보(loginInfo) 세션 설정
		System.out.println(session.getAttribute("loginInfo"));
		
		if(session.getAttribute("loginInfo")==null) {//로그인 안 함
			return "redirect:/loginForm.mb"; //MemberLoginForm.jsp로 이동
		}else { //로그인 함
			return getPage;
		}
	}
	
	@RequestMapping(value = command, method = RequestMethod.POST)
	public String doAction(
			@ModelAttribute("productBean") @Valid ProductBean productBean,
			BindingResult result) {
		
		String uploadPath = servletContext.getRealPath("/resources");
		System.out.println("uploadPath:"+uploadPath);
		//uploadPath:C:\\Users\\user\Downloads\spring-tool-suite-3.9.17.RELEASE\sts-bundle\pivotal-tc-server\instances\Spring3\wtpwebapps\20_Spring_MyBatis_Products\resources
		// C:\\Users~~~\resources\lemon.jpg
		
		System.out.println("*:"+uploadPath+File.separator+productBean.getUpload().getOriginalFilename());
		
		
		File destination = new File(uploadPath+File.separator+productBean.getUpload().getOriginalFilename());
		
		MultipartFile multi = productBean.getUpload();
		
		if(result.hasErrors()) {
			return getPage;
			
		} else {
			int cnt = pdao.insertProduct(productBean);
			System.out.println("ProductInsertController cnt : " + cnt);
			if(cnt > -1) {
				
				try {
					multi.transferTo(destination);
					
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return gotoPage;
				
			} else {
				return getPage;
			}
		}
	}
	
}