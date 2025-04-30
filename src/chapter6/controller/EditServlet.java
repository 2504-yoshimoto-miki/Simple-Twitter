package chapter6.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.Message;
import chapter6.beans.User;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/edit" })
public class EditServlet extends HttpServlet {

	Logger log = Logger.getLogger("twitter");

	public EditServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	//編集画面を表示するためのメソッド
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		//つぶやきのIDを取得
		String id = request.getParameter("id");

		//IDが数字かどうかチェック
		if (!id.matches("^[0-9]+$")) {
			List<String> errorMessages = new ArrayList<String>();

			errorMessages.add("不正なパラメータが入力されました");

			HttpSession session = request.getSession();
			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}

		//つぶやきのIDを型変換
		int messageId = Integer.parseInt(id);

		//serviceへ渡す
		Message message = new MessageService().select(messageId);
		//URLに存在しないつぶやきのIDが入力されたらエラーメッセージ
		HttpSession session = request.getSession();
		User loginUser = (User) session.getAttribute("loginUser");
		if (message.getUserId() != loginUser.getId()) {
			List<String> errorMessages = new ArrayList<String>();

			errorMessages.add("不正なパラメータが入力されました");

			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}

		request.setAttribute("message", message);
		request.getRequestDispatcher("edit.jsp").forward(request, response);
	}

	//編集後のつぶやきを取得→serviceへ渡す
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		HttpSession session = request.getSession();
		List<String> errorMessages = new ArrayList<String>();

		//つぶやきとつぶやきのIDを取得→変数へ代入
		String text = request.getParameter("text");
		int id = (Integer.parseInt(request.getParameter("id")));

		//messageへ取得した値を格納
		Message message = new Message();
		message.setText(text);
		message.setUserId(id);

		//バリデーション
		if (!isValid(text, errorMessages)) {
			session.setAttribute("errorMessages", errorMessages);
			request.setAttribute("message", message);
			request.getRequestDispatcher("edit.jsp").forward(request, response);
			return;
		}

		//serviceへ渡す
		new MessageService().update(message);
		response.sendRedirect("./");
	}

	private boolean isValid(String text, List<String> errorMessages) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		if (StringUtils.isBlank(text)) {
			errorMessages.add("入力してください");
		} else if (140 < text.length()) {
			errorMessages.add("140文字以下で入力してください");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}
}