package filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.SessionAware;

import domain.User;

//@WebFilter(filterName = "fileFilter", urlPatterns = { "*.jpg", "*.png", "/UserVerify/*" })
public class FileFilter implements Filter, SessionAware {

	private Map<String, Object> session;

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		System.out.println("访问来源IP：" + request.getRemoteAddr());
		System.out.println("访问来源PORT：" + request.getRemotePort());
		System.out.println("访问链接：" + ((HttpServletRequest) request).getServletPath());

		if (session != null) {
			User user = (User) session.get("user");
			if (user == null) {
				System.out.println("user is not login!");
				return;
			} else {
				chain.doFilter(request, response);
				System.out.println("user has logined,name is " + user.getUsername());
			}
		} else {
			((HttpServletResponse) response).sendRedirect("/null.html");
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

}
