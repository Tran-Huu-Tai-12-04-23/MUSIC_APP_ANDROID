package BottomSheet;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.musicplayer.databinding.BottomSheetChooseTypeUploadAvatarBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import Interface.BottomSheetChoosePictureInteractionListener;
import utils.Util;

public class BottomSheetChooseTypeUploadAvatar extends BottomSheetDialogFragment {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100;
    private BottomSheetChoosePictureInteractionListener bottomSheetChoosePictureInteractionListener;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 3;
    private BottomSheetChooseTypeUploadAvatarBinding binding;


    @Override
    public void onAttach(@NonNull Context context) {
        if( getActivity() instanceof BottomSheetChoosePictureInteractionListener) {
            bottomSheetChoosePictureInteractionListener = (BottomSheetChoosePictureInteractionListener) getActivity();
        }
        super.onAttach(context);
    }
    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = BottomSheetChooseTypeUploadAvatarBinding.inflate(getLayoutInflater());
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = binding.getRoot();

        dialog.setContentView(view);

        binding.btnSelectCamera.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                openCamera();
            }

        });

        binding.btnStorage.setOnClickListener(v -> {
            Util.applyClickAnimation(v);
            openFilePicker();
        });


        return dialog;
    }



    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            // Show explanation dialog
            new AlertDialog.Builder(requireContext())
                    .setTitle("Camera Permission Required")
                    .setMessage("This app needs camera permission to take pictures.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                requireActivity().finish();
            }
        }

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
                // Handle permission denial, e.g., disable camera functionality
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Nhận ảnh từ Intent và xử lý nó tại đây (ví dụ: hiển thị ảnh lên ImageView)
            Uri selectedImageUri = data.getData();
            if( bottomSheetChoosePictureInteractionListener != null ) {
                bottomSheetChoosePictureInteractionListener.onChooseImg(selectedImageUri);
            }
        }else  if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            // Nhận Uri của tệp được chọn từ Intent và xử lý nó tại đây
            Uri selectedImageUri = data.getData();
            if( bottomSheetChoosePictureInteractionListener != null ) {
                bottomSheetChoosePictureInteractionListener.onChooseImg(selectedImageUri);
            }
        }
    }





}
