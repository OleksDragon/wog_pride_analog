package com.example.wogprideanalog.ui.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.wogprideanalog.R;

public class MapFragment extends Fragment {

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!isNetworkAvailable()) {
            Log.e("MapFragment", "No internet connection");
            Toast.makeText(getContext(), "Немає підключення до Інтернету", Toast.LENGTH_LONG).show();
            return;
        }

        WebView mapWebView = view.findViewById(R.id.map_webview);
        WebSettings webSettings = mapWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setLoadsImagesAutomatically(true);

        mapWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.contains("openstreetmap.org") || url.startsWith("file://")) {
                    return false;
                }
                Log.e("MapFragment", "Blocked URL: " + url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e("MapFragment", "WebView error: " + description + " URL: " + failingUrl);
            }
        });

        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>html, body, #map { height: 100%; margin: 0; }</style>" +
                "<style>" + getAssetFileContent("leaflet.css") + "</style>" +
                "</head>" +
                "<body>" +
                "<div id=\"map\"></div>" +
                "<script>" + getAssetFileContent("leaflet.js") + "</script>" +
                "<script>" +
                "var map = L.map('map').setView([48.3794, 31.1656], 6);" +
                "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {" +
                "attribution: '© OpenStreetMap contributors'" +
                "}).addTo(map);" +
                "L.marker([50.4501, 30.5234]).addTo(map).bindPopup('Заправка в Києві');" +
                "L.marker([46.4825, 30.7233]).addTo(map).bindPopup('Заправка в Одесі');" +
                "L.marker([49.4444, 32.0597]).addTo(map).bindPopup('Заправка в Черкасах');" +
                "L.marker([49.8397, 24.0297]).addTo(map).bindPopup('Заправка у Львові');" +
                "L.marker([48.4647, 35.0462]).addTo(map).bindPopup('Заправка в Дніпрі');" +
                "L.marker([49.9935, 36.2304]).addTo(map).bindPopup('Заправка в Харкові');" +
                "</script>" +
                "</body>" +
                "</html>";

        mapWebView.loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/html", "UTF-8", null);
    }

    private String getAssetFileContent(String fileName) {
        try {
            java.io.InputStream inputStream = getContext().getAssets().open(fileName);
            java.io.ByteArrayOutputStream byteArrayOutputStream = new java.io.ByteArrayOutputStream();
            int i;
            while ((i = inputStream.read()) != -1) {
                byteArrayOutputStream.write(i);
            }
            inputStream.close();
            return byteArrayOutputStream.toString("UTF-8");
        } catch (java.io.IOException e) {
            Log.e("MapFragment", "Error reading asset file: " + fileName, e);
            return "";
        }
    }
}