package chapter6.dao;

import static chapter6.utils.CloseableUtil.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import chapter6.beans.Message;
import chapter6.beans.UserMessage;
import chapter6.exception.SQLRuntimeException;
import chapter6.logging.InitApplication;

public class MessageDao {


    /**
    * ロガーインスタンスの生成
    */
    Logger log = Logger.getLogger("twitter");

    /**
    * デフォルトコンストラクタ
    * アプリケーションの初期化を実施する。
    */
    public MessageDao() {
        InitApplication application = InitApplication.getInstance();
        application.init();

    }

    public void insert(Connection connection, Message message) {

	  log.info(new Object(){}.getClass().getEnclosingClass().getName() +
        " : " + new Object(){}.getClass().getEnclosingMethod().getName());

        PreparedStatement ps = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO messages ( ");
            sql.append("    user_id, ");
            sql.append("    text, ");
            sql.append("    created_date, ");
            sql.append("    updated_date ");
            sql.append(") VALUES ( ");
            sql.append("    ?, ");                  // user_id
            sql.append("    ?, ");                  // text
            sql.append("    CURRENT_TIMESTAMP, ");  // created_date
            sql.append("    CURRENT_TIMESTAMP ");   // updated_date
            sql.append(")");

            ps = connection.prepareStatement(sql.toString());

            ps.setInt(1, message.getUserId());
            ps.setString(2, message.getText());

            ps.executeUpdate();
        } catch (SQLException e) {
		log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
            throw new SQLRuntimeException(e);
        } finally {
            close(ps);
        }
    }

	public void delete(Connection connection, Message message) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("DELETE FROM messages WHERE id = ?");

			ps = connection.prepareStatement(sql.toString());

			ps.setInt(1, message.getUserId());

			ps.executeUpdate();
		} catch (SQLException e) {
		log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
            throw new SQLRuntimeException(e);
        } finally {
            close(ps);
        }
	}

	//つぶやき編集のためにidとuser_idが紐づくつぶやき参照
	public UserMessage select(Connection connection, Message message) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM messages WHERE id = ?");

			ps = connection.prepareStatement(sql.toString());

			//バインド変数へserviceから受け取ったmessageの値を入れる
			ps.setInt(1, message.getUserId());

			//SQL実行
			ResultSet rs = ps.executeQuery();
			return ;
		} catch (SQLException e) {
		log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
            throw new SQLRuntimeException(e);
        } finally {
            close(ps);
        }

	}

	//つぶやきを編集した内容に更新
	public void Update(Connection connection, Message message) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			//idとuser_idが紐づくレコードのtextを更新
			sql.append("UPDATE messages SET ");
			sql.append("  text = ?");
			sql.append("WHERE ");
			sql.append("  id = ?");

			ps = connection.prepareStatement(sql.toString());

			ps.setString(1, message.getText());
			ps.setInt(1, message.getUserId());

			//SQL実行
			ps.executeUpdate();
		} catch (SQLException e) {
		log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
            throw new SQLRuntimeException(e);
        } finally {
            close(ps);
        }
	}
}