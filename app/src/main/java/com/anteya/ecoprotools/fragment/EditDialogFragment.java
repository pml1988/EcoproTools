package com.anteya.ecoprotools.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anteya.ecoprotools.MainActivity;
import com.anteya.ecoprotools.R;
import com.anteya.ecoprotools.object.Ecopro;
import com.anteya.ecoprotools.object.ProjectTools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yenlungchen on 2016/2/25.
 */
public class EditDialogFragment extends DialogFragment {

    private static final String TAG = "EditDialogFragment";

    private ProjectTools projectTools;

    private String name;

    private String ipAddress;

    private String ipAddress_Wan;

    private String port ="8023";

    private int password;

    private boolean flag_name = false, flag_local = false, flag_wan = false, flag_pw = false;


    private Ecopro ecopro;

    public boolean isNewEcopro = false;

    private ImageView im1, im2, im3, im4;

    String IPADDRESS_PATTERN = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";
    Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);

    private TextView textViewNameTitle, textViewIpAddressTitle;

    private EditText editTextName, editTextIpAddress, editTextPassword, editTextIpAddress_wan;

    public EditDialogFragment() {

    }

    public static EditDialogFragment newInstance(Ecopro ecopro, EditDialogFragmentCallback editDialogFragmentCallback) {

        EditDialogFragment editDialogFragment = new EditDialogFragment();

        editDialogFragment.setEditDialogFragmentCallback(editDialogFragmentCallback);

        editDialogFragment.isNewEcopro = false;

        editDialogFragment.ecopro = ecopro;

        return editDialogFragment;
    }

    public static EditDialogFragment newInstance(EditDialogFragmentCallback editDialogFragmentCallback) {

        EditDialogFragment editDialogFragment = new EditDialogFragment();

        editDialogFragment.setEditDialogFragmentCallback(editDialogFragmentCallback);

        editDialogFragment.isNewEcopro = true;

        return editDialogFragment;
    }

    private EditDialogFragmentCallback editDialogFragmentCallback;

    public void setEditDialogFragmentCallback(EditDialogFragmentCallback editDialogFragmentCallback) {
        this.editDialogFragmentCallback = editDialogFragmentCallback;
    }

    public interface EditDialogFragmentCallback {

        void addNewEcopro(Ecopro ecopro);

        void updateEcopro(Ecopro ecopro);

        void deleteEcopro(Ecopro ecopro);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }

    AlertDialog.Builder builder;

    AlertDialog alertClient;

    @Override
    public void onStart() {
        super.onStart();

    }

    public Dialog onCreateDialog(Bundle saveInstanceState) {

        builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_edit, null);


        im1 = (ImageView) view.findViewById(R.id.dialogFragmentEdit_im1);
        im2 = (ImageView) view.findViewById(R.id.dialogFragmentEdit_im2);
        im3 = (ImageView) view.findViewById(R.id.dialogFragmentEdit_im3);
        im4 = (ImageView) view.findViewById(R.id.dialogFragmentEdit_im4);

        // initial View
        textViewNameTitle = (TextView) view.findViewById(R.id.dialogFragmentEdit_textViewNameTitle);
        textViewIpAddressTitle = (TextView) view.findViewById(R.id.dialogFragmentEdit_textViewIpAddressTitle);
        editTextName = (EditText) view.findViewById(R.id.dialogFragmentEdit_editTextName);
        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (editTextName.getText().toString().length() > 0) {
                    im1.setImageResource(R.drawable.checkok);
                    flag_name = true;
                } else {
                    im1.setImageResource(R.drawable.checken);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        editTextIpAddress = (EditText) view.findViewById(R.id.dialogFragmentEdit_editTextIpAddress);

        editTextIpAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                Matcher matcher = pattern.matcher(editTextIpAddress.getText().toString());
                System.out.println("判斷IP格式:" + editTextIpAddress.getText().toString());

                String[] tempipport = editTextIpAddress.getText().toString().split(":");

                if (tempipport.length > 1) {
                    port = tempipport[1];
                    System.out.println("判斷IP端口數據:" + port);
                }


                if (matcher.find()) {

                    String tempstr = tempipport[0];
                    System.out.println("判斷IP位址數據:" + tempstr);
                    String[] tmp = tempstr.split("\\.");
                    if (tmp.length == 4) {
                        if (tempstr.substring(tempstr.length() - 1).equals(".")) {
                            System.out.println("判斷IP最後是點");
                            im2.setImageResource(R.drawable.checken);
                        } else {
                            System.out.println("判斷IP最後不是點:");
                            System.out.println("判斷IP段落:" + tmp.length);
                            for (int i = 0; i < tmp.length; i++) {
                                if (Integer.parseInt(tmp[i]) > 255) {
                                    System.out.println("判斷IP錯誤大於255:" + i);
                                    im2.setImageResource(R.drawable.checken);
                                    return;
                                } else {


                                    flag_local = true;
                                    im2.setImageResource(R.drawable.checkok);

//                                    if (i == 0) {
//                                        if (Integer.parseInt(tmp[0]) != 192 || Integer.parseInt(tmp[0]) != 10 || Integer.parseInt(tmp[0]) != 172) {
//                                            im2.setImageResource(R.drawable.checken);
//                                            Toast.makeText(getActivity(), "不屬於區域網路IP", Toast.LENGTH_SHORT).show();
//                                            System.out.println("判斷IP不屬於區域網路:" + i);
//                                            return;
//                                        }
//                                    } else {
//                                        System.out.println("判斷IP正確");
//                                        flag_local = true;
//                                        im2.setImageResource(R.drawable.checkok);
//
//                                    }
                                }
                            }
                        }
                    } else {
                        System.out.println("判斷IP錯誤1:" + tmp.length);
                        im2.setImageResource(R.drawable.checken);
                    }
                } else {
                    System.out.println("判斷IP錯誤格式不正確");
                    im2.setImageResource(R.drawable.checken);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        editTextIpAddress_wan = (EditText) view.findViewById(R.id.dialogFragmentEdit_editTextIpAddress_Wan);

        editTextIpAddress_wan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Matcher matcher = pattern.matcher(editTextIpAddress_wan.getText().toString());
                System.out.println("判斷IP格式:" + editTextIpAddress_wan.getText().toString());
                if (matcher.find()) {
                    String tempstr = editTextIpAddress_wan.getText().toString();
                    String[] tmp = tempstr.split("\\.");
                    if (tmp.length == 4) {
                        if (tempstr.substring(tempstr.length() - 1).equals(".")) {
                            System.out.println("判斷IP最後是點");
                            im3.setImageResource(R.drawable.checken);
                        } else {
                            System.out.println("判斷IP最後不是點:");
                            System.out.println("判斷IP段落:" + tmp.length);
                            for (int i = 0; i < tmp.length; i++) {
                                if (Integer.parseInt(tmp[i]) > 255) {
                                    System.out.println("判斷IP錯誤大於255:" + i);
                                    im3.setImageResource(R.drawable.checken);
                                    return;
                                } else {
                                    System.out.println("判斷IP正確");
                                    flag_wan = true;
                                    im3.setImageResource(R.drawable.checkok);
                                }
                            }
                        }
                    } else {
                        System.out.println("判斷IP錯誤1:" + tmp.length);
                        im3.setImageResource(R.drawable.checken);
                    }
                } else {
                    System.out.println("判斷IP錯誤2");
                    im3.setImageResource(R.drawable.checken);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        editTextPassword = (EditText) view.findViewById(R.id.dialogFragmentEdit_editTextIpassword);

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (editTextPassword.getText().toString().length() == 4) {
                    im4.setImageResource(R.drawable.checkok);
                    flag_pw = true;
                } else {
                    im4.setImageResource(R.drawable.checken);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // setup
        builder.setView(view);
        if (isNewEcopro) {
            builder.setPositiveButton("Save", addEcoproDialogClickListener);
            builder.setNegativeButton("Cancel", null);

        } else {
            builder.setPositiveButton("Save", updateEcoproDialogClickListener);
            builder.setNegativeButton("Cancel", null);
            builder.setNeutralButton("Delete", deleteEcoproDialogClickListener);

            try {
                editTextName.setText(ecopro.getName());
                editTextIpAddress.setText(ecopro.getIpAddress());
                editTextIpAddress_wan.setText(ecopro.getIpAddress_wan());
                editTextPassword.setText(ecopro.getPassword());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return builder.create();
    }


    // region dialog click listener

    private DialogInterface.OnClickListener addEcoproDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            if (flag_name != true) {
                Toast.makeText(getActivity().getApplicationContext(), "Please Input Name", Toast.LENGTH_SHORT).show();

            } else if (flag_local != true) {
                Toast.makeText(getActivity().getApplicationContext(), "Please Input Local IP address", Toast.LENGTH_SHORT).show();

            } else if (flag_wan != true) {
                Toast.makeText(getActivity().getApplicationContext(), "Please Input Wan IP address", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            } else if (flag_pw != true) {
                Toast.makeText(getActivity().getApplicationContext(), "最多四碼，請重新輸入", Toast.LENGTH_SHORT).show();

            } else {
                ecopro = new Ecopro();

                ecopro.setName(editTextName.getText().toString());

                ecopro.setIpAddress(editTextIpAddress.getText().toString());

                ecopro.setIpAddress_wan(editTextIpAddress_wan.getText().toString());

                ecopro.setPassword(editTextPassword.getText().toString());

                if (editDialogFragmentCallback != null) {
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

            ecopro.setIpAddress_wan(editTextIpAddress_wan.getText().toString());

            ecopro.setPassword(editTextPassword.getText().toString());

            if (editDialogFragmentCallback != null) {
                editDialogFragmentCallback.updateEcopro(ecopro);
            }
        }
    };

    private DialogInterface.OnClickListener deleteEcoproDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            if (editDialogFragmentCallback != null) {
                editDialogFragmentCallback.deleteEcopro(ecopro);
            }
        }
    };

    // endregion

}
