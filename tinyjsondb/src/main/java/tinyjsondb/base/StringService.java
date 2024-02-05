package tinyjsondb.base;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class StringService {

    private StringService() {
    }
    
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isBlank();
    }
    
    public static String prettyJSON(String json) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement je = JsonParser.parseString(json);
            return gson.toJson(je);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> String prettyJSON(T data) {
        return prettyJSON(new Gson().toJson(data));
    }
    
    public static String umlaute(String text) {
        if (text == null) {
            return "";
        }
        return text.toLowerCase()
            .replace("ä", "ae")
            .replace("ö", "oe")
            .replace("ü", "ue")
            .replace("ß", "ss");
    }

    public static List<String> upper(List<String> list) {
        return list.stream().map(i -> i.toUpperCase()).collect(Collectors.toList());
    }

    /**
     * @param text contains many Markdown links: (title)[url]
     * @return HTML link
     */
    public static String makeClickableLinks(String text) {
		Pattern regex = Pattern.compile("\\(([^\\)]+)\\)\\[([^\\]]+)\\]");
		Matcher matcher = regex.matcher(text);
		while (matcher.find()) {
			String url = matcher.group(2);
			if (url.contains("createpage.action")) {
				continue;
			}
			String target = "";
			if (url.startsWith("http://") || url.startsWith("https://")) {
				target = " target=\"_blank\"";
			} else {
				if (url.startsWith("N")) { // note link
					url = url.substring(1);
					url = "?highlight=" + url + "#" + url;
				} else { // page link
					url = "../" + url;
				}
			}
			text = text.replace(matcher.group(0), "<a href=\"" + url + "\"" + target + ">" + matcher.group(1) + "</a>");
		}
		return text;
	}
    
    /**
     * Limit text to maxlen, but to do not cut text within link "(...)[...]"
     */
    public static String cutOutsideLinks(String text, int maxlen) {
        // TODO man sollte auch http:.... Links unterstützen
        if (text == null || maxlen < 1 || text.length() < maxlen) {
            return text;
        }
        String ret = "";
        boolean inside = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '(') {
                int o = text.indexOf(")[", i);
                inside = o > i && text.indexOf("]", i) > o;
            }
            ret += c;
            if (i >= maxlen && !inside) {
                return ret;
            }
            if (inside && c == ']') {
                inside = false;
            }
        }
        return ret;
    }
    
    public static String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
