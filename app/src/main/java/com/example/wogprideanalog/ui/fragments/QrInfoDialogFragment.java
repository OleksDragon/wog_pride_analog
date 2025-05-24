package com.example.wogprideanalog.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.wogprideanalog.R;

public class QrInfoDialogFragment extends DialogFragment {

    private static final String ARG_QR_BITMAP = "qr_bitmap";

    private Bitmap qrBitmap;

    public static QrInfoDialogFragment newInstance(Bitmap qrBitmap) {
        QrInfoDialogFragment fragment = new QrInfoDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_QR_BITMAP, qrBitmap);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            qrBitmap = getArguments().getParcelable(ARG_QR_BITMAP);
        }
        setStyle(STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_qr_info, container, false);

        ImageView qrImageView = view.findViewById(R.id.dialog_qr_image);
        TextView instructionTextView = view.findViewById(R.id.dialog_instruction_text);

        if (qrBitmap != null) {
            qrImageView.setImageBitmap(qrBitmap);
        } else {
            qrImageView.setImageResource(android.R.drawable.ic_dialog_alert);
        }

        instructionTextView.setText("Надайте цей QR-код на касі WOG для нарахування бонусів, отримання знижок або підтвердження вашої картки учасника.");

        return view;
    }
}
