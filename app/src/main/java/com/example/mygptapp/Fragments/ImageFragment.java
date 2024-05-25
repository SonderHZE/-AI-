package com.example.mygptapp.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.mygptapp.R;
import com.example.mygptapp.Uils.JsonUtil;
import com.example.mygptapp.ViewModelHolders.ViewModelHolder;
import com.example.mygptapp.pojo.MyResponse;

import java.io.IOException;

public class ImageFragment extends Fragment {
    private ImageView imageView;
    private EditText editText;
    private ProgressBar progressBar;

    public ImageFragment() {
    }


    public static ImageFragment newInstance() {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        imageView = view.findViewById(R.id.image_view);
        editText = view.findViewById(R.id.url_input);
        progressBar = view.findViewById(R.id.progress_bar);

        editText.setOnEditorActionListener((v, actionId, event) -> {
            progressBar.setVisibility(View.VISIBLE);
            ViewModelHolder.getInstance().getChatViewModel().sendImageMessage(editText.getText().toString(), getContext(), new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Log.e("ChatViewModel", e.getMessage());
                    });
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    getActivity().runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        MyResponse myResponse = JsonUtil.parseJson(res);
                        if(myResponse.getStatus().equals("success")) {
                            String url = myResponse.getData().getAsString();
                            getActivity().runOnUiThread(() -> loadImage(url));
                        } else {
                            Log.e("ChatViewModel", myResponse.getMessage());
                        }
                    } else {
                        Log.e("ChatViewModel", "response failed");
                    }
                }
            });
            return true;
        });

        return view;
    }

    private void loadImage(String url) {
        Glide.with(this).load(url).into(imageView);
    }
}