package filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.TimeUtil;
import util.ValidateUtil;
import config.BasicInfoConfig;

//@WebFilter(filterName = "validateFilter", urlPatterns = { "*.action", "*.action?*" })
public class ValidateFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		resp.addHeader("Access-Control-Allow-Origin", "*");

		try {
			System.out.println("CharacterEncoding:" + req.getCharacterEncoding());
			System.out.println("RequestURI:" + req.getRequestURI());
			System.out.println("QueryString:" + req.getQueryString());
			System.out.println("ServletPath:" + req.getServletPath());

			String timestamp = req.getParameter("timestamp");
			String signature = req.getParameter("signature");

			System.out.println("timestamp:" + timestamp);
			System.out.println("signature:" + signature);

			if (req.getServletPath().equals("/util_getTimestamp.action")) {
				PrintWriter pw = resp.getWriter();
				pw.write(TimeUtil.currentTime());
				pw.flush();
				pw.close();
				return;//TODO
			}

			// action过滤器
			if (timestamp != null && signature != null) {
				long ts = Long.parseLong(timestamp);
				long cur = System.currentTimeMillis();

				// 在5分钟之内
				if (Math.abs(cur - ts) <= 5 * 60 * 1000) {
					String md5 = ValidateUtil.MD5(timestamp + BasicInfoConfig.secretKey);
					md5 = ValidateUtil.MD5(md5);// 加两次md5
					System.out.println("md5 success:" + md5.equals(signature));
					if (md5.equals(signature)) {
						chain.doFilter(request, response);
					} else {
						((HttpServletResponse) response).sendRedirect("/index.jsp");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			((HttpServletResponse) response).sendRedirect("/index.jsp");
			System.out.println("Verify failed.");// 验证失败
		}

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
