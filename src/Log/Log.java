/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Log;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 *
 * @author Administrator
 */
public class Log {

    private static StringBuffer sb;
    private static Log instance;

    private Log() {
    }

    public static synchronized Log getInstance() {
        if (instance == null) {
            instance = new Log();
            sb = new StringBuffer("");
        }
        return instance;
    }
    
    public void ClearLog()
    {
        sb.setLength(0);
    }
    
    public String getLog(){
        return sb.toString();
    }

    public StringBuffer Log(int nLogLevel, String strContent) {
        switch (nLogLevel) {
            case 1:
                sb.append("[LOG]").append(strContent).append("\n");
                break;
            case 2:
                sb.append("[LOG]").append(strContent).append("\n");
                break;
            case 3:
                sb.append("[LOG]").append(strContent).append("\n");
                break;
            default:
                throw new AssertionError();
        }
        return sb;
    }

    public void MailLog() throws MessagingException {

        Properties props = new Properties();
        props.setProperty("mail.debug", "true");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.host", "smtp.126.com");
        props.setProperty("mail.transport.protocol", "smtp");
        Session session = Session.getInstance(props);

        Message msg = new MimeMessage(session);
        msg.setSubject("BlackJack AI LogReport");
        msg.setText(sb.toString());
        // 设置发件人
        msg.setFrom(new InternetAddress("demonxdemon@126.com"));

        Transport transport = session.getTransport();
        // 连接邮件服务器
        transport.connect("demonxdemon", "diablo1985");
        // 发送邮件
        transport.sendMessage(msg, new Address[]{new InternetAddress("zhangyue.gucas@gmail.com")});
        // 关闭连接
        transport.close();

        return;
    }

}
