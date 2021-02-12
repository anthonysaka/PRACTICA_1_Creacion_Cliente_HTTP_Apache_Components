package isc415.practica1;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        System.out.println("[*] By Anthony Sakamoto 00008614 -> HTTP Client - Web Scraping");
        System.out.println("[*] Please, insert a valid URL: ");

        Scanner scn = new Scanner(System.in);
        String url = scn.nextLine();

        Document doc = Jsoup.connect(url).userAgent("Chrome/88.0.4324.150").get();

        System.out.println("[a] Number of lines --> " + lineCounter(url));
        System.out.println("[b] Number of paragraphs  --> " + paragraphCounter(doc));
        System.out.println("[c] Number of images inside of paragraphs --> " + imageCounterInsideParagraph(doc));
        System.out.println("[d] Number of forms --> (TOTAL): "+ formCounterByMethod(doc)[0] + " (GET): " + formCounterByMethod(doc)[1] + " (POST): " + formCounterByMethod(doc)[2]);
        System.out.println("[e] Inputs field inside forms --> \n" + inputsInsideForms(doc));
        System.out.println("[f] Server Response (FORMS <POST>) --> \n" + formPostServerResponse(url,doc));
    }

    /*
     ***** METHODS *****
     */

    private static Integer lineCounter(String URL) throws IOException {
        Connection.Response httpRes = Jsoup.connect(URL).execute();
        return httpRes.body().split("\n").length; //Returning amount of lines of http response resource
    }

    private static Integer paragraphCounter(Document doc){
        Elements paragraphs = doc.getElementsByTag("p");
        return  paragraphs.size(); //Returning amount of paragraphs (Tag <p>) in the URL.
    }

    private static  Integer imageCounterInsideParagraph(Document doc){
        Elements paragraphs = doc.getElementsByTag("p");
        int x = 0;
        for (Element p: paragraphs) {
            x += p.getElementsByTag("img").size();
        }
        return x; //Returning amount of image (Tag <img>) inside of each paragraphs (Tag <p>) in the URL.
    }

    private static Integer[] formCounterByMethod(Document doc){
        Elements forms = doc.getElementsByTag("form");
        int xGet = 0;
        int xPost = 0;
        Integer[] z = new Integer[3];

        for (Element form: forms) {
            if (form.attr("method").equalsIgnoreCase("GET")){
                xGet++;
            } else if (form.attr("method").equalsIgnoreCase("POST")){
                xPost++;
            }

        }
        z[0] = forms.size();
        z[1] = xGet;
        z[2] = xPost;

        return z; //[Total forms, GET forms, POST forms]
    }

    private static StringBuilder inputsInsideForms(Document doc){
        Elements forms = doc.getElementsByTag("form");
        int i = 0;
        int y;
        StringBuilder m = new StringBuilder();

        for (Element form: forms) {
            m.append("\t[-] FORM - ").append(i++).append(":\n");
            y = 0;
            for (Element input: form.getElementsByTag("input")) {
                m.append("\t[").append(y++).append("] (INPUT NAME): ").append(input.attr("name").toString()).append("  (INPUT TYPE): ").append(input.attr("type")).append("\n");
            }
        }
        return  m;
    }

    private static StringBuilder formPostServerResponse(String URL, Document doc){
        Elements forms = doc.getElementsByTag("form");
        StringBuilder m = new StringBuilder();
        int i = 0;

        for (Element form: forms) {
            if (form.attr("method").equalsIgnoreCase("POST")) {
                m.append("\t[-] FORM - ").append(i++).append(" / Server Response (").append(form.attr("action")).append("):\n");
                Connection.Response rs  = null;
                try {
                    rs = Jsoup.connect(URL + form.attr("action")).userAgent("Chrome/88.0.4324.150")
                                                                .data("asignatura", "practica1")
                                                                .header("matricula","20161565").method(Connection.Method.POST).execute();
                    m.append(rs.body()).append("\n\n");
                } catch (IOException e) {
                    e.printStackTrace();
                    m.append("****** ERROR: ").append(e.getMessage()).append(" ******\n\n");
                }
            }
        }
        return m;
    }

}

