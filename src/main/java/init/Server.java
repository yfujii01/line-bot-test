package init;

import static spark.Spark.*;

import org.json.JSONObject;
import util.HttpSendJSON;

public class Server {
    public static void main(String[] args) {
        port(getHerokuAssignedPort());

        System.out.println("http://localhost:3000/hello");
        get("/hello", (request, response) -> "hello world");

        System.out.println("http://localhost:3000/json");
        get("/json", "application/json", (request, response) -> "{\"message\": \"Hello World\"}");

        get("/test", (request, response) -> "i am test page");

        post("/message", "application/json", (request, response) -> "{\"message\": \"Hello World\"}");

        get("/aaa", "application/json", (req, res) -> {

            String str = "result message";

            return "{\"message\": \"" + str + "\"}";
        });


        post("/bbb", "application/json", (req, res) -> {
            String json = req.body();

            JSONObject jo = new JSONObject(json);

            // ブロック部：getJSONObjectメソッド
            // キー・バリュー部：getStringメソッド
            // 配列部分：getJSONArrayメソッド＋getJSONObjectメソッドで繰り返し取得
            JSONObject events = (JSONObject) jo.getJSONArray("events").get(0);
            JSONObject source = (JSONObject) events.get("source");

            JSONObject message = (JSONObject) events.get("message");

            // 送られたメッセージ
            String text = (String) message.get("text");

            // 送ってきた人のid
            String userId = (String) source.get("userId");

            String retStr = "";
            switch (text) {
                case "おはよう":
                    retStr = "はい、おはようございます。";
                    break;
                case "こんにちは":
                    retStr = "はい、こんにちは。";
                    break;
                case "しね":
                    retStr = "おまえがな";
                    break;
                default:
                    retStr = "おはよう、こんにちは、しね に反応するよ";
            }

            // *************************************************************
            // bot反応
            // *************************************************************
            // 送信先URL
            String strPostUrl = "https://api.line.me/v2/bot/message/push";

            // アカウント情報のJSON文字列
            String JSON = "{\"to\":\"" + userId + "\",\"messages\":[{\"type\":\"text\",\"text\":\"" + retStr + "\"}]}";

            // 認証
            HttpSendJSON httpSendJSON = new HttpSendJSON();
            String result = httpSendJSON.callPost(strPostUrl, JSON);

            // 結果の表示
            System.out.println(result);

            return json;
        });
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 3000; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
