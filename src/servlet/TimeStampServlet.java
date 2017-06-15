package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.TimeUtil;

/**
 * 废弃，struts已经过滤器已将请求过滤过去了，servlet失效
 * 
 * @author zhagz
 * 
 */
@WebServlet("/TimeStampServlet")
public class TimeStampServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public TimeStampServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		request.setCharacterEncoding("utf-8");

		PrintWriter pw = response.getWriter();

		String resp = TimeUtil.currentTime();
		try {
			pw.write(resp);
			pw.flush();
			pw.close();
		} catch (Exception e) {
			resp = "-2";
			pw.flush();
			pw.close();
			e.printStackTrace();
		}
	}

}
