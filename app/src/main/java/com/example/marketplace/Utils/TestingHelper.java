package com.example.marketplace.Utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.example.marketplace.Model.ProductModel;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestingHelper {
    @SuppressLint("StaticFieldLeak")
    public static void addTestProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String[] categories = new String[]{"Rose", "Tulip", "Lily", "Sunflower", "Plants"};

        for (int i = 0; i < 12; i++) {
            ProductModel testProduct = new ProductModel();
            testProduct.setProductId(db.collection("Products").document().getId());
            testProduct.setTitle(categories[i % categories.length] + " " + (i + 33));
            testProduct.setCategory(categories[i % categories.length]);
            int randomPrice = (int) (Math.random() * (15000 - 400 + 1)) + 400;
            testProduct.setPrice(randomPrice);
            testProduct.setDetails("Արթնացրե՛ք վարդերի հավերժական գրավչությունը այս նրբագեղ " +
                    "ծաղկման շնորհիվ, որոնք գերազանցում են անցողիկ պահերը: Մեր վարդերը, " +
                    "որոնք մանրակրկիտ մշակվել են անզուգական գեղեցկության համար, պարծենում են " +
                    "թավշյա թերթիկներով, որոնք բացվում են գույների սիմֆոնիայի մեջ՝ կրքոտ կարմիրից " +
                    "մինչև նուրբ վարդագույն: Յուրաքանչյուր ծաղիկ բնության արտիստիկության վկայությունն " +
                    "է, որը ներշնչում է հմայիչ բուրմունք, որը մնում է օդում՝ ստեղծելով ռոմանտիկայի և " +
                    "նրբագեղության մթնոլորտ: Բարձրացրեք ցանկացած առիթ սիրո և էլեգանտության այս " +
                    "խորհրդանիշներով, քանի որ դրանք ներառում են հարատև գեղեցկության և սրտանց" +
                    " զգացմունքների էությունը: " + (i + 33));

            testProduct.setSeller("Test Manufacturer " + (i + 33));

            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    return getRandomUnsplashImageUrl();
                }

                @Override
                protected void onPostExecute(String unsplashImageUrl) {
                    if (unsplashImageUrl != null) {
                        testProduct.setPhoto(unsplashImageUrl);
                    } else {
                        testProduct.setPhoto("https://picsum.photos/200/200?random=" + 1);
                    }

                    db.collection("Products").document(testProduct.getProductId()).set(testProduct)
                            .addOnCompleteListener(task -> {
                                Log.d("ADD: ", "ADDED ");
                            });

                }
            }.execute();
        }
    }

    private static String getRandomUnsplashImageUrl() {
        try {
            URL url = new URL("https://api.unsplash.com/photos/random?client_id=buPJCBomlHu7lQ6Knv1fbxfRWiwu0-YJgu_tnR3POZU&query=flower");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            JSONObject json = new JSONObject(response.toString());
            JSONObject urls = json.getJSONObject("urls");
            String imageUrl = urls.getString("regular");

            return imageUrl;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
