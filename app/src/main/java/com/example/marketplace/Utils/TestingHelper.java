package com.example.marketplace.Utils;

import android.os.AsyncTask;
import android.util.Log;

import com.example.marketplace.Model.ProductModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestingHelper {
    public static void addTestProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String[] categories = new String[]{"Rose", "Tulip", "Lily", "Sunflower", "Plants"};
        String[] sellers = new String[]{"Vlad", "Vlad18gm"};
        String[] sellerIds = new String[]{"9AC9co3aNxexYrx8qjjGvF9pcDc2", "QdFnl0zOFWPpnSSDVUePm8Xqb7b2"};

        for (int i = 0; i < 5; i++) {
            ProductModel testProduct = new ProductModel();
            testProduct.setProductId(db.collection("Products").document().getId());
            testProduct.setTitle("Test " + categories[i % categories.length] + " " + (i + 1));
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
                    " զգացմունքների էությունը:");

            int k = i % sellers.length;
            testProduct.setSeller(sellers[k]);
            testProduct.setSellerId(sellerIds[k]);

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
                            .addOnCompleteListener(task -> Log.d("ADD: ", "ADDED "));

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

            return urls.getString("regular");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void deleteTestProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Products").
                whereEqualTo("details", "Արթնացրե՛ք վարդերի հավերժական գրավչությունը այս նրբագեղ ծաղկման շնորհիվ, որոնք գերազանցում են անցողիկ պահերը: Մեր վարդերը, որոնք մանրակրկիտ մշակվել են անզուգական գեղեցկության համար, պարծենում են թավշյա թերթիկներով, որոնք բացվում են գույների սիմֆոնիայի մեջ՝ կրքոտ կարմիրից մինչև նուրբ վարդագույն: Յուրաքանչյուր ծաղիկ բնության արտիստիկության վկայությունն է, որը ներշնչում է հմայիչ բուրմունք, որը մնում է օդում՝ ստեղծելով ռոմանտիկայի և նրբագեղության մթնոլորտ: Բարձրացրեք ցանկացած առիթ սիրո և էլեգանտության այս խորհրդանիշներով, քանի որ դրանք ներառում են հարատև գեղեցկության և սրտանց զգացմունքների էությունը:").
                get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("Products").document(document.getId()).delete()
                                    .addOnCompleteListener(deleteTask -> {
                                        if (deleteTask.isSuccessful()) {
                                            Log.d("DELETE: ", "DELETED " + document.getId());
                                        } else {
                                            Log.e("DELETE ERROR: ", "Error deleting document " + document.getId(), deleteTask.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.e("DELETE ERROR: ", "Error getting documents: ", task.getException());
                    }
                });
    }
}
