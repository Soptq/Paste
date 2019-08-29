package soptqs.paste.utils.bilibili;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by S0ptq on 2018/3/11.
 */

public class BilibiliPicture {

    public static String getBilibiliPic(String av) {
        String picHtml = "404";
        String htmlUrl = "https://www.bilibili.com/video/" + av;
        String source = BilibiliTitle.getHtmlSource(htmlUrl);
        Pattern pattern = Pattern.compile("http://([0-9a-zA-Z/.]{25,})");
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            picHtml = matcher.group(0);
        }
        return picHtml;
    }
}
