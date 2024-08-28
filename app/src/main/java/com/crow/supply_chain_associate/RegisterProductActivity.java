package com.crow.supply_chain_associate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.crow.supply_chain_associate.dto.Category;
import com.crow.supply_chain_associate.dto.Product;
import com.crow.supply_chain_associate.service.APIClient;
import com.google.zxing.common.StringUtils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.net.Uri;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegisterProductActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    ImageButton scanBtn;
    Button submitProductButton;
    TextView messageText;
    EditText nameText,descriText,priceText,barCodeText,reorderQtyText,packedHeightText,packedWeightText,packedDepth,packedWidth;
    // One Button
    ImageButton BSelectImage;
    // One Preview Image
    ImageView IVPreviewImage;
    // constant to compare
    // the activity result code
    int SELECT_PICTURE = 200;
    Uri currentProduct=null;
    Spinner categSpinner;
    List<Category> categories=new ArrayList<>();
    private String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_product);
        populateCategories();
        // referencing and initializing
        // the button and textviews
        scanBtn = findViewById(R.id.scanBtn);
        messageText = findViewById(R.id.textContent);
        BSelectImage = findViewById(R.id.BSelectImage);
        IVPreviewImage = findViewById(R.id.IVPreviewImage);
        submitProductButton=findViewById(R.id.submitProductButton);
        categSpinner=(Spinner) findViewById(R.id.sp_min);
        nameText=findViewById(R.id.name);
        descriText=findViewById(R.id.description);
        priceText=findViewById(R.id.price);
        barCodeText=findViewById(R.id.textContent);
        reorderQtyText=findViewById(R.id.reOrderQty);
        packedHeightText=findViewById(R.id.packedHeight);
        packedWeightText=findViewById(R.id.packedWeight);
        packedWidth=findViewById(R.id.packedWidth);
        packedDepth=findViewById(R.id.packedDepth);

        categSpinner.setOnItemSelectedListener(this);
        // adding listener to the button
        scanBtn.setOnClickListener(this);
        System.err.println(categories.size());


        BSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        submitProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDrawable(RegisterProductActivity.this,IVPreviewImage.getDrawable());
            }
        });
    }
    @Override
    public void onClick(View v) {
        // we need to create the object
        // of IntentIntegrator class
        // which is the class of QR library
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message
                messageText.setText(intentResult.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        // this function is triggered when user
        // selects the image from the imageChooser
        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    currentProduct=selectedImageUri;
                    IVPreviewImage.setImageURI(selectedImageUri);
                }
            }
        }
    }

    // this function is triggered when
    // the Select Image Button is clicked
    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }



    public Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public File bitmapToFile(Context context, Bitmap bitmap, String fileName) throws IOException {
        File file = new File(context.getCacheDir(), fileName); // You might use a different directory if needed
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // Compress the bitmap
        fos.flush();
        fos.close();
        return file;
    }

    public MultipartBody.Part prepareFilePart(String partName, File file) {
        // Use RequestBody to create a part for the file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"),file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    public void uploadDrawable(Context context, Drawable drawable) {
        try {
            // Convert Drawable to Bitmap
            Bitmap bitmap = drawableToBitmap(drawable);
            // Convert Bitmap to File
            File file = bitmapToFile(context, bitmap, nameText.getText().toString()+".png");
            // Prepare file part for Retrofit
            MultipartBody.Part body = prepareFilePart("file", file);
            Log.e("IMAGE", file.getAbsolutePath());
            // Assuming you have a Retrofit service interface for file upload
            Call<String> call = APIClient.getInstance().getApi().uploadAttachment(body);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    // Handle successful response
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterProductActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("Upload", "File upload failed: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    // Handle failure
                    t.printStackTrace();
                    Log.d("Upload", "File upload error: " + t.getMessage());
                }
            });
            if(nameText.getText()==null){
                Toast.makeText(this, "name can't be empty", Toast.LENGTH_SHORT).show();
            }
            if(descriText.getText()==null){
                Toast.makeText(this, "description can't be empty", Toast.LENGTH_SHORT).show();
            }
            if(priceText.getText()==null){
                Toast.makeText(this, "price can't be empty", Toast.LENGTH_SHORT).show();
            }
            if(reorderQtyText.getText()==null){
                Toast.makeText(this, "reorder can't be empty", Toast.LENGTH_SHORT).show();
            }
            Product product=new Product();
            product.setName(nameText.getText().toString());
            product.setDescription(descriText.getText().toString());
            product.setPrice(Float.parseFloat(priceText.getText().toString().trim()));
            product.setReorderQty(Integer.parseInt(reorderQtyText.getText().toString().trim()));
            product.setBarCode(barCodeText.getText().toString());
            Category category=new Category();
            category.setId(categoryId);
            product.setCategory(category);

            Call<Product> call1 = APIClient.getInstance().getApi().addProduct(product);
            call1.enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    // Handle successful response
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterProductActivity.this, response.body().getName()+" updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("CatFail", "File upload failed: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<Product> call, Throwable t) {
                    // Handle failure
                    t.printStackTrace();
                    Log.d("CateFail", "File upload error: " + t.getMessage());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Category category=(Category) parent.getItemAtPosition(position);
        categoryId=category.getId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public void populateCategories(){
        Call<List<Category>> call = APIClient.getInstance().getApi().getCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                // Handle successful response
                if (response.isSuccessful()) {
                    ArrayAdapter<Category> ad= new ArrayAdapter<>(RegisterProductActivity.this,android.R.layout.simple_spinner_item, response.body());
                    ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categSpinner.setAdapter(ad);
                } else {
                    Log.d("CatFail", "File upload failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                Log.d("CateFail", "File upload error: " + t.getMessage());
            }
        });
    }
}