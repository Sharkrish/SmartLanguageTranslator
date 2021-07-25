package com.thinksmarter786.smartLanguageTranslator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thinksmarter786.smartLanguageTranslator.Utils.SharedPrefs;
import com.thinksmarter786.smartLanguageTranslator.Utils.TranslateSingleton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
/*
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
*/

public class ImageTranslate extends Fragment implements View.OnClickListener{
    private FloatingActionButton camera;
    private LinearLayout imageLayout,textTranslationLayout;
    private ImageView imageSelected;
    private MaterialTextView textFound,textFromTO;
    TranslateSingleton translateSingleton;
    SharedPrefs sharedPrefs;
    @Nullable
    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, @Nullable  ViewGroup container, @Nullable  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_translate,container,false);
        translateSingleton = TranslateSingleton.getInstance();
        sharedPrefs = SharedPrefs.getInstance(getActivity());
        camera = view.findViewById(R.id.camera);
        imageSelected = view.findViewById(R.id.imageSelected);
        imageLayout = view.findViewById(R.id.imageLayout);
        textTranslationLayout = view.findViewById(R.id.textTranslationLayout);
        textFound = view.findViewById(R.id.textFound);
        textFromTO = view.findViewById(R.id.textFromTO);
        camera.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()){
            case R.id.camera:
                callCameraandgallery();
                break;
        }*/
    }

   /* private void callCameraandgallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                System.out.println(">>result>>> " + resultUri);
                Bitmap resultBitmap = null;
                try {
                    resultBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageSelected.setImageBitmap(resultBitmap);
                imageSelected.setScaleType(ImageView.ScaleType.FIT_XY);
                imageLayout.setVisibility(View.VISIBLE);
                if(resultBitmap != null)
                    callTextRecognizer(resultBitmap);
                else
                    Toast.makeText(getContext(),"Unable to parse the image",Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

            }
        }
    }

    private void callTextRecognizer(Bitmap bitmap) {

    }*/
    /**
     * TextRecognizer txtRecognizer = new TextRecognizer.Builder(getActivity()).build();
     *
     *         if (!txtRecognizer.isOperational()) {
     *
     *         } else {
     *             Frame frame = new Frame.Builder().setBitmap(bitmap).build();
     *             SparseArray items = txtRecognizer.detect(frame);
     *             StringBuilder strBuilder = new StringBuilder();
     *             for (int i = 0; i < items.size(); i++) {
     *                 TextBlock item = (TextBlock) items.valueAt(i);
     *                 strBuilder.append(item.getValue());
     *                 strBuilder.append("/");
     *                 for (Text line : item.getComponents()) {
     *                     //extract scanned text lines here
     *                     Log.v("lines", line.getValue());
     *
     *                 }
     *             }
     *
     *         }
     */

}
