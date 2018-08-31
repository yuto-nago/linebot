package init;

import static spark.Spark.*;

import org.json.JSONObject;

public class Server {
    public static void main(String[] args) {
        //port(3000);
        port(getHerokuAssignedPort());
        System.out.println("http://localhost:3000/hello");
        get("/hello", (request, response) -> "hello world");
        System.out.println("http://localhost:3000/json");
        get("/json", "application/json", (request, response) -> "{\"message\": \"Hello World\"}");

        //post("/message", "application/json", (request, response) -> "{\"message\": \"Hello World\"}");

        get("/message", "application/json", (req, res) -> {

            String str = "result message";

            return "{\"message\": \"Hello World\"}";
        });

        post("/message", "application/json", (req, res) -> {

            String js = req.body();

            JSONObject json = new JSONObject(js);

            JSONObject events = (JSONObject) json.getJSONArray("events").get(0);
            JSONObject message = (JSONObject) events.get("message");
            JSONObject source = (JSONObject) events.get("source");


            // ブロック部：getJSONObjectメソッド
            // キー・バリュー部：getStringメソッド
            // 配列部分：getJSONArrayメソッド＋getJSONObjectメソッドで繰り返し取得
            String name = message.getString("text");

            String id = source.getString("userId");

            System.out.println("message = " + name);

            System.out.println("userId = " + id);

            if (name.equals("今日の天気は")) {
                String strPostUrl = "https://api.line.me/v2/bot/message/push";
                //String strPostUrl = "https://api.line.me/v2/bot/profile/" + id;
                // アカウント情報のJSON文字列
                String JSON = "{\"to\":\"" + id + "\",\"messages\":[{\"type\":\"text\",\"text\":\"晴れです\"}]}";

                // 認証
                HttpSendJSON httpSendJSON = new HttpSendJSON();
                String result = httpSendJSON.callPost(strPostUrl, JSON);
                return "{\"message\":\"" + result + "\"}";

            } else {
                String strPostUrl = "https://api.line.me/v2/bot/message/push";
                // アカウント情報のJSON文字列
                String JSON = "{\"to\":\"" + id + "\",\"messages\":[{\"type\":\"text\",\"text\":\"あかさ\"}]}";
                // 認証
                HttpSendJSON httpSendJSON = new HttpSendJSON();
                String result = httpSendJSON.callPost(strPostUrl, JSON);
                return "{\"message\":\"" + result + "\"}";
            }


            //String str = "result message";

            //return "{\"message\":\"" + str + "\"}";

			/*
			String strPostUrl = "https://api.line.me/v2/bot/message/push";
			// アカウント情報のJSON文字列
			String JSON = "{\"to\":\"U560b544cc4d9609538d3e5568f89594d\",\"messages\":[{\"type\":\"text\",\"text\":\"あかさ\"}]}";
			// 認証
			HttpSendJSON httpSendJSON = new HttpSendJSON();
			String result = httpSendJSON.callPost(strPostUrl, JSON);
			// 結果の表示
			System.out.println(result);
			*/
            //return "{\"message\":\"" + result + "\"}";

        });

    }

    static int getHerokuAssignedPort () {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 3000; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
