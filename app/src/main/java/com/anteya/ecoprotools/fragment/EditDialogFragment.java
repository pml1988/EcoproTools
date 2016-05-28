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

    private String port = "8023";

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
        editDialogFragment.setCancelable(false);
        editDialogFragment.ecopro = ecopro;

        return editDialogFragment;
    }

    public static EditDialogFragment newInstance(EditDialogFragmentCallback editDialogFragmentCallback) {

        EditDialogFragment editDialogFragment = new EditDialogFragment();

        editDialogFragment.setEditDialogFragmentCallback(editDialogFragmentCallback);

        editDialogFragment.isNewEcopro = true;
        editDialogFragment.setCancelable(false);
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

                String tempipaddress = editTextIpAddress.getText().toString();


                boolean flag1 = false;


                String[] tempipport = ProjectTools.changesemicolon(tempipaddress).split(":");

                if (tempipport.length == 2) {
                    try {
                        port = Integer.parseInt(tempipport[1]) + "";
                        System.out.println("判斷IP1端口數據:" + tempipport[0] + "" + port);
                        flag1 = true;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        System.out.println("判斷IP1端口錯誤:" + port + " " + e);
                    }

                } else if (tempipport.length == 1) {

                    try {
                        System.out.println("判斷IP1:" + tempipport[0]);
                        if (tempipaddress.substring(tempipaddress.length() - 1).equals(".") || tempipaddress.substring(tempipaddress.length() - 1).equals(":")) {
                            System.out.println("判斷IP1有.;:");
                            //  im2.setImageResource(R.drawable.checken);
                        } else {
                            flag1 = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Matcher matcher = pattern.matcher(tempipport[0]);


                if (matcher.find() && flag1 == true) {
                    System.out.println("判斷IP1格式if:" + tempipport[0]);
                    String[] tmp = tempipport[0].split("\\.");


                    if (tmp.length == 4) {

                        System.out.println("判斷IP1格式4:" + tempipport[0]);


                        for (int i = 0; i < tmp.length; i++) {
                            System.out.println("判斷IP1拆解數值:" + tmp[i]);
                            try {
                                if (Integer.parseInt(tmp[i]) > 255) {
                                    System.out.println("判斷IP1錯誤大於255:" + i);
                                    im2.setImageResource(R.drawable.checken);
                                } else {

                                    im2.setImageResource(R.drawable.checkok);
                                    System.out.println("判斷IP正確:" + i);

                                    if (Integer.parseInt(tmp[0]) == 192 || Integer.parseInt(tmp[0]) == 10 || Integer.parseInt(tmp[0]) == 172) {
                                        im2.setImageResource(R.drawable.checkok);
                                        flag_local = true;
                                        System.out.println("判斷IP1屬於區域網路:" + tmp[0]);
                                    } else {
                                        im2.setImageResource(R.drawable.checken);
                                        System.out.println("判斷IP1不屬於區域網路IP:" + tmp[0]);
                                        Toast.makeText(getActivity(), "不屬於區域網路IP", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    System.out.println("判斷IP1格式else:" + tempipport[0]);
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


                try {
                    Matcher matcher = pattern.matcher(editTextIpAddress_wan.getText().toString());
                    System.out.println("判斷IP2端口數據:" + editTextIpAddress_wan.getText().toString());

                    String temp = (ProjectTools.changesemicolon(editTextIpAddress_wan.getText().toString()));

                    String[] tempipport = temp.split(":");

                    if (tempipport[0].equals("http")) {


                        if (tempipport.length == 3) {
                            port = tempipport[2];
                        }

                        im3.setImageResource(R.drawable.checkok);
                        flag_wan = true;
                    } else {


                        if (tempipport.length > 1) {
                            port = tempipport[1];
                            System.out.println("判斷IP2端口數據=:" + port);
                        }


                        System.out.println("判斷IP2端口數據:" + tempipport[0]);


                        String[] tmp = tempipport[0].split("\\.");

                        if (tmp.length == 4) {


                            im3.setImageResource(R.drawable.checkok);
                            flag_wan = true;

                        } else {
                            im3.setImageResource(R.drawable.checken);
                            flag_wan = true;
                        }


                    }


                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    System.out.println("判斷IP2例外:" + e);
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
                                                }

        );

        // setup
        builder.setView(view);
        if (isNewEcopro)

        {
            builder.setPositiveButton("Save", addEcoproDialogClickListener);

            builder.setNegativeButton("Cancel", null);

        } else

        {
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

            System.out.println("關閉：" + which);

            if (flag_name != true) {
                Toast.makeText(getActivity().getApplicationContext(), "Please Input Name", Toast.LENGTH_SHORT).show();

            } else if (flag_local != true) {
                Toast.makeText(getActivity().getApplicationContext(), "Please Input Local IP address", Toast.LENGTH_SHORT).show();

            }else if (flag_pw != true) {
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

                try {
                    ecopro.setName(editTextName.getText().toString());

                    ecopro.setIpAddress(editTextIpAddress.getText().toString());

                    ecopro.setIpAddress_wan(editTextIpAddress_wan.getText().toString());

                    ecopro.setPassword(editTextPassword.getText().toString());

                    if (editDialogFragmentCallback != null) {
                        editDialogFragmentCallback.updateEcopro(ecopro);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
