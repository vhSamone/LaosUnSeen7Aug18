package masterung.androidthai.in.th.laosunseen.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import masterung.androidthai.in.th.laosunseen.MainActivity;
import masterung.androidthai.in.th.laosunseen.R;
import masterung.androidthai.in.th.laosunseen.utility.MyAlert;

public class RegisterFragment extends Fragment {

    //    Explicit
    private Uri uri;
    private ImageView imageView;
    private boolean aBoolean = true;
    private String nameString ,emailString, passwordString,
    uidString,pathURLString,myPostString;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        create Toolbar
        createToolbar();

//        Photo Controller
        photoController();

    }//Main class

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_register, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemUpload) {
            uploadProcess();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadProcess() {

        EditText nameEditText = getView().findViewById(R.id.edtName);
        EditText emailEditText = getView().findViewById(R.id.edtEmail);
        EditText passwordEditText = getView().findViewById(R.id.editPassword);

//        Get Value From editText
        nameString = nameEditText.getText().toString().trim();
        emailString = emailEditText.getText().toString().trim();
        passwordString = passwordEditText.getText().toString().trim();


//        Check choose photo
        if (aBoolean) {
//            Non Choose Photo
            MyAlert myAlert = new MyAlert(getActivity());
            myAlert.normalDialog("None Choose Photo", "Please Choose Photo");
        } else if (nameString.isEmpty() || emailString.isEmpty() || passwordString.isEmpty()) {
//            Have space
            MyAlert myAlert = new MyAlert(getActivity());
            myAlert.normalDialog("Have Sapace", "Please Fill All Every Blank");

        } else {
//            No space

            craceteAuthentication();
            uploadPhotoToFirebase();


        }
    }

    private void craceteAuthentication() {

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            uidString = firebaseAuth.getCurrentUser().getUid();
                            Log.d("8AugV1", "uidString ==>" + uidString);

                        } else {
                            MyAlert myAlert = new MyAlert(getActivity());
                            myAlert.normalDialog("Cannot Register",
                                    "Because ==>"+task.getException());
                            Log.d("8AugV1", "Error==>" + task.getException().getMessage());
                        }
                    }
                });


    }

    private void uploadPhotoToFirebase() {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference storageReference1 = storageReference.child("Avata/"+ nameString);

        storageReference1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "Sucess Upload Photo", Toast.LENGTH_SHORT).show();
                findPathURLPhoto();
                createPost();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Cannot Upload Photo", Toast.LENGTH_SHORT).show();
                Log.d("8AugV1", "e ==>" + e.toString());
            }
        });



    }//uploadPhotoToFirebase()

    private void createPost() {

        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add("Hello");
        myPostString = stringArrayList.toString();
        Log.d("9AugV1", "myPost ==>" + myPostString);
    }

    private void findPathURLPhoto() {

        try {

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference();
            final String[] urlStrings = new String[1];

            storageReference.child("Avata").child(nameString)
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            urlStrings[0] = uri.toString();
                            pathURLString = urlStrings[0];
                            Log.d("9AugV1", "urlStrings[0]==>" + urlStrings[0]);
                            Log.d("9AugV1", "pathURL==>" + pathURLString);//check URL
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("9AugV1", "e Error ==>" + e.toString());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();//take error into Char is red color
        }

    }// findPath

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {

            uri = data.getData();
            aBoolean = false;
            try {

                Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, 400, 300, true);
                imageView.setImageBitmap(bitmap1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Please Choose Photo", Toast.LENGTH_SHORT).show();
        }
    }

    private void photoController() {
        imageView = getView().findViewById(R.id.imvPhoto);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // invoke picture
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");// any software to open photo
                startActivityForResult(Intent.createChooser(intent, "Please Choose app"), 1);
            }
        });
    }

    private void createToolbar() {
        Toolbar toolbar = getView().findViewById(R.id.toolbarRegister);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Register"); //set title on toolbar
        ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("Please Should Photo and fill All Blank");

        //enable arrow navigator
        ((MainActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //activate arrow navigator
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack(); // press arrow navigator and back to Home
            }
        });
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        return view;
    }
}
