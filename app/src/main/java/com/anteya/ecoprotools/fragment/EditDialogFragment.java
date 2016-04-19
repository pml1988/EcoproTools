package com.anteya.ecoprotools.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anteya.ecoprotools.MainActivity;
import com.anteya.ecoprotools.R;
import com.anteya.ecoprotools.object.Ecopro;

/**
 * Created by yenlungchen on 2016/2/25.
 */
public class EditDialogFragment extends DialogFragment {

    private static final String TAG = "EditDialogFragment";

    private String name;

    private String ipAddress;

    private int password;

    private Ecopro ecopro;

    public boolean isNewEcopro = false;



    private TextView textViewNameTitle, textViewIpAddressTitle;

    private EditText editTextName, editTextIpAddress , editTextPassword;

    public EditDialogFragment(){

    }

    public static EditDialogFragment newInstance(Ecopro ecopro, EditDialogFragmentCallback editDialogFragmentCallback){

        EditDialogFragment editDialogFragment = new EditDialogFragment();

        editDialogFragment.setEditDialogFragmentCallback(editDialogFragmentCallback);

        editDialogFragment.isNewEcopro = false;

        editDialogFragment.ecopro = ecopro;

        return editDialogFragment;
    }
    public static EditDialogFragment newInstance(EditDialogFragmentCallback editDialogFragmentCallback){

        EditDialogFragment editDialogFragment = new EditDialogFragment();

        editDialogFragment.setEditDialogFragmentCallback(editDialogFragmentCallback);

        editDialogFragment.isNewEcopro = true;

        return editDialogFragment;
    }

    private EditDialogFragmentCallback editDialogFragmentCallback;

    public void setEditDialogFragmentCallback(EditDialogFragmentCallback editDialogFragmentCallback) {
        this.editDialogFragmentCallback = editDialogFragmentCallback;
    }

    public interface EditDialogFragmentCallback{

        void addNewEcopro(Ecopro ecopro);

        void updateEcopro(Ecopro ecopro);

        void deleteEcopro(Ecopro ecopro);
    }



    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }

    public Dialog onCreateDialog(Bundle saveInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_edit, null);

        // initial View
        textViewNameTitle = (TextView) view.findViewById(R.id.dialogFragmentEdit_textViewNameTitle);
        textViewIpAddressTitle = (TextView) view.findViewById(R.id.dialogFragmentEdit_textViewIpAddressTitle);
        editTextName = (EditText) view.findViewById(R.id.dialogFragmentEdit_editTextName);
        editTextIpAddress = (EditText) view.findViewById(R.id.dialogFragmentEdit_editTextIpAddress);
        editTextPassword = (EditText) view.findViewById(R.id.dialogFragmentEdit_editTextIpassword);

        // setup
        builder.setView(view);
        if(isNewEcopro){
            builder.setPositiveButton("Save", addEcoproDialogClickListener);
            builder.setNegativeButton("Cancel", null);
        }else{
            builder.setPositiveButton("Save", updateEcoproDialogClickListener);
            builder.setNegativeButton("Cancel", null);
            builder.setNeutralButton("Delete", deleteEcoproDialogClickListener);

            editTextName.setText(ecopro.getName());
            editTextIpAddress.setText(ecopro.getIpAddress());
            editTextPassword.setText(ecopro.getPassword());
        }

        return builder.create();
    }

    // region dialog click listener

    private DialogInterface.OnClickListener addEcoproDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            if(editTextPassword.getText().toString().length()!=4)
            {
                Toast.makeText(getActivity().getApplicationContext(), "最多四碼，請重新輸入",Toast.LENGTH_SHORT).show();
            }
            else {

                ecopro = new Ecopro();

                ecopro.setName(editTextName.getText().toString());

                ecopro.setIpAddress(editTextIpAddress.getText().toString());

                ecopro.setPassword(editTextPassword.getText().toString());

                if(editDialogFragmentCallback != null){
                    editDialogFragmentCallback.addNewEcopro(ecopro);
                }

            }

        }
    };

    private DialogInterface.OnClickListener updateEcoproDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            ecopro.setName(editTextName.getText().toString());

            ecopro.setIpAddress(editTextIpAddress.getText().toString());

            ecopro.setPassword(editTextPassword.getText().toString());

            if(editDialogFragmentCallback != null){
                editDialogFragmentCallback.updateEcopro(ecopro);
            }
        }
    };

    private DialogInterface.OnClickListener deleteEcoproDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            if(editDialogFragmentCallback != null){
                editDialogFragmentCallback.deleteEcopro(ecopro);
            }
        }
    };

    // endregion

}
