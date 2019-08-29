package soptqs.paste.utils.bilibili;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by S0ptq on 2018/2/27.
 */

public class BilibiliTitle {
    public static String main(String args) {
        String htmlUrl = "https://www.bilibili.com/video/" + args;
        BilibiliTitle bilibiliTitle = new BilibiliTitle();
        return bilibiliTitle.getBilibiliTitle(htmlUrl);
    }

    public static String getHtmlSource(String htmlUrl) {
        URL url;
        StringBuilder sb = new StringBuilder();
        try {
            url = new URL(htmlUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));//读取网页全部内容
            String temp;
            while ((temp = in.readLine()) != null) {
                sb.append(temp);
            }
            in.close();
        } catch (MalformedURLException e) {
            System.out.println("Error URL");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private String getBilibiliTitle(String htmlUrl) {
        String htmlSource;
        htmlSource = getHtmlSource(htmlUrl);//获取htmlUrl网址网页的源码
        String title = getTitle(htmlSource);
        return title;
    }

    private String getTitle(String htmlSource) {
        List<String> list = new ArrayList<>();
        StringBuilder title = new StringBuilder();

        //Pattern pa = Pattern.compile("<title>.*?</title>", Pattern.CANON_EQ);也可以
        Pattern pa = Pattern.compile("<title.*?</title>");//源码中标题正则表达式
        Matcher ma = pa.matcher(htmlSource);
        while (ma.find())//寻找符合el的字串
        {
            list.add(ma.group());//将符合el的字串加入到list中
        }
        for (int i = 0; i < list.size(); i++) {
            title.append(list.get(i));
        }
        return outTag(title.toString());
    }

    private String outTag(String s) {
        return s.replaceAll("<.*?>", "");
    }

}
