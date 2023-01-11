package com.vid.allvideodownloader.activity;

import static androidx.databinding.DataBindingUtil.inflate;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.vid.allvideodownloader.R;
import com.vid.allvideodownloader.adapter.WhatsappStatusAdapter;

import com.vid.allvideodownloader.databinding.FragmentWhatsappImageBinding;
import com.vid.allvideodownloader.interfaces.FileListClickInterface;
import com.vid.allvideodownloader.model.WhatsappStatusModel;

import java.io.File;
import java.util.ArrayList;

public class WhatsappQImageFragment extends Fragment implements FileListClickInterface {

    FragmentWhatsappImageBinding binding;

    private File[] allfiles;
    private ArrayList<Uri> fileArrayList;
    ArrayList<WhatsappStatusModel> statusModelArrayList;
    private WhatsappStatusAdapter whatsappStatusAdapter;

    public WhatsappQImageFragment(ArrayList<Uri> fileArrayList) {
        this.fileArrayList = fileArrayList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = inflate(inflater, R.layout.fragment_whatsapp_image, container, false);
        initViews();
        return binding.getRoot();
    }

    private void initViews() {
        statusModelArrayList = new ArrayList<>();
        getData();
        binding.swiperefresh.setOnRefreshListener(() -> {
            statusModelArrayList = new ArrayList<>();
            getData();
            binding.swiperefresh.setRefreshing(false);
        });

    }

    private void getData() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            try {
                for (int i = 0; i < fileArrayList.size(); i++) {
                    WhatsappStatusModel whatsappStatusModel;
                    Uri uri = fileArrayList.get(i);
                    if (uri.toString().endsWith(".png") || uri.toString().endsWith(".jpg")) {
                        whatsappStatusModel = new WhatsappStatusModel("WhatsStatus: " + (i + 1),
                                uri,
                                new File(uri.toString()).getAbsolutePath(),
                                new File(uri.toString()).getName());
                        statusModelArrayList.add(whatsappStatusModel);
                    }
                }
                if (statusModelArrayList.size() != 0) {
                    binding.tvNoResult.setVisibility(View.GONE);
                } else {
                    binding.tvNoResult.setVisibility(View.VISIBLE);
                }
                whatsappStatusAdapter = new WhatsappStatusAdapter(getActivity(), statusModelArrayList, WhatsappQImageFragment.this);
                binding.rvFileList.setAdapter(whatsappStatusAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void getPosition(int position, File file) {
//        Intent inNext = new Intent(getActivity(), FullViewActivity.class);
//        inNext.putExtra("ImageDataFile", fileArrayList);
//        inNext.putExtra("Position", position);
//        getActivity().startActivity(inNext);

    }

    }
